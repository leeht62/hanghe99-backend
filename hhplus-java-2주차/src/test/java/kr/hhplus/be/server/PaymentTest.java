package kr.hhplus.be.server;

import kr.hhplus.be.server.payment.PaymentHistory;
import kr.hhplus.be.server.payment.PaymentHistoryRepository;
import kr.hhplus.be.server.payment.PaymentService;
import kr.hhplus.be.server.queue.QueueService;
import kr.hhplus.be.server.reservation.Reservation;
import kr.hhplus.be.server.reservation.ReservationRepository;
import kr.hhplus.be.server.reservation.ReservationStatus;
import kr.hhplus.be.server.seat.Seat;
import kr.hhplus.be.server.seat.SeatStatus;
import kr.hhplus.be.server.user.User;
import kr.hhplus.be.server.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class PaymentTest {
  @InjectMocks
  private PaymentService paymentService;

  @Mock
  private UserRepository userBalanceRepository;

  @Mock
  private PaymentHistoryRepository paymentHistoryRepository;

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private QueueService queueService;

  private UUID userId;
  private Reservation reservation;
  private User balance;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    Seat seat = new Seat(10, LocalDate.now().plusDays(1), SeatStatus.NOT);
    userId = UUID.randomUUID();
    reservation = new Reservation(LocalDate.now().plusDays(1), seat, userId.toString(), ReservationStatus.PENDING);
    balance = new User(userId.toString(), 5000L);
  }

  // 잔액 충전 테스트
  @Test
  void 잔액충전_성공() {
    when(userBalanceRepository.findById(userId.toString())).thenReturn(Optional.of(balance));
    when(userBalanceRepository.save(balance)).thenReturn(balance);

    User updated = paymentService.charge(userId.toString(), 2000L);

    assertThat(updated.getBalance()).isEqualTo(7000L);
  }


  // 잔액 조회 테스트

  @Test
  void 잔액조회_성공() {
    when(userBalanceRepository.findById(userId.toString())).thenReturn(Optional.of(balance));

    long current = paymentService.getBalance(userId.toString());

    assertThat(current).isEqualTo(5000L);
  }

  // 결제 성공 테스트
  @Test
  void 결제_성공() {
    when(userBalanceRepository.findById(userId.toString())).thenReturn(Optional.of(balance));
    when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
    when(userBalanceRepository.save(balance)).thenReturn(balance);
    when(reservationRepository.save(reservation)).thenReturn(reservation);
    when(paymentHistoryRepository.save(any(PaymentHistory.class))).thenAnswer(i -> i.getArguments()[0]);

    PaymentHistory history = paymentService.pay(userId.toString(), reservation.getId(), 3000L);

    assertThat(history.getAmount()).isEqualTo(3000L);
    assertThat(balance.getBalance()).isEqualTo(2000L);
    assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
  }

}
