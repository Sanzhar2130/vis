package cz.vsb.vis.cinema.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Movie movie;

    @ManyToOne(optional = false)
    private CinemaHall hall;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal basePrice;

    public boolean hasCollision(LocalDateTime newStart, LocalDateTime newEnd) {
        return newStart.isBefore(this.endTime) && newEnd.isAfter(this.startTime);
    }
}