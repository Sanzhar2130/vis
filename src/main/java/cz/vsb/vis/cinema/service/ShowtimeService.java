package cz.vsb.vis.cinema.service;

import cz.vsb.vis.cinema.domain.*;
import cz.vsb.vis.cinema.repository.*;
import cz.vsb.vis.cinema.domain.*;
import cz.vsb.vis.cinema.repository.*;
import cz.vsb.vis.cinema.service.dto.CreateShowtimeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepo;
    private final MovieRepository movieRepo;
    private final CinemaHallRepository hallRepo;
    private final SeatRepository seatRepo;
    private final ShowtimeSeatStateRepository seatStateRepo;

    public List<Showtime> getAllShowtimes() {
        return showtimeRepo.findAll();
    }

    public List<Movie> getAllMovies() {
        return movieRepo.findAll();
    }

    public List<CinemaHall> getAllHalls() {
        return hallRepo.findAll();
    }

    public List<ShowtimeSeatState> getSeatsForShowtime(Long showtimeId) {
        return seatStateRepo.findByShowtimeIdOrderBySeatRowNumberAscSeatSeatNumberAsc(showtimeId);
    }

    @Transactional
    public Showtime scheduleShowtime(CreateShowtimeRequest req) {
        Movie movie = movieRepo.findById(req.movieId())
                .orElseThrow(() -> new IllegalArgumentException("Film nebyl nalezen."));
        CinemaHall hall = hallRepo.findById(req.hallId())
                .orElseThrow(() -> new IllegalArgumentException("Sál nebyl nalezen."));

        LocalDateTime start = req.startTime();
        LocalDateTime end = start.plusMinutes(movie.getRuntimeWithCleaning()); // + 15 min úklid

        // Проверка коллизий в выбранном зале (UC-06, шаг 5)
        List<Showtime> existingInHall = showtimeRepo.findByHallId(hall.getId());
        for (Showtime existing : existingInHall) {
            if (existing.hasCollision(start, end)) {
                throw new IllegalStateException(
                        "Časová kolize v sále " + hall.getHallName() + " s filmem '" +
                                existing.getMovie().getTitle() + "' (" + existing.getStartTime() + " - " + existing.getEndTime() + ")"
                );
            }
        }

        Showtime showtime = showtimeRepo.save(new Showtime(null, movie, hall, start, end, req.basePrice()));

        // Инициализация матрицы кресел в состоянии FREE
        List<Seat> hallSeats = seatRepo.findByHallIdOrderByRowNumberAscSeatNumberAsc(hall.getId());
        for (Seat seat : hallSeats) {
            seatStateRepo.save(new ShowtimeSeatState(null, showtime, seat, SeatState.FREE, null));
        }

        return showtime;
    }
}