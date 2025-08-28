package kr.hhplus.be.server.queue;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

  private static final String QUEUE_KEY = "user:queue";

  private final StringRedisTemplate redisTemplate;

  public QueueService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  // 토큰 발급
  public QueueToken generateToken(String userId) {
    long score = System.currentTimeMillis();
    redisTemplate.opsForZSet().add(QUEUE_KEY, userId, score);

    Long rank = redisTemplate.opsForZSet().rank(QUEUE_KEY, userId);
    return new QueueToken(userId, rank != null ? rank + 1 : -1);
  }

  // 순번 조회
  public long getPosition(String userId) {
    Long rank = redisTemplate.opsForZSet().rank(QUEUE_KEY, userId);
    return rank != null ? rank + 1 : -1;
  }

  // 활성화 (예: 맨 앞 순번일 때만 통과)
  public boolean isActive(String userId) {
    return getPosition(userId) == 1;
  }

  // 만료/삭제
  public void removeFromQueue(String userId) {
    redisTemplate.opsForZSet().remove(QUEUE_KEY, userId);
  }
}