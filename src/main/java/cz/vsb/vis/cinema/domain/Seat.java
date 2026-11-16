package cz.vsb.vis.cinema.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private CinemaHall hall;

    private int rowNumber;
    private int seatNumber;

    public String getLabel() {
        return "Řada " + rowNumber + ", Sedadlo " + seatNumber;
    }
}