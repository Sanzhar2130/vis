package cz.vsb.vis.cinema.service.pricing;

import cz.vsb.vis.cinema.domain.TariffType;
import java.math.BigDecimal;

public interface ITariffStrategy {
    TariffType getType();
    BigDecimal calculatePrice(BigDecimal basePrice);
}