package com.morakmorak.morak_back_end.config;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.repository.RedisRepository;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;

public class RedisRepositoryTestImpl<T> implements RedisRepository<T> {
    @Override
    public boolean saveData(String key, T value, Long expire) {
        return true;
    }

    @Override
    public Optional<T> getData(String key, Class<T> classType) {
        return getValue(key, classType);
    }

    @Override
    public boolean deleteData(String key) {
        return true;
    }

    @Override
    public Optional<T> getDataAndDelete(String key, Class<T> classType) {
        return getValue(key, classType);
    }

    private Optional<T> value(String key, Class<T> classType) {
        if (classType.equals(String.class) && key.equals(REDIS_AUTH_KEY_NOT_EMPTY)) return (Optional<T>) Optional.of(EMAIL1);
        if (classType.equals(String.class) && key.equals(REDIS_EMAIL_NOT_EMPTY)) return (Optional<T>) Optional.of(AUTH_KEY);
        if (classType.equals(User.class)) return (Optional<T>) Optional.of(User.builder().build());

        return Optional.empty();
    }

    private Optional<T> getValue(String key, Class<T> classType) {
        if (key.equals(REDIS_AUTH_KEY_EMPTY)) return Optional.empty();

        if (key.equals(REDIS_EMAIL_EMPTY)) return Optional.empty();

        if (key.equals(REDIS_TOKEN_EMPTY)) return Optional.empty();

        return value(key, classType);
    }
}
