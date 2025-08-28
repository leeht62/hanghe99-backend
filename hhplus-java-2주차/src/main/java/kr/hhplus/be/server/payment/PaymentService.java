package kr.hhplus.be.server.payment;

import kr.hhplus.be.server.queue.QueueService;
import kr.hhplus.be.server.reservation.Reservation;
import kr.hhplus.be.server.reservation.ReservationRepository;
import kr.hhplus.be.server.reservation.ReservationStatus;
import kr.hhplus.be.server.user.User;
import kr.hhplus.be.server.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

  private final UserRepository userBalanceRepository;
  private final PaymentHistoryRepository paymentHistoryRepository;
  private final ReservationRepository reservationRepository;
  private final QueueService queueService;

  public PaymentService(UserRepository userBalanceRepository,
                        PaymentHistoryRepository paymentHistoryRepository,
                        ReservationRepository reservationRepository,
                        QueueService queueService) {
    this.userBalanceRepository = userBalanceRepository;
    this.paymentHistoryRepository = paymentHistoryRepository;
    this.reservationRepository = reservationRepository;
    this.queueService = queueService;
  }

  // 잔액 충전
  @Transactional
  public User charge(String userId, long amount) {
    User balance = userBalanceRepository.findById(userId)
        .orElse(new User(userId, 0));
    balance.addBalance(amount);
    return userBalanceRepository.save(balance);
  }

  // 잔액 조회
  public long getBalance(String userId) {
    return userBalanceRepository.findById(userId)
        .map(User::getBalance)
        .orElse(0L);
  }

  // 결제
  @Transactional
  public PaymentHistory pay(String userId, Long reservationId, long amount) {
    User balance = userBalanceRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자 잔액 정보 없음"));
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("예약 내역 없음"));

    if (reservation.getStatus() != ReservationStatus.PENDING) {
      throw new IllegalStateException("예약이 이미 확정되었거나 만료됨");
    }

    balance.deduct(amount);
    userBalanceRepository.save(balance);

    reservation.confirm();
    reservationRepository.save(reservation);

    PaymentHistory history = new PaymentHistory(userId, reservationId, amount);
    paymentHistoryRepository.save(history);

    // 대기열 토큰 만료
    queueService.removeFromQueue(userId);

    return history;
  }
}
