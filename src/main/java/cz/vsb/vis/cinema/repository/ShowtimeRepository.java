package cz.vsb.vis.cinema.repository;

import cz.vsb.vis.cinema.domain.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByHallId(Long hallId);
}