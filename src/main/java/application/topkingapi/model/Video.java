package application.topkingapi.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
public class Video implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String docName;
    private String docType;
    private List<String> productTiersAppliedTo;
    @Lob
    private byte[] data;

    public Video(){}

    public Video(String docName,
                 String docType,
                 byte[] data,
                 List<String> productTiersAppliedTo) {
        this.docName = docName;
        this.docType = docType;
        this.data = data;
        this.productTiersAppliedTo = productTiersAppliedTo;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocName() {
        return this.docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocType() {
        return this.docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public List<String> getProductTiersAppliedTo() {
        return this.productTiersAppliedTo;
    }

    public void setProductTiersAppliedTo(List<String> productTiersAppliedTo) {
        this.productTiersAppliedTo = productTiersAppliedTo;
    }
}
