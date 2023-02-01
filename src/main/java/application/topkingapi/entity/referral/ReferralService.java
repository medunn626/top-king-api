package application.topkingapi.entity.referral;

import application.topkingapi.model.Referral;
import application.topkingapi.model.User;
import application.topkingapi.repo.ReferralRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferralService {
    private final ReferralRepo referralRepo;
    @Autowired
    public ReferralService(ReferralRepo referralRepo) {
        this.referralRepo = referralRepo;
    }

    public void createReferral(String referralEmail, User affiliate) {
        Referral referralToCreate = new Referral();
        referralToCreate.setEmail(referralEmail);
        referralToCreate.setAffiliateId(affiliate.getId());
        referralRepo.save(referralToCreate);
    }

    public List<Referral> getReferrals() {
        return referralRepo.findAll();
    }
}
