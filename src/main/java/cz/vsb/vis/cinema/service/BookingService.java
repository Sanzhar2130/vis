package cz.vsb.vis.cinema.service;


import cz.vsb.vis.cinema.domain.*;
import cz.vsb.vis.cinema.domain.*;
import cz.vsb.vis.cinema.repository.*;
import cz.vsb.vis.cinema.service.dto.*;
import cz.vsb.vis.cinema.service.pricing.ITariffStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ShowtimeRepository showtimeRepo;
    private final ShowtimeSeatStateRepository seatStateRepo;
    private final OrderRepository orderRepo;
    private final List<ITariffStrategy> tariffStrategies; // Паттерн GoF Strategy

    // Временная блокировка кресла в корзине на 10 минут (UC-02)
    @Transactional
    public ShowtimeSeatState toggleSeatLock(Long showtimeId, Long seatId) {
        ShowtimeSeatState state = seatStateRepo.findByShowtimeIdAndSeatId(showtimeId, seatId)
                .orElseThrow(() -> new IllegalArgumentException("Sedadlo nebylo nalezeno."));

        if (state.getState() == SeatState.FREE) {
            state.setState(SeatState.BLOCKED);
            state.setLockExpirationTime(LocalDateTime.now().plusMinutes(10));
        } else if (state.getState() == SeatState.BLOCKED) {
            state.setState(SeatState.FREE);
            state.setLockExpirationTime(null);
        } else {
            throw new IllegalStateException("Sedadlo je již rezervováno nebo prodáno.");
        }
        return seatStateRepo.save(state);
    }

    // Создание брони (UC-02) или прямая покупка онлайн (UC-03)
    @Transactional
    public Order createOrder(CreateOrderRequest req) {
        if (req.seats() == null || req.seats().isEmpty()) {
            throw new IllegalArgumentException("Musíte vybrat alespoň jedno sedadlo.");
        }

        Showtime showtime = showtimeRepo.findById(req.showtimeId())
                .orElseThrow(() -> new IllegalArgumentException("Promítání nebylo nalezeno."));

        // Проверка ограничения: бронировать без оплаты можно только если до сеанса > 30 минут
        if (!req.payImmediately() && showtime.getStartTime().isBefore(LocalDateTime.now().plusMinutes(30))) {
            throw new IllegalStateException("Rezervace bez platby je možná nejpozději 30 minut před začátkem.");
        }

        Order order = new Order();
        order.setContactEmail(req.email());
        order.setContactPhone(req.phone());
        order.setReservationCode(generate6CharCode());
        order.setPaymentStatus(req.payImmediately() ? "PAID" : "RESERVED");

        for (SeatSelectionItem item : req.seats()) {
            ShowtimeSeatState seatState = seatStateRepo.findByShowtimeIdAndSeatId(showtime.getId(), item.seatId())
                    .orElseThrow(() -> new IllegalArgumentException("Sedadlo neexistuje."));

            if (seatState.getState() == SeatState.RESERVED || seatState.getState() == SeatState.SOLD) {
                throw new IllegalStateException("Sedadlo " + seatState.getSeat().getLabel() + " je již obsazené.");
            }

            // Применяем паттерн Strategy для расчета цены
            BigDecimal finalPrice = resolveStrategy(item.tariff()).calculatePrice(showtime.getBasePrice());

            // Меняем состояние кресла
            seatState.setState(req.payImmediately() ? SeatState.SOLD : SeatState.RESERVED);
            seatState.setLockExpirationTime(null);
            seatStateRepo.save(seatState);

            // Создаем билет
            Ticket ticket = new Ticket();
            ticket.setShowtime(showtime);
            ticket.setSeat(seatState.getSeat());
            ticket.setTariff(item.tariff());
            ticket.setFinalPrice(finalPrice);
            ticket.setQrCode("QR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            ticket.setTicketState(TicketState.SOLD);

            order.addTicket(ticket);
        }

        return orderRepo.save(order);
    }

    // Поиск брони по 6-значному коду для кассира (UC-04)
    public Order findOrderByCode(String code) {
        return orderRepo.findByReservationCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new IllegalArgumentException("Rezervace s kódem '" + code + "' nebyla nalezena."));
    }

    // Подтверждение оплаты брони кассиром (UC-04)
    @Transactional
    public Order confirmCashPayment(String code) {
        Order order = findOrderByCode(code);
        if ("PAID".equals(order.getPaymentStatus())) {
            throw new IllegalStateException("Objednávka je již zaplacena.");
        }
        order.setPaymentStatus("PAID");
        for (Ticket t : order.getTickets()) {
            ShowtimeSeatState seatState = seatStateRepo
                    .findByShowtimeIdAndSeatId(t.getShowtime().getId(), t.getSeat().getId())
                    .orElseThrow();
            seatState.setState(SeatState.SOLD);
            seatStateRepo.save(seatState);
        }
        return orderRepo.save(order);
    }

    // Фоновый системный таймер (UC-09): раз в минуту снимает просроченные 10-мин корзины
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        for (ShowtimeSeatState state : seatStateRepo.findAll()) {
            if (state.getState() == SeatState.BLOCKED && state.getLockExpirationTime() != null
                    && state.getLockExpirationTime().isBefore(now)) {
                state.setState(SeatState.FREE);
                state.setLockExpirationTime(null);
                seatStateRepo.save(state);
            }
        }
    }

    private ITariffStrategy resolveStrategy(TariffType type) {
        TariffType target = (type != null) ? type : TariffType.STANDARD;
        return tariffStrategies.stream()
                .filter(s -> s.getType() == target)
                .findFirst()
                .orElseThrow();
    }

    private String generate6CharCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}