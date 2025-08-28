package kr.hhplus.be.server.payment;

import kr.hhplus.be.server.user.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  // 잔액 충전
  @PostMapping("/charge")
  public User charge(@RequestParam String userId,
                     @RequestParam long amount) {
    return paymentService.charge(userId, amount);
  }

  // 잔액 조회
  @GetMapping("/balance/{userId}")
  public long getBalance(@PathVariable String userId) {
    return paymentService.getBalance(userId);
  }

  // 결제
  @PostMapping("/pay")
  public PaymentHistory pay(@RequestParam String userId,
                            @RequestParam Long reservationId,
                            @RequestParam long amount) {
    return paymentService.pay(userId, reservationId, amount);
  }
}