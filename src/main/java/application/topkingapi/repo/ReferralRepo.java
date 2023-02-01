package application.topkingapi.repo;

import application.topkingapi.model.Referral;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralRepo extends JpaRepository<Referral, Long> {
}
