package cz.vsb.vis.cinema.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReportRowDto(
        Long showtimeId,
        String movieTitle,
        String hallName,
        LocalDateTime startTime,
        long soldTickets,
        int totalCapacity,
        double occupancyPercent,
        BigDecimal revenue
) {}