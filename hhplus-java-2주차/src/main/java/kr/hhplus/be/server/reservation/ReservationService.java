package kr.hhplus.be.server.reservation;

import kr.hhplus.be.server.seat.Seat;
import kr.hhplus.be.server.seat.SeatRepository;
import kr.hhplus.be.server.seat.SeatStatus;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final SeatRepository seatRepository;
  private final StringRedisTemplate redisTemplate;

  private static final int TOTAL_SEATS = 50;
  private static final long HOLD_TIME_MINUTES = 5;

  public ReservationService(ReservationRepository reservationRepository,
                            StringRedisTemplate redisTemplate,
                            SeatRepository seatRepository) {
    this.reservationRepository = reservationRepository;
    this.redisTemplate = redisTemplate;
    this.seatRepository=seatRepository;
  }

  @Transactional
  public Reservation reserveSeat(LocalDate date, int seatNumber, String userId) {
    if (seatNumber < 1 || seatNumber > TOTAL_SEATS) {
      throw new IllegalArgumentException("좌석 번호는 1~50 사이여야 합니다.");
    }

    String redisKey = "seat:" + date + ":" + seatNumber;

    // Redis를 이용해 동시성 제어 (임시 배정)
    Boolean success = redisTemplate.opsForValue()
        .setIfAbsent(redisKey, userId, HOLD_TIME_MINUTES, TimeUnit.MINUTES);

    if (Boolean.FALSE.equals(success)) {
      throw new DuplicateKeyException("이미 다른 사용자가 임시 배정한 좌석입니다.");
    }

    Seat seat = seatRepository.findBySeatNumber(seatNumber)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));
    seat.setStatus(SeatStatus.RESERVE);
    // DB 저장 (상태 = PENDING)
    Reservation reservation = new Reservation(date, seat, userId, ReservationStatus.PENDING);
    return reservationRepository.save(reservation);
  }

  // 결제 성공 시 확정
  @Transactional
  public void confirmReservation(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("예약 내역이 없습니다."));

    reservation.confirm();
    reservationRepository.save(reservation);

    // Redis 키 제거
    String redisKey = "seat:" + reservation.getDate() + ":" + reservation.getSeat().getSeatNumber();
    redisTemplate.delete(redisKey);
  }

  public List<LocalDate> getAvailableDates() {
    LocalDate today = LocalDate.now();
    return IntStream.rangeClosed(1, 30)
        .mapToObj(today::plusDays)
        .collect(Collectors.toList());
  }


    //특정 날짜의 예약 가능한 좌석 조회

  public List<Seat> getAvailableSeats(LocalDate date) {
    return seatRepository.findAvailableSeats(date);
  }


}
