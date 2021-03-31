package org.quantdirect.platform;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class QdLoggerHandler extends Handler {
    private static QdLoggerHandler h;
    private final int max;
    private final Queue<LogRecord> rec;

    private QdLoggerHandler() {
        max = 1000;
        rec = new ConcurrentLinkedQueue<>();
    }

    public static synchronized QdLoggerHandler instance() {
        if (h == null) {
            h = new QdLoggerHandler();
        }
        return h;
    }

    public Queue<LogRecord> takeAfter(Instant instant) {
        synchronized (rec) {
            boolean found = false;
            var q = new LinkedList<LogRecord>();
            var i = rec.iterator();
            LogRecord r = null;
            while (i.hasNext() && !found) {
                r = i.next();
                found = r.getInstant().isAfter(instant);
            }
            q.add(r);
            while (i.hasNext()) {
                q.add(i.next());
            }
            return q;
        }
    }

    @Override
    public void publish(LogRecord record) {
        try {
            synchronized (rec) {
                rec.add(record);
            }
        } catch (IllegalStateException exception) {
            outdate();
            rec.add(record);
        } finally {
            outdate();
        }
    }

    private void outdate() {
        synchronized (rec) {
            while (rec.size() > max) {
                rec.remove();
            }
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
        rec.clear();
    }
}
