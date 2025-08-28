package kr.hhplus.be.server.seat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@Entity
@Table(name = "seats")
@NoArgsConstructor
public class Seat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer seatNumber;

  @Column(nullable = false)
  private LocalDate date;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SeatStatus status = SeatStatus.NOT;

  public Seat(Integer seatNumber, LocalDate date, SeatStatus status) {
    this.seatNumber = seatNumber;
    this.date = date;
    this.status = status;
  }

}
