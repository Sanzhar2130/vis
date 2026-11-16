package cz.vsb.vis.cinema.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JsonBackReference
    private Order order;

    @ManyToOne(optional = false)
    private Showtime showtime;

    @ManyToOne(optional = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private TariffType tariff;

    @Column(unique = true)
    private String qrCode;

    private BigDecimal finalPrice;

    @Enumerated(EnumType.STRING)
    private TicketState ticketState;
}