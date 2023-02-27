package application.topkingapi.entity.pricing;

import application.topkingapi.model.Prices;
import application.topkingapi.repo.PricesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingService {
    private final PricesRepo pricesRepo;
    @Autowired
    public PricingService(PricesRepo pricesRepo) {
        this.pricesRepo = pricesRepo;
    }

    public Prices updatePrices(List<Integer> updatedPrices) {
        var priceEntry = pricesRepo.findAll().get(0);
        priceEntry.setBeginner(updatedPrices.get(0));
        priceEntry.setIntermediate(updatedPrices.get(1));
        priceEntry.setElite(updatedPrices.get(2));
        priceEntry.setConsulting(updatedPrices.get(3));
        return pricesRepo.save(priceEntry);
    }

    public Prices getPrices() {
        var prices = pricesRepo.findAll();
        if (prices.isEmpty()) {
            return new Prices(30, 50, 75, 100);
        }
        return prices.get(0);
    }

}
