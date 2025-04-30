package com.code_space.code_space_editor.code_execution.service;

import com.code_space.code_space_editor.code_execution.config.ExecutionProperties;
import com.code_space.code_space_editor.code_execution.dto.CodeExecutionResult;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeExecutionService {

    private final ExecutionProperties execProps;

    private static final List<String> LANGUAGE_CONFIGS = List.of(
            "java",
            "javascript",
            "python",
            "cpp",
            "c",
            "csharp");

    public CodeExecutionResult executeCode(String code, String language, List<String> args, Integer timeoutSeconds) {
        CodeExecutionResult result = new CodeExecutionResult();
        result.setSuccess(false);

        if (!LANGUAGE_CONFIGS.contains(language.toLowerCase())) {
            result.setErrorMessage("Unsupported language: " + language);
            return result;
        }

        int timeout = timeoutSeconds != null ? Math.min(timeoutSeconds, execProps.getMaxTimeoutSeconds())
                : execProps.getDefaultTimeoutSeconds();

        return executeInContainer(code, language, args, timeout);
    }

    public CodeExecutionResult executeInContainer(String code, String language, List<String> args, int timeoutSeconds) {
        CodeExecutionResult result = new CodeExecutionResult();
        result.setSuccess(false);
        long startTime = System.currentTimeMillis();

        try {
            // Get the appropriate execution strategy based on language
            ExecutionStrategy strategy = getExecutionStrategy(language, code, args);

            // Build the container command
            List<String> command = new ArrayList<>(List.of(
                    "docker", "run", "-i", "--rm",
                    "--memory=" + execProps.getMemoryLimit() + "m",
                    "--cpus=" + execProps.getCpuLimit(),
                    "--network=none",
                    getDockerImage(language)));

            // Add the execution command
            command.addAll(strategy.getCommand());

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            // Write code to STDIN if needed
            if (strategy.requiresStdin()) {
                try (OutputStream os = process.getOutputStream()) {
                    os.write(code.getBytes());
                    os.flush();
                }
            }

            // Read output with timeout
            Future<String> stdoutFuture = readProcessOutputAsync(process.getInputStream());
            Future<String> stderrFuture = readProcessOutputAsync(process.getErrorStream());

            if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new TimeoutException("Execution timed out");
            }

            result.setStdout(stdoutFuture.get(5, TimeUnit.SECONDS));
            result.setStderr(stderrFuture.get(5, TimeUnit.SECONDS));
            result.setExitCode(process.exitValue());
            result.setSuccess(process.exitValue() == 0);

        } catch (TimeoutException e) {
            result.setErrorMessage("Execution timed out");
            result.setStderr("Your code execution exceeded the time limit");
        } catch (Exception e) {
            result.setErrorMessage("Execution error: " + e.getMessage());
            result.setStderr(e.toString());
        } finally {
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    private ExecutionStrategy getExecutionStrategy(String language, String code, List<String> args) {
        String argsStr = args != null ? String.join(" ", args) : "";
        String sanitizedCode = code.replace("'", "'\\''");

        return switch (language.toLowerCase()) {
            case "python" -> code.contains("\n")
                    ? new ExecutionStrategy(
                            List.of("/bin/sh", "-c", "cat > /tmp/script.py && python /tmp/script.py " + argsStr),
                            true)
                    : new ExecutionStrategy(List.of("python", "-c", code, argsStr), false);

            case "javascript" ->
                new ExecutionStrategy(List.of("node", "-e", code, argsStr), false);

            case "java" ->
                new ExecutionStrategy(List.of("/bin/sh", "-c",
                        "echo '" + sanitizedCode + "' > Main.java && " +
                                "javac Main.java && java Main " + argsStr),
                        false);

            case "cpp" ->
                new ExecutionStrategy(List.of("/bin/sh", "-c",
                        "echo '" + sanitizedCode + "' > main.cpp && " +
                                "g++ main.cpp -o program && ./program " + argsStr),
                        false);

            case "c" ->
                new ExecutionStrategy(List.of("/bin/sh", "-c",
                        "echo '" + sanitizedCode + "' > main.c && " +
                                "gcc main.c -o program && ./program " + argsStr),
                        false);

            case "csharp" ->
                new ExecutionStrategy(List.of("/bin/sh", "-c",
                        "echo '" + sanitizedCode + "' > Program.cs && " +
                                "dotnet new console -o . --force && " +
                                "dotnet run " + argsStr),
                        false);

            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        };
    }

    private String getDockerImage(String language) {
        return switch (language.toLowerCase()) {
            case "java" -> "openjdk:17";
            case "javascript" -> "node:16";
            case "python" -> "python:3.9";
            case "cpp", "c" -> "gcc:11";
            case "csharp" -> "mcr.microsoft.com/dotnet/sdk:6.0";
            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        };
    }

    private Future<String> readProcessOutputAsync(InputStream is) {
        return CompletableFuture.supplyAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    if (output.length() > execProps.getMaxOutputSize()) {
                        output.append("\n... Output truncated ...");
                        break;
                    }
                }
                return output.toString();
            } catch (IOException e) {
                return "Error reading output: " + e.getMessage();
            }
        });
    }

    // Helper class to encapsulate execution strategy
    private static class ExecutionStrategy {
        private final List<String> command;
        private final boolean requiresStdin;

        public ExecutionStrategy(List<String> command, boolean requiresStdin) {
            this.command = command;
            this.requiresStdin = requiresStdin;
        }

        public List<String> getCommand() {
            return command;
        }

        public boolean requiresStdin() {
            return requiresStdin;
        }
    }
}