package com.atpuser.ContractModels.User.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLogin {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("person_id")
    @Expose
    private String personId;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("middlename")
    @Expose
    private String middlename;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("suffix")
    @Expose
    private Object suffix;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("temporary_address")
    @Expose
    private String temporaryAddress;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("civil_status")
    @Expose
    private String civilStatus;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("landline_number")
    @Expose
    private Object landlineNumber;
    @SerializedName("email")
    @Expose
    private Object email;
    @SerializedName("registered_from")
    @Expose
    private String registeredFrom;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("mpin")
    @Expose
    private String mpin;
    @SerializedName("province")
    @Expose
    private String province;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("barangay")
    @Expose
    private String barangay;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

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

    public Object getSuffix() {
        return suffix;
    }

    public void setSuffix(Object suffix) {
        this.suffix = suffix;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTemporaryAddress() {
        return temporaryAddress;
    }

    public void setTemporaryAddress(String temporaryAddress) {
        this.temporaryAddress = temporaryAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Object getLandlineNumber() {
        return landlineNumber;
    }

    public void setLandlineNumber(Object landlineNumber) {
        this.landlineNumber = landlineNumber;
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = email;
    }

    public String getRegisteredFrom() {
        return registeredFrom;
    }

    public void setRegisteredFrom(String registeredFrom) {
        this.registeredFrom = registeredFrom;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }
}
