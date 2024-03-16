package nsu.fit.crackhashworker.config;

import java.net.URI;

public class Constants {
    public static final String HASH_NAME = "MD5";
    public static final URI MANAGER_URI = URI.create(
            "http://crackhash-manager:8080/internal/api/manager/hash/crack/request");
}
