package com.example.przychodnia_mobile;

public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String pesel;
    private String phone;

    public RegisterRequest(String firstName, String lastName, String email, String password, String pesel, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.pesel = pesel;
        this.phone = phone;
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPesel() { return pesel; }
    public void setPesel(String pesel) { this.pesel = pesel; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}