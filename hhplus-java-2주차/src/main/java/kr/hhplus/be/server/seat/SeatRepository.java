package kr.hhplus.be.server.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
  Optional<Seat> findBySeatNumber(Integer seatNumber);

  List<Seat> findByStatus(SeatStatus status);

  @Query("SELECT s FROM Seat s WHERE s.date = :date AND s.status = 'NOT'")
  List<Seat> findAvailableSeats(@Param("date") LocalDate date);
}
