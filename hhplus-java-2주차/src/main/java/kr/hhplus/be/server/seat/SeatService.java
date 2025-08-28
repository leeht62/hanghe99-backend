package kr.hhplus.be.server.seat;

import kr.hhplus.be.server.reservation.Reservation;
import kr.hhplus.be.server.reservation.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SeatService {

  private final SeatRepository seatRepository;

  public List<Seat> getConfirmSeatList(){
    List<Seat> confirmedReservations =
        seatRepository.findByStatus(SeatStatus.NOT);
    return confirmedReservations;
  }


}
