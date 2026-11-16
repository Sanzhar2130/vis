package cz.vsb.vis.cinema.service;

import cz.vsb.vis.cinema.domain.Ticket;
import cz.vsb.vis.cinema.domain.TicketState;
import cz.vsb.vis.cinema.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketValidationService {

    private final TicketRepository ticketRepo;

    @Transactional
    public String validateAndUseTicket(String qrCode) {
        Ticket ticket = ticketRepo.findByQrCodeIgnoreCase(qrCode.trim())
                .orElseThrow(() -> new IllegalArgumentException("Neplatný QR kód vstupenky!"));

        if (!"PAID".equals(ticket.getOrder().getPaymentStatus())) {
            throw new IllegalStateException("Vstupenka nebyla uhrazena (pouze rezervace)!");
        }

        if (ticket.getTicketState() == TicketState.USED) {
            throw new IllegalStateException("VSTUP ZAMÍTNUT: Vstupenka již byla jednou uplatněna!");
        }

        if (ticket.getTicketState() == TicketState.CANCELLED) {
            throw new IllegalStateException("VSTUP ZAMÍTNUT: Vstupenka byla stornována!");
        }

        ticket.setTicketState(TicketState.USED);
        ticketRepo.save(ticket);

        return String.format("VSTUP POVOLEN | Film: %s | %s | %s (%s)",
                ticket.getShowtime().getMovie().getTitle(),
                ticket.getShowtime().getHall().getHallName(),
                ticket.getSeat().getLabel(),
                ticket.getTariff());
    }
}