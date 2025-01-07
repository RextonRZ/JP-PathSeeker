package com.example.MAD;

import java.io.Serializable;

public class RateUser implements Serializable {
    private String rateUser;  // Renamed for better clarity

    // Constructor
    public RateUser(String rateUser) {
        this.rateUser = rateUser;
    }

    // Getter
    public String getRateUser() {
        return rateUser;
    }

    // Setter
    public void setRateUser(String rateUser) {
        this.rateUser = rateUser;
    }
}
