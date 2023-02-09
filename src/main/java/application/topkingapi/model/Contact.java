package application.topkingapi.model;

import java.io.Serializable;

public class Contact implements Serializable {
    private String name;
    private String email;
    private String message;

    public Contact(){}

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
