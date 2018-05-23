package com.synergics.ishom.jualikanid_driver.Model.Object;

/**
 * Created by asmarasusanto on 3/12/18.
 */

public class CustomerDetail {
    private String first_name;
    private String email;
    private String phone;

    public CustomerDetail(String first_name, String email, String phone) {
        this.first_name = first_name;
        this.email = email;
        this.phone = phone;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
