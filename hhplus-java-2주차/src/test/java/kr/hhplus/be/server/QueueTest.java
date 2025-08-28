package kr.hhplus.be.server;

import kr.hhplus.be.server.queue.QueueService;
import kr.hhplus.be.server.queue.QueueToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class QueueTest {
  private QueueService queueService;
  private StringRedisTemplate redisTemplate;
  private ZSetOperations<String, String> zSetOperations;

  @BeforeEach
  void setUp() {
    redisTemplate = Mockito.mock(StringRedisTemplate.class);
    zSetOperations = Mockito.mock(ZSetOperations.class);
    when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

    queueService = new QueueService(redisTemplate);
  }

  @Test
  void 토큰을_발급하면_UUID와_순번이_생성된다() {
    // given
    String userId = "user-123";
    when(zSetOperations.add(anyString(), eq(userId), anyDouble())).thenReturn(true);
    when(zSetOperations.rank(anyString(), eq(userId))).thenReturn(0L); // 첫 번째 대기자

    // when
    QueueToken token = queueService.generateToken(userId);

    // then
    assertThat(token).isNotNull();
    assertThat(token.getUserId()).isEqualTo(userId);
    assertThat(token.getPosition()).isEqualTo(1L); // rank 0 → 순번 1
    assertThat(token.getTokenId()).isNotBlank();
  }

  @Test
  void 유저의_순번을_조회할_수_있다() {
    // given
    String userId = "user-456";
    when(zSetOperations.rank(anyString(), eq(userId))).thenReturn(5L);

    // when
    long position = queueService.getPosition(userId);

    // then
    assertThat(position).isEqualTo(6L); // rank 5 → 순번 6
  }


  @Test
  void 대기열에서_삭제할_수_있다() {
    // given
    String userId = "user-111";

    // when
    queueService.removeFromQueue(userId);

    // then
    verify(zSetOperations, times(1)).remove("user:queue", userId);
  }
}
