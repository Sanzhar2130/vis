package cz.vsb.vis.cinema.repository;

import cz.vsb.vis.cinema.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByQrCodeIgnoreCase(String qrCode);
}