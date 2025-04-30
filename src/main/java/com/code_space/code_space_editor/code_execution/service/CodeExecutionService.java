package com.code_space.code_space_editor.code_execution.service;

import com.code_space.code_space_editor.code_execution.config.ExecutionProperties;
import com.code_space.code_space_editor.code_execution.dto.CodeExecutionResult;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
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

        if (!LANGUAGE_CONFIGS.contains(language.toLowerCase())) {
            result.setErrorMessage("Unsupported language: " + language);
            return result;
        }

        long startTime = System.currentTimeMillis();
        Path tempDir = null;

        try {
            // Create temporary directory for code files
            tempDir = Files.createTempDirectory("docker_execution_");
            String fileName = getMainFileName(language);
            Path codeFile = tempDir.resolve(fileName);
            Files.writeString(codeFile, code);

            Files.setPosixFilePermissions(codeFile, Set.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OTHERS_READ));

            // Create a script to run the code based on language
            String dockerCommand = getDockerCommand(language, fileName);

            // Build Docker command
            List<String> command = new ArrayList<>();
            command.addAll(List.of(
                    "docker", "run", "--rm",
                    // Set resource limits
                    "--memory=" + execProps.getMemoryLimit() + "m",
                    "--cpus=" + execProps.getCpuLimit(),
                    // Set timeout
                    "--stop-timeout=" + timeoutSeconds,
                    // Network isolation
                    "--network=none",
                    // Mount code directory
                    "-v", tempDir.toAbsolutePath() + ":/app",
                    // Set working directory
                    "-w", "/app",
                    // Image to use
                    getDockerImage(language),
                    // Command to run
                    "/bin/sh", "-c", dockerCommand));

            // Add arguments if provided
            if (args != null && !args.isEmpty()) {
                String argsStr = String.join(" ", args);
                // Escape the arguments
                argsStr = argsStr.replace("\"", "\\\"");
                // Update the command to include args
                command.set(command.size() - 1, dockerCommand + " " + argsStr);
            }

            // Execute Docker command
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            // Read output with timeout
            Future<String> stdoutFuture = readProcessOutputAsync(process.getInputStream());
            Future<String> stderrFuture = readProcessOutputAsync(process.getErrorStream());

            if (!process.waitFor(timeoutSeconds + 5, TimeUnit.SECONDS)) {
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
            // Cleanup
            deleteDirectory(tempDir);
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    private String getMainFileName(String language) {
        return switch (language) {
            case "java" -> "Main.java";
            case "javascript" -> "main.js";
            case "python" -> "main.py";
            case "cpp" -> "main.cpp";
            case "c" -> "main.c";
            case "csharp" -> "Program.cs";
            case "go" -> "main.go";
            case "ruby" -> "main.rb";
            case "php" -> "index.php";
            case "rust" -> "main.rs";
            default -> "main";
        };
    }

    private void deleteDirectory(Path dir) {
        if (dir == null || !Files.exists(dir))
            return;

        try {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // Log and ignore
                            System.err.println("Failed to delete: " + path);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Failed to clean up directory: " + dir);
        }
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

    private String getDockerCommand(String language, String fileName) {
        return switch (language) {
            case "java" -> "javac " + fileName + " && java " + fileName.replace(".java", "");
            case "javascript" -> "node " + fileName;
            case "python" -> "python " + fileName;
            case "cpp" -> "g++ " + fileName + " -o program && ./program";
            case "c" -> "gcc " + fileName + " -o program && ./program";
            case "csharp" -> "dotnet new console -o . --force && " +
                    "rm -f Program.cs && " +
                    "cp " + fileName + " Program.cs && " +
                    "dotnet run";
            case "go" -> "go run " + fileName;
            case "ruby" -> "ruby " + fileName;
            case "php" -> "php " + fileName;
            case "rust" -> "rustc " + fileName + " -o program && ./program";
            default -> "echo 'Unsupported language'";
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
