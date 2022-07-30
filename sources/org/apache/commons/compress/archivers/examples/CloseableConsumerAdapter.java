package org.apache.commons.compress.archivers.examples;

import java.io.Closeable;
import java.io.IOException;

final class CloseableConsumerAdapter implements Closeable {
    private Closeable closeable;
    private final CloseableConsumer consumer;

    CloseableConsumerAdapter(CloseableConsumer consumer2) {
        if (consumer2 != null) {
            this.consumer = consumer2;
            return;
        }
        throw new NullPointerException("consumer must not be null");
    }

    /* access modifiers changed from: package-private */
    public <C extends Closeable> C track(C closeable2) {
        this.closeable = closeable2;
        return closeable2;
    }

    public void close() throws IOException {
        Closeable closeable2 = this.closeable;
        if (closeable2 != null) {
            this.consumer.accept(closeable2);
        }
    }
}
