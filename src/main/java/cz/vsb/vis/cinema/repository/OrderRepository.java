package cz.vsb.vis.cinema.repository;

import cz.vsb.vis.cinema.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByReservationCodeIgnoreCase(String reservationCode);
}