package com.clinic.dto;

public class PatientProfileResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String pesel;
    private String phone;

    public PatientProfileResponse(Long id, String firstName, String lastName, String pesel, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPesel() {
        return pesel;
    }

    public String getPhone() {
        return phone;
    }
}