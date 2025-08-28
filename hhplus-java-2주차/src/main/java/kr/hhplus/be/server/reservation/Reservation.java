package kr.hhplus.be.server.reservation;

import jakarta.persistence.*;
import kr.hhplus.be.server.seat.Seat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate date;

  private String userId;

  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_id", nullable = false) // FK
  private Seat seat;

  public Reservation(LocalDate date, Seat seat, String userId, ReservationStatus status) {
    this.date = date;
    this.seat= seat;
    this.userId = userId;
    this.status = status;
  }

  public void confirm() {
    this.status = ReservationStatus.CONFIRMED;
  }

  public void expire() {
    this.status = ReservationStatus.EXPIRED;
  }
}