package cz.vsb.vis.cinema.repository;

import cz.vsb.vis.cinema.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {}