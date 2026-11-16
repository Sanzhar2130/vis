package cz.vsb.vis.cinema.service.pricing;

import cz.vsb.vis.cinema.domain.TariffType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ChildTariff implements ITariffStrategy {
    @Override
    public TariffType getType() { return TariffType.CHILD; }

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.60")).setScale(0, RoundingMode.HALF_UP); // sleva 40 %
    }
}