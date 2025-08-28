package kr.hhplus.be.server.queue;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class QueueToken {
  private String tokenId;   // 랜덤 토큰 (UUID)
  private String userId;    // 유저 UUID
  private long position;    // 대기열 순번
  private long issuedAt;    // 발급 시간

  public QueueToken(String userId, long position) {
    this.tokenId = UUID.randomUUID().toString();
    this.userId = userId;
    this.position = position;
    this.issuedAt = System.currentTimeMillis();
  }
}