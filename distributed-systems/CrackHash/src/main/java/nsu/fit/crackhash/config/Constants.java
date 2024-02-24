package nsu.fit.crackhash.config;

public class Constants {
    public static final String MANAGER_BASE_API_PATH = "/api/hash";
    public static final String WORKER_TASK_URL = "/internal/api/worker/hash/crack/task";
    public static final String WORKER_TO_MANAGER_URL = "/internal/api/manager/hash/crack/request";
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"; // ^[a-z0-9]+$
    public static final int SEQ_MAX_LEN = 10;
    public static final long TASK_TIMEOUT_MILLIS = 7200000; // 2 hours
    public static final long CHECK_PERIOD_MILLIS = 600000; // 10 minutes
}
