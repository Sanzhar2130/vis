package cz.vsb.vis.cinema.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String reservationCode; // 6-значный код брони

    private String contactEmail;
    private String contactPhone;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String paymentStatus; // RESERVED, PAID, CANCELLED
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Ticket> tickets = new ArrayList<>();

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setOrder(this);
        this.totalAmount = this.totalAmount.add(ticket.getFinalPrice());
    }
}