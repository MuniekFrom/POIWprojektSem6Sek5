package com.example.przychodnia_mobile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PatientResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String pesel;
    private String phone;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonProperty("firstName")
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    @JsonProperty("lastName")
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPesel() { return pesel; }
    public void setPesel(String pesel) { this.pesel = pesel; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}