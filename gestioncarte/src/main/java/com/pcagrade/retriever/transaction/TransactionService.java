package com.pcagrade.retriever.transaction;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Service
public class TransactionService implements Executor {

    @Autowired
    private PlatformTransactionManager transactionManager;

    public void asyncExecuteInTransaction(Runnable action) throws TransactionException {
        executeInTransaction(r -> new Thread(r).start(), action);
    }

    public void executeInTransaction(Executor executor, Runnable action) throws TransactionException {
        executor.execute(() -> executeInTransaction(action));
    }

    public void executeInTransaction(Runnable action) throws TransactionException {
        executeInTransaction(s -> {
            action.run();
            return null;
        });
    }

    public <T> T executeInTransaction(TransactionCallback<T> action) throws TransactionException {
        return new TransactionTemplate(transactionManager).execute(action);
    }

    public <T> void parallelForEach(Collection<T> collection, Consumer<T> action) throws TransactionException {
        collection.parallelStream().forEach(o -> executeInTransaction(() -> action.accept(o)));
    }

    @Override
    public void execute(@Nonnull Runnable command) {
        executeInTransaction(command);
    }
}
