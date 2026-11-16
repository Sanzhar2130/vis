package cz.vsb.vis.cinema.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaHall {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hallName;
    private int rowCount;
    private int seatsPerRow;

    public int getTotalCapacity() {
        return rowCount * seatsPerRow;
    }
}