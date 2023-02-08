package application.topkingapi.entity.referral;

import application.topkingapi.model.Referral;
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

    public void createReferral(Referral referral) {
        Referral referralToCreate = new Referral();
        referralToCreate.setEmail(referral.getEmail());
        referralToCreate.setPaymentMethod(referral.getPaymentMethod());
        referralToCreate.setPaymentHandle(referral.getPaymentHandle());
        referralToCreate.setAffiliateId(referral.getAffiliateId());
        referralRepo.save(referralToCreate);
    }

    public List<Referral> getReferrals() {
        return referralRepo.findAll();
    }
}
