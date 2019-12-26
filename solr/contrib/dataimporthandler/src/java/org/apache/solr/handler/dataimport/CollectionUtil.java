package org.apache.solr.handler.dataimport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CollectionUtil {

    private CollectionUtil() {
        // never invoked
    }

    public static <T> Iterator<CompletableFuture<T>> completableFutureIterator(final Iterator<T> iter) {
        return new Iterator<CompletableFuture<T>>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public CompletableFuture<T> next() {
                return CompletableFuture.completedFuture(iter.next());
            }
        };
    }

    public static <T> Collection<T> resolveAll(Iterator<CompletableFuture<T>> iter) {
        List<CompletableFuture<T>> list = new ArrayList<>();
        while (iter.hasNext()) {
            list.add((iter.next()));
        }
        CompletableFuture<?>[] array = list.toArray(new CompletableFuture<?>[list.size()]);
        CompletableFuture.allOf(array);
        List<T> result = new ArrayList<>(list.size());
        for (CompletableFuture<T> completed : list) {
            try {
                result.add(completed.get());
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } catch (ExecutionException e) {
                throw new IllegalStateException(e);
            }
        }
        return result;
    }
}
