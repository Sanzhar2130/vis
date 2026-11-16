package cz.vsb.vis.cinema.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateShowtimeRequest(
        Long movieId,
        Long hallId,
        LocalDateTime startTime,
        BigDecimal basePrice
) {}