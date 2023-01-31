package application.topkingapi.entity.product;

import application.topkingapi.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("plans")
public class ProductController {
    private final ProductOrchestrator productOrchestrator;

    public ProductController(ProductOrchestrator productOrchestrator) {
        this.productOrchestrator = productOrchestrator;
    }

    @PutMapping("/user/{userId}/plan/{productTier}")
    public ResponseEntity<User> updatePlan(@PathVariable String userId,
                                           @PathVariable String productTier) throws Exception {
        return productOrchestrator.updatePlan(userId, productTier);
    }

}
