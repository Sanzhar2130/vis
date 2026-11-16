package cz.vsb.vis.cinema.repository;

import cz.vsb.vis.cinema.domain.ShowtimeSeatState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ShowtimeSeatStateRepository extends JpaRepository<ShowtimeSeatState, Long> {
    List<ShowtimeSeatState> findByShowtimeIdOrderBySeatRowNumberAscSeatSeatNumberAsc(Long showtimeId);
    Optional<ShowtimeSeatState> findByShowtimeIdAndSeatId(Long showtimeId, Long seatId);
}