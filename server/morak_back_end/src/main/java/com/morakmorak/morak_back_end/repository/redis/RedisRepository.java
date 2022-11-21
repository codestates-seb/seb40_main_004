package com.morakmorak.morak_back_end.repository.redis;

import java.util.Optional;

public interface RedisRepository <T> {

    boolean saveData(String key, T value, Long expire);

    Optional<T> getData(String key, Class<T> classType);

    boolean deleteData(String key);

    Optional<T> getDataAndDelete(String key, Class<T> classType);
}
