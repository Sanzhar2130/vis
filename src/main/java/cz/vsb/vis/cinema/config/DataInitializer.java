package cz.vsb.vis.cinema.config;

import cz.vsb.vis.cinema.domain.*;
import cz.vsb.vis.cinema.repository.*;
import cz.vsb.vis.cinema.domain.*;
import cz.vsb.vis.cinema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepo;
    private final CinemaHallRepository hallRepo;
    private final SeatRepository seatRepo;
    private final ShowtimeRepository showtimeRepo;
    private final ShowtimeSeatStateRepository seatStateRepo;

    @Override
    public void run(String... args) {
        // 1. Создаем 2 зала и генерируем кресла
        CinemaHall sal1 = createHallWithSeats("Sál 1", 5, 8);
        CinemaHall sal2 = createHallWithSeats("Sál 2", 4, 6);

        // 2. Создаем 3 фильма
        Movie dune = movieRepo.save(new Movie(null, "Duna: Část druhá", "Sci-Fi", 166, 12));
        Movie interstellar = movieRepo.save(new Movie(null, "Interstellar", "Sci-Fi", 169, 12));
        Movie laLaLand = movieRepo.save(new Movie(null, "La La Land", "Muzikál", 128, 12));

        // 3. Создаем 2 сеанса на завтра
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0);
        createShowtimeWithSeats(dune, sal1, tomorrow, new BigDecimal("200"));
        createShowtimeWithSeats(laLaLand, sal2, tomorrow.withHour(20), new BigDecimal("180"));
    }

    private CinemaHall createHallWithSeats(String name, int rows, int seatsPerRow) {
        CinemaHall hall = hallRepo.save(new CinemaHall(null, name, rows, seatsPerRow));
        for (int r = 1; r <= rows; r++) {
            for (int s = 1; s <= seatsPerRow; s++) {
                seatRepo.save(new Seat(null, hall, r, s));
            }
        }
        return hall;
    }

    private void createShowtimeWithSeats(Movie movie, CinemaHall hall, LocalDateTime start, BigDecimal price) {
        LocalDateTime end = start.plusMinutes(movie.getRuntimeWithCleaning());
        Showtime showtime = showtimeRepo.save(new Showtime(null, movie, hall, start, end, price));

        List<Seat> seats = seatRepo.findByHallIdOrderByRowNumberAscSeatNumberAsc(hall.getId());
        for (Seat seat : seats) {
            seatStateRepo.save(new ShowtimeSeatState(null, showtime, seat, SeatState.FREE, null));
        }
    }
}