package brachy84.brachydium.api.handlers.storage;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class ListStorage<T> implements Storage<T> {

    private final List<Storage<T>> parts = new ArrayList<>();

    public ListStorage(Storage<T>... storages) {
        for(Storage<T> storage : storages) {
            if(storage == null)
                throw new NullPointerException("Storage is null");
            parts.add(storage);
        }
    }

    @Override
    public boolean supportsInsertion() {
        for (Storage<T> part : parts) {
            if (part.supportsInsertion()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;

        for (Storage<T> part : parts) {
            amount += part.insert(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public boolean supportsExtraction() {
        for (Storage<T> part : parts) {
            if (part.supportsExtraction()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;

        for (Storage<T> part : parts) {
            amount += part.extract(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public Iterator<StorageView<T>> iterator(TransactionContext transaction) {
        return new ListIterator(transaction);
    }

    /**
     * The combined iterator for multiple storages.
     */
    private class ListIterator implements Iterator<StorageView<T>>, Transaction.CloseCallback {
        boolean open = true;
        final TransactionContext transaction;
        final Iterator<Storage<T>> partIterator = parts.iterator();
        // Always holds the next StorageView<T>, except during next() while the iterator is being advanced.
        Iterator<StorageView<T>> currentPartIterator = null;

        ListIterator(TransactionContext transaction) {
            this.transaction = transaction;
            advanceCurrentPartIterator();
            transaction.addCloseCallback(this);
        }

        @Override
        public boolean hasNext() {
            return open && currentPartIterator != null && currentPartIterator.hasNext();
        }

        @Override
        public StorageView<T> next() {
            if (!open) {
                throw new NoSuchElementException("The transaction for this iterator was closed.");
            }

            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            StorageView<T> returned = currentPartIterator.next();

            // Advance the current part iterator
            if (!currentPartIterator.hasNext()) {
                advanceCurrentPartIterator();
            }

            return returned;
        }

        private void advanceCurrentPartIterator() {
            while (partIterator.hasNext()) {
                this.currentPartIterator = partIterator.next().iterator(transaction);

                if (this.currentPartIterator.hasNext()) {
                    break;
                }
            }
        }

        @Override
        public void onClose(TransactionContext transaction, Transaction.Result result) {
            // As soon as the transaction is closed, this iterator is not valid anymore.
            open = false;
        }
    }
}
