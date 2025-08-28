package kr.hhplus.be.server.queue;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

  private final QueueService queueService;

  public QueueController(QueueService queueService) {
    this.queueService = queueService;
  }

  // 토큰 발급 API
  @PostMapping("/token")
  public QueueToken issueToken(@RequestParam String userId) {
    return queueService.generateToken(userId);
  }

  // 대기열 상태 조회
  @GetMapping("/status/{userId}")
  public String getStatus(@PathVariable String userId) {
    long position = queueService.getPosition(userId);
    boolean active = queueService.isActive(userId);
    return "현재 순번: " + position + ", 활성 상태: " + active;
  }

  // 토큰 만료 처리
  @DeleteMapping("/remove/{userId}")
  public String remove(@PathVariable String userId) {
    queueService.removeFromQueue(userId);
    return "대기열에서 제거됨: " + userId;
  }
}