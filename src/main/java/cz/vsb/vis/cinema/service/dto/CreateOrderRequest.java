package cz.vsb.vis.cinema.service.dto;

import java.util.List;

public record CreateOrderRequest(
        Long showtimeId,
        String email,
        String phone,
        boolean payImmediately, // true = buy, false = booking
        List<SeatSelectionItem> seats
) {}