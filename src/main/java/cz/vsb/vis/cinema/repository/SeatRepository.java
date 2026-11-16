package cz.vsb.vis.cinema.repository;

import cz.vsb.vis.cinema.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByHallIdOrderByRowNumberAscSeatNumberAsc(Long hallId);
}