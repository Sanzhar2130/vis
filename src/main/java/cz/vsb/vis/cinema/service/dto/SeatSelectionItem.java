package cz.vsb.vis.cinema.service.dto;

import cz.vsb.vis.cinema.domain.TariffType;

public record SeatSelectionItem(
        Long seatId,
        TariffType tariff
) {}