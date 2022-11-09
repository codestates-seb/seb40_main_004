package com.morakmorak.morak_back_end.config;

import com.morakmorak.morak_back_end.exception.EmbeddedRedisServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static com.morakmorak.morak_back_end.exception.ErrorCode.*;


@Profile("local")
public class EmbeddedRedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void setRedisServer() {
        if (isRunning(executeGrepProcessCommend(redisPort))) {
            redisPort = findAvailablePort();
        }

        if (isArmMac()) {
            redisServer = new RedisServer(Objects.requireNonNull(getRedisFileForArmMac()), redisPort);
        } else {
            redisServer = new RedisServer(redisPort);
        }

        redisServer.start();
    }

    private File getRedisFileForArmMac() {
        try {
        return new ClassPathResource("binary/redis/redis-server-6.0.10-mac-arm64").getFile();
        } catch (IOException e) {
            throw new EmbeddedRedisServerException(EMBEDDED_REDIS_EXCEPTION.getMessage(), e);
        }
    }

    private boolean isArmMac() {
        return Objects.equals(System.getProperty("os.arch"), "aarch64") &&
                Objects.equals(System.getProperty("os.name"), "Mac OS X");
    }

    private int findAvailablePort() {
        for (int port = 10000; port <= 65535; port++) {
            Process process = executeGrepProcessCommend(port);

            if (!isRunning(process)) {
                return port;
            }
        }

        throw new EmbeddedRedisServerException(NOT_FOUND_AVAILABLE_PORT.getMessage());
    }

    private Process executeGrepProcessCommend(int port) {
        try {
            String command = String.format("netstat -nat | grep LISTEN|grep%d", port);
            String[] shell = {"/bin/sh", "-c", command};
            return Runtime.getRuntime().exec(shell);
        } catch (IOException e) {
            throw new EmbeddedRedisServerException(CAN_NOT_EXECUTE_GREP.getMessage(), e);
        }
    }

    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (IOException e) {
            throw new EmbeddedRedisServerException(CAN_NOT_EXECUTE_REDIS_SERVER.getMessage(), e);
        }

        return StringUtils.hasText(pidInfo.toString());
    }
}
