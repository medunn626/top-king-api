package application.topkingapi.entity.pricing;

import application.topkingapi.model.Prices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping()
    public ResponseEntity<Prices> updatePrices(@RequestBody Prices updatedPrices) {
        var prices = pricingService.updatePrices(updatedPrices);
        return new ResponseEntity<>(prices, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Prices> getPrices() throws Exception {
        var prices = pricingService.getPrices();
        return new ResponseEntity<>(prices, HttpStatus.OK);
    }
}
