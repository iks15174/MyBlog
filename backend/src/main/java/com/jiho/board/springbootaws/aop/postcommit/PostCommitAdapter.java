package com.jiho.board.springbootaws.aop.postcommit;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class PostCommitAdapter implements TransactionSynchronization {
    private static final ThreadLocal<List<Runnable>> RUNNABLE = new ThreadLocal<>();

    // register a new runnable for post commit execution
    public void execute(Runnable runnable) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            List<Runnable> runnables = RUNNABLE.get();
            if (runnables == null) {
                runnables = new ArrayList<>();
                RUNNABLE.set(runnables);
                TransactionSynchronizationManager.registerSynchronization(this);
            }
            return;
        }
        // if transaction synchronisation is not active
        runnable.run();
    }

    @Override
    public void afterCommit() {
        List<Runnable> runnables = RUNNABLE.get();
        runnables.forEach(Runnable::run);
    }

    @Override
    public void afterCompletion(int status) {
        RUNNABLE.remove();
    }
}
