package kr.hhplus.be.server.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PaymentHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String userId;
  private Long reservationId;
  private long amount;
  private LocalDateTime paidAt;

  public PaymentHistory(String userId, Long reservationId, long amount) {
    this.userId = userId;
    this.reservationId = reservationId;
    this.amount = amount;
    this.paidAt = LocalDateTime.now();
  }
}
