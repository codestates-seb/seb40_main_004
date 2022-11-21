package com.morakmorak.morak_back_end.repository.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl<T> implements RedisRepository<T> {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public boolean saveData(String key, T value, Long expire) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue()
                    .set(key, json, expire, MILLISECONDS);
            return true;
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public Optional<T> getData(String key, Class<T> classType) {
        String json = redisTemplate.opsForValue()
                .get(key);

        if (json == null) {
            return Optional.empty();
        }

        return getOptionalData(classType, json);
    }

    public boolean deleteData(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public Optional<T> getDataAndDelete(String key, Class<T> classType) {
        String json = redisTemplate.opsForValue()
                .getAndDelete(key);

        if (json == null) {
            return Optional.empty();
        }

        return getOptionalData(classType, json);
    }

    private Optional<T> getOptionalData(Class<T> classType, String json) {
        try {
            return Optional.of(objectMapper.readValue(json, classType));
        } catch (IOException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }
}
