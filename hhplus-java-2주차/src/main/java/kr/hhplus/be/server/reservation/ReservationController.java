package kr.hhplus.be.server.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

  private final ReservationService reservationService;

  public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;
  }

  // 좌석 예약 요청
  @PostMapping("/reserve")
  public Reservation reserveSeat(@RequestParam String date,
                                 @RequestParam int seatNumber,
                                 @RequestParam String userId) {
    LocalDate localDate = LocalDate.parse(date);
    return reservationService.reserveSeat(localDate, seatNumber, userId);
  }

  // 결제 완료 시 확정 처리
  @PostMapping("/{reservationId}/confirm")
  public String confirmReservation(@PathVariable Long reservationId) {
    reservationService.confirmReservation(reservationId);
    return "좌석 예약이 확정되었습니다.";
  }

}