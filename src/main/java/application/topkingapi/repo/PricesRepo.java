package application.topkingapi.repo;

import application.topkingapi.model.Prices;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricesRepo extends JpaRepository<Prices, Long> {
}
