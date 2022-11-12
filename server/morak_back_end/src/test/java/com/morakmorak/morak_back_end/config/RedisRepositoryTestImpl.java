package com.morakmorak.morak_back_end.config;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.repository.RedisRepository;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;

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

    private Optional<T> value(Class<T> classType) {
        if (classType.equals(String.class)) return (Optional<T>) Optional.of("EMAIL");
        if (classType.equals(User.class)) return (Optional<T>) Optional.of(User.builder().build());

        return Optional.empty();
    }

    private Optional<T> getValue(String key, Class<T> classType) {
        if (key.equals(REDIS_AUTH_KEY_EMPTY)) return Optional.empty();

        if (key.equals(REDIS_EMAIL_EMPTY)) return Optional.empty();

        if (key.equals(REDIS_TOKEN_EMPTY)) return Optional.empty();

        return value(classType);
    }
}
