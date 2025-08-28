package kr.hhplus.be.server.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
  @Id
  private String userId; // 사용자 식별자
  private long balance;  // 잔액

  public User(String userId, long balance) {
    this.userId = userId;
    this.balance = balance;
  }
  public void addBalance(long amount) {
    this.balance += amount;
  }

  public void deduct(long amount) {
    if (balance < amount) {
      throw new IllegalArgumentException("잔액 부족");
    }
    this.balance -= amount;
  }


}
