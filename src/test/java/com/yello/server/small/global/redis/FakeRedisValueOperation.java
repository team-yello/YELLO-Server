package com.yello.server.small.global.redis;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

public class FakeRedisValueOperation implements ValueOperations<Long, ServiceTokenVO> {

  private final HashMap<Long, ServiceTokenVO> data = new HashMap<>();

  @Override
  public void set(Long key, ServiceTokenVO value) {
    data.put(key, value);
  }

  @Override
  public void set(Long key, ServiceTokenVO value, long timeout, TimeUnit unit) {

  }

  @Override
  public Boolean setIfAbsent(Long key, ServiceTokenVO value) {
    return null;
  }

  @Override
  public Boolean setIfAbsent(Long key, ServiceTokenVO value, long timeout, TimeUnit unit) {
    return null;
  }

  @Override
  public Boolean setIfPresent(Long key, ServiceTokenVO value) {
    return null;
  }

  @Override
  public Boolean setIfPresent(Long key, ServiceTokenVO value, long timeout, TimeUnit unit) {
    return null;
  }

  @Override
  public void multiSet(Map<? extends Long, ? extends ServiceTokenVO> map) {

  }

  @Override
  public Boolean multiSetIfAbsent(Map<? extends Long, ? extends ServiceTokenVO> map) {
    return null;
  }

  @Override
  public ServiceTokenVO get(Object key) {
    return data.get(Long.parseLong(key.toString()));
  }

  @Override
  public ServiceTokenVO getAndDelete(Long key) {
    return null;
  }

  @Override
  public ServiceTokenVO getAndExpire(Long key, long timeout, TimeUnit unit) {
    return null;
  }

  @Override
  public ServiceTokenVO getAndExpire(Long key, Duration timeout) {
    return null;
  }

  @Override
  public ServiceTokenVO getAndPersist(Long key) {
    return null;
  }

  @Override
  public ServiceTokenVO getAndSet(Long key, ServiceTokenVO value) {
    return null;
  }

  @Override
  public List<ServiceTokenVO> multiGet(Collection<Long> keys) {
    return null;
  }

  @Override
  public Long increment(Long key) {
    return null;
  }

  @Override
  public Long increment(Long key, long delta) {
    return null;
  }

  @Override
  public Double increment(Long key, double delta) {
    return null;
  }

  @Override
  public Long decrement(Long key) {
    return null;
  }

  @Override
  public Long decrement(Long key, long delta) {
    return null;
  }

  @Override
  public Integer append(Long key, String value) {
    return null;
  }

  @Override
  public String get(Long key, long start, long end) {
    return null;
  }

  @Override
  public void set(Long key, ServiceTokenVO value, long offset) {

  }

  @Override
  public Long size(Long key) {
    return null;
  }

  @Override
  public Boolean setBit(Long key, long offset, boolean value) {
    return null;
  }

  @Override
  public Boolean getBit(Long key, long offset) {
    return null;
  }

  @Override
  public List<Long> bitField(Long key, BitFieldSubCommands subCommands) {
    return null;
  }

  @Override
  public RedisOperations<Long, ServiceTokenVO> getOperations() {
    return null;
  }
}
