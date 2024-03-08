package nsu.fit.crackhash.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CrackHashTask {
    @Getter
    private final long taskStartTime;

    private final AtomicInteger completionCount;
    private final List<String> words = new ArrayList<>();
    private WorkStatus status;

    public CrackHashTask(int workerCount) {
        this.completionCount = new AtomicInteger(workerCount);
        status = WorkStatus.IN_PROGRESS;
        taskStartTime = System.currentTimeMillis();
    }

    public synchronized void addWords(List<String> words) {
        this.words.addAll(words);
        completionCount.decrementAndGet();
        if (completionCount.get() == 0) {
            status = WorkStatus.READY;
        }
    }

    public synchronized List<String> getWords() {
        if (status == WorkStatus.READY) {
            return words;
        }
        return null;
    }

    public synchronized WorkStatus getStatus() {
        return status;
    }

    public void setTimeoutExpired() {
        status = WorkStatus.ERROR;
    }
}
