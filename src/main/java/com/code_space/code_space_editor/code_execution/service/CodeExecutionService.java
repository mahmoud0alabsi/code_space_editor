package com.code_space.code_space_editor.code_execution.service;

import com.code_space.code_space_editor.code_execution.config.ExecutionProperties;
import com.code_space.code_space_editor.code_execution.dto.CodeExecutionResult;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
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
            "csharp",
            "go",
            "ruby",
            "php",
            "rust");

    public CodeExecutionResult executeCode(String code, String language, List<String> args, Integer timeoutSeconds) {
        CodeExecutionResult result = new CodeExecutionResult();
        result.setSuccess(false);

        if (!LANGUAGE_CONFIGS.contains(language.toLowerCase())) {
            result.setErrorMessage("Unsupported language: " + language);
            return result;
        }

        // Apply system limits or use defaults
        int timeout = timeoutSeconds != null ? Math.min(timeoutSeconds, execProps.getMaxTimeoutSeconds())
                : execProps.getDefaultTimeoutSeconds();

        return executeInDocker(code, language, args, timeout);
    }

    public CodeExecutionResult executeInDocker(String code, String language, List<String> args, int timeoutSeconds) {
        CodeExecutionResult result = new CodeExecutionResult();
        result.setSuccess(false);

        long startTime = System.currentTimeMillis();

        try {
            String executionCommand = getDirectExecutionCommand(language, code, args);

            List<String> command = new ArrayList<>();
            command.addAll(List.of(
                    "docker", "run", "-i", "--rm",
                    "--memory=" + execProps.getMemoryLimit() + "m",
                    "--cpus=" + execProps.getCpuLimit(),
                    "--network=none",
                    getDockerImage(language),
                    "/bin/sh", "-c", executionCommand));

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            if (language.equalsIgnoreCase("python") && code.contains("\n")) {
                try (OutputStream os = process.getOutputStream()) {
                    os.write(code.getBytes());
                    os.flush();
                }
            }

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
        } catch (IOException | InterruptedException | ExecutionException e) {
            result.setErrorMessage("Execution error: " + e.getMessage());
            result.setStderr(e.toString());
        } finally {
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    private String getDirectExecutionCommand(String language, String code, List<String> args) {
        String sanitizedCode = code.replace("'", "'\\''");
        String argsStr = args != null ? String.join(" ", args) : "";

        return switch (language.toLowerCase()) {
            case "python" ->
                code.contains("\n")
                        ? "cat > /tmp/script.py && python /tmp/script.py " + argsStr
                        : "python -c '" + sanitizedCode + "' " + argsStr;

            case "javascript" ->
                "node -e '" + sanitizedCode + "' " + argsStr;

            case "java" ->
                "echo '" + sanitizedCode + "' > Main.java && " +
                        "javac Main.java && java Main " + argsStr;

            case "cpp" ->
                "echo '" + sanitizedCode + "' > main.cpp && " +
                        "g++ main.cpp -o program && ./program " + argsStr;

            case "c" ->
                "echo '" + sanitizedCode + "' > main.c && " +
                        "gcc main.c -o program && ./program " + argsStr;

            case "csharp" ->
                "echo '" + sanitizedCode + "' > Program.cs && " +
                        "dotnet new console -o . --force && " +
                        "dotnet run " + argsStr;

            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        };
    }

    private String getDockerImage(String language) {
        return switch (language) {
            case "java" -> "openjdk:17";
            case "javascript" -> "node:16";
            case "python" -> "python:3.9";
            case "cpp", "c" -> "gcc:11";
            case "csharp" -> "mcr.microsoft.com/dotnet/sdk:6.0";
            case "go" -> "golang:1.18";
            case "ruby" -> "ruby:3.1";
            case "php" -> "php:8.1-cli";
            case "rust" -> "rust:1.60";
            default -> "ubuntu:latest";
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
                        output.append("\n... Output truncated (exceeded maximum size) ...");
                        break;
                    }
                }
                return output.toString();
            } catch (IOException e) {
                return "Error reading process output: " + e.getMessage();
            }
        });
    }

}
