package cz.vsb.vis.cinema.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vsb.vis.cinema.domain.*;
import cz.vsb.vis.cinema.repository.*;
import cz.vsb.vis.cinema.service.dto.ReportRowDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ShowtimeRepository showtimeRepo;
    private final TicketRepository ticketRepo;
    private final ObjectMapper objectMapper; // Встроенный в Spring сериализатор JSON

    public List<ReportRowDto> generateReport() {
        List<Ticket> allTickets = ticketRepo.findAll();
        List<ReportRowDto> rows = new ArrayList<>();

        for (Showtime s : showtimeRepo.findAll()) {
            List<Ticket> paidTickets = allTickets.stream()
                    .filter(t -> t.getShowtime().getId().equals(s.getId()))
                    .filter(t -> "PAID".equals(t.getOrder().getPaymentStatus()))
                    .toList();

            long soldCount = paidTickets.size();
            int capacity = s.getHall().getTotalCapacity();
            double occupancy = capacity > 0 ? Math.round((soldCount * 1000.0) / capacity) / 10.0 : 0.0;
            BigDecimal revenue = paidTickets.stream()
                    .map(Ticket::getFinalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            rows.add(new ReportRowDto(
                    s.getId(),
                    s.getMovie().getTitle(),
                    s.getHall().getHallName(),
                    s.getStartTime(),
                    soldCount,
                    capacity,
                    occupancy,
                    revenue
            ));
        }
        return rows;
    }

    // 2-й способ хранения данных: сохранение отчета в файл report.json на диске
    public File exportToJsonFile() throws Exception {
        List<ReportRowDto> data = generateReport();
        File file = new File("report.json");
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        return file;
    }

    // 2-й способ хранения данных: сохранение отчета в файл report.csv на диске
    public File exportToCsvFile() throws Exception {
        List<ReportRowDto> data = generateReport();
        File file = new File("report.csv");
        try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
            writer.println("ShowtimeID;Film;Sal;Zacatek;ProdaneListky;Kapacita;VytizeniPercent;TrzbyCZK");
            for (ReportRowDto r : data) {
                writer.printf("%d;%s;%s;%s;%d;%d;%.1f;%s%n",
                        r.showtimeId(), r.movieTitle(), r.hallName(), r.startTime(),
                        r.soldTickets(), r.totalCapacity(), r.occupancyPercent(), r.revenue());
            }
        }
        return file;
    }
}