package com.atpuser.ContractModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserRegisterRequest {
    @SerializedName("firstname")
    @Expose
    public String firstname;

    @SerializedName("middlename")
    @Expose
    public String middlename;

    @SerializedName("lastname")
    @Expose
    public String lastname;

    @SerializedName("suffix")
    @Expose
    public String suffix;

    @SerializedName("date_of_birth")
    @Expose
    public String date_of_birth;

    @SerializedName("gender")
    @Expose
    public String gender;


    @SerializedName("barangay")
    @Expose
    public String barangay;


    @SerializedName("civil_status")
    @Expose
    public String civil_status;

    @SerializedName("mobile_number")
    @Expose
    public String phone_number;

    @SerializedName("landline_number")
    @Expose
    public String landline_number;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("registered_from")
    @Expose
    public String registered_from;


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getCivil_status() {
        return civil_status;
    }

    public void setCivil_status(String civil_status) {
        this.civil_status = civil_status;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getLandline_number() {
        return landline_number;
    }

    public void setLandline_number(String landline_number) {
        this.landline_number = landline_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistered_from() {
        return registered_from;
    }

    public void setRegistered_from(String registered_from) {
        this.registered_from = registered_from;
    }
}
