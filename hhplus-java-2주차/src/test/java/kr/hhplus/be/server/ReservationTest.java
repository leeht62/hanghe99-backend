package kr.hhplus.be.server;

import kr.hhplus.be.server.reservation.Reservation;
import kr.hhplus.be.server.reservation.ReservationRepository;
import kr.hhplus.be.server.reservation.ReservationService;
import kr.hhplus.be.server.reservation.ReservationStatus;
import kr.hhplus.be.server.seat.Seat;
import kr.hhplus.be.server.seat.SeatRepository;
import kr.hhplus.be.server.seat.SeatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class ReservationTest {
  @InjectMocks
  private ReservationService reservationService;

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private SeatRepository seatRepository;

  @Mock
  private StringRedisTemplate redisTemplate;

  @Mock
  private ValueOperations<String, String> valueOperations;

  public ReservationTest() {
    MockitoAnnotations.openMocks(this);
  }

  @BeforeEach
  void setUp() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
        .thenReturn(true);
  }
  @Test
  void 예약가능한_날짜목록_조회() {
    List<LocalDate> dates = reservationService.getAvailableDates();

    assertThat(dates).isNotEmpty();
    assertThat(dates).hasSize(30);
    assertThat(dates).contains(LocalDate.now().plusDays(1));
  }

  @Test
  void 특정_날짜의_예약가능_좌석예약() {

    LocalDate dates = LocalDate.now();

    List<Seat> allSeats = new ArrayList<>();
    for (int i = 1; i <= 50; i++) {
      Seat seat = new Seat();
      seat.setSeatNumber(i);
      seat.setDate(dates);
      seat.setStatus(SeatStatus.NOT);
      allSeats.add(seat);
    }
    when(seatRepository.findAvailableSeats(dates)).thenReturn(allSeats);

    int seatNumber = 5;
    String userId = "user-123";

    Seat seat = new Seat();
    seat.setSeatNumber(seatNumber);
    seat.setDate(dates);
    seat.setStatus(SeatStatus.NOT);


    when(seatRepository.findBySeatNumber(seatNumber)).thenReturn(Optional.of(seat));

    Reservation reservation = new Reservation(dates, seat, userId, ReservationStatus.PENDING);
    when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
    when(reservationRepository.findByDate(dates)).thenReturn(List.of(reservation));

    reservationService.reserveSeat(dates, seatNumber, userId);

    Seat seatToReserve = allSeats.stream()
        .filter(s -> s.getSeatNumber() == seatNumber)
        .findFirst()
        .get();
    seatToReserve.setStatus(SeatStatus.RESERVE);

    when(seatRepository.findAvailableSeats(dates)).thenAnswer(invocation ->
        allSeats.stream()
            .filter(s -> s.getStatus() == SeatStatus.NOT)
            .collect(Collectors.toList())
    );


    List<Seat> availableSeats = reservationService.getAvailableSeats(dates);


    assertThat(availableSeats).hasSize(49);
  }



}
