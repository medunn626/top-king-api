package application.topkingapi.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Referral implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String email;
    private Long affiliateId;

    public Referral(){}

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAffiliateId() {
        return this.affiliateId;
    }

    public void setAffiliateId(Long affiliateId) {
        this.affiliateId = affiliateId;
    }
}
