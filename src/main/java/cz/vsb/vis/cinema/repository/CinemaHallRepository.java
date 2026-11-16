package cz.vsb.vis.cinema.repository;

import cz.vsb.vis.cinema.domain.CinemaHall;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CinemaHallRepository extends JpaRepository<CinemaHall, Long> {}