package com.example.gsb_mobile_app;

import java.io.Serializable;

public class User implements Serializable {
    public String status;
    private final String user_id;
    private final String kilometer_costs_id;
    private final String role_id;
    private final String first_name;
    private final String last_name;
    private final String email;

    public User(String user_id, String kilometer_costs_id, String role_id, String first_name, String last_name, String email, String status) {
        this.user_id = user_id;
        this.kilometer_costs_id = kilometer_costs_id;
        this.role_id = role_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.status = status;
    }

    public String getUserId() {
        return user_id;
    }

    public String getKilometerCostsId() {
        return kilometer_costs_id;
    }

    public String getRoleId() {
        return role_id;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }
}




