package application.topkingapi.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Prices implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private Integer beginner;
    private Integer intermediate;
    private Integer elite;
    private Integer consulting;

    public Prices(){}

    public Prices(Integer beginner, Integer intermediate, Integer elite, Integer consulting){
        this.beginner = beginner;
        this.intermediate = intermediate;
        this.elite = elite;
        this.consulting = consulting;
    }

    public Integer getBeginner() {
        return beginner;
    }

    public void setBeginner(Integer beginner) {
        this.beginner = beginner;
    }

    public Integer getIntermediate() {
        return intermediate;
    }

    public void setIntermediate(Integer intermediate) {
        this.intermediate = intermediate;
    }

    public Integer getElite() {
        return elite;
    }

    public void setElite(Integer elite) {
        this.elite = elite;
    }

    public Integer getConsulting() {
        return consulting;
    }

    public void setConsulting(Integer consulting) {
        this.consulting = consulting;
    }
}
