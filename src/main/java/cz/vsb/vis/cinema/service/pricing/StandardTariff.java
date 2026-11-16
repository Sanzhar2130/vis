package cz.vsb.vis.cinema.service.pricing;

import cz.vsb.vis.cinema.domain.TariffType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class StandardTariff implements ITariffStrategy {
    @Override
    public TariffType getType() {
        return TariffType.STANDARD;
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice; // 100 %
    }
}