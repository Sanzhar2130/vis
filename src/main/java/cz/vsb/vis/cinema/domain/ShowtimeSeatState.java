package cz.vsb.vis.cinema.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeSeatState {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Showtime showtime;

    @ManyToOne(optional = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private SeatState state = SeatState.FREE;

    private LocalDateTime lockExpirationTime;
}