package application.topkingapi.entity.pricing;

import application.topkingapi.model.Prices;
import application.topkingapi.repo.PricesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class PricingService {
    private final PricesRepo pricesRepo;
    @Autowired
    public PricingService(PricesRepo pricesRepo) {
        this.pricesRepo = pricesRepo;
    }

    public Prices updatePrices(Prices updatedPrices) {
        var currentPrices = pricesRepo.findAll();
        if (CollectionUtils.isEmpty(currentPrices)) {
            var newPrice = new Prices(
                    updatedPrices.getBeginner(),
                    updatedPrices.getIntermediate(),
                    updatedPrices.getElite(),
                    updatedPrices.getConsulting(),
                    updatedPrices.getAnnualPrices()
            );
            return pricesRepo.save(newPrice);
        }
        var priceEntry = currentPrices.get(0);
        priceEntry.setBeginner(updatedPrices.getBeginner());
        priceEntry.setIntermediate(updatedPrices.getIntermediate());
        priceEntry.setElite(updatedPrices.getElite());
        priceEntry.setConsulting(updatedPrices.getConsulting());
        priceEntry.setAnnualPrices(updatedPrices.getAnnualPrices());
        return pricesRepo.save(priceEntry);
    }

    public Prices getPrices() {
        var prices = pricesRepo.findAll();
        if (prices.isEmpty()) {
            return new Prices(30, 50, 75, 100, List.of());
        }
        return prices.get(0);
    }

}
