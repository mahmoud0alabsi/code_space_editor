package com.code_space.code_space_editor.project_managment.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;

@Service
public class ConcurrencyService {
    private final ConcurrentHashMap<Long, ReentrantLock> branchLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ReentrantReadWriteLock> fileLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantReadWriteLock> filePathLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ReentrantLock> projectLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ReentrantLock> commitLocks = new ConcurrentHashMap<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // Branch lock operations
    public void lockBranch(Long branchId) {
        ReentrantLock lock = branchLocks.computeIfAbsent(branchId, k -> new ReentrantLock());
        lock.lock();
    }

    public void unlockBranch(Long branchId) {
        ReentrantLock lock = branchLocks.get(branchId);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    // File lock operations
    public void lockFileWrite(Long fileId) {
        ReentrantReadWriteLock lock = fileLocks.computeIfAbsent(fileId, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
    }

    public void unlockFileWrite(Long fileId) {
        ReentrantReadWriteLock lock = fileLocks.get(fileId);
        if (lock != null && lock.writeLock().isHeldByCurrentThread()) {
            lock.writeLock().unlock();
        }
    }

    public void lockFileRead(Long fileId) {
        ReentrantReadWriteLock lock = fileLocks.computeIfAbsent(fileId, k -> new ReentrantReadWriteLock());
        lock.readLock().lock();
    }

    public void unlockFileRead(Long fileId) {
        ReentrantReadWriteLock lock = fileLocks.get(fileId);
        if (lock != null) {
            lock.readLock().unlock();
        }
    }

    // File path lock operations
    public void lockFilePathWrite(String filePath) {
        ReentrantReadWriteLock lock = filePathLocks.computeIfAbsent(filePath, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
    }

    public void unlockFilePathWrite(String filePath) {
        ReentrantReadWriteLock lock = filePathLocks.get(filePath);
        if (lock != null && lock.writeLock().isHeldByCurrentThread()) {
            lock.writeLock().unlock();
        }
    }

    public void lockFilePathRead(String filePath) {
        ReentrantReadWriteLock lock = filePathLocks.computeIfAbsent(filePath, k -> new ReentrantReadWriteLock());
        lock.readLock().lock();
    }

    public void unlockFilePathRead(String filePath) {
        ReentrantReadWriteLock lock = filePathLocks.get(filePath);
        if (lock != null) {
            lock.readLock().unlock();
        }
    }

    // Project lock operations
    public void lockProject(Long projectId) {
        ReentrantLock lock = projectLocks.computeIfAbsent(projectId, k -> new ReentrantLock());
        lock.lock();
    }

    public void unlockProject(Long projectId) {
        ReentrantLock lock = projectLocks.get(projectId);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    // Commit lock operations
    public void lockCommit(Long commitId) {
        ReentrantLock lock = commitLocks.computeIfAbsent(commitId, k -> new ReentrantLock());
        lock.lock();
    }

    public void unlockCommit(Long commitId) {
        ReentrantLock lock = commitLocks.get(commitId);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    // Executor service
    public ExecutorService getExecutorService() {
        return executorService;
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}