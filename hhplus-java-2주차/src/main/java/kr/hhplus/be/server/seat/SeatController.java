package kr.hhplus.be.server.seat;

import kr.hhplus.be.server.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/seat")
public class SeatController {

  private final SeatService seatService;

  @GetMapping("/findallseat")
  public List<Seat> findAllReserveSeat(){
    return seatService.getConfirmSeatList();
  }
}
