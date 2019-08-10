package com.iqbaal.ruangadvokat.model;

public class Advocate {
    String name, address, gender, birthplace, birthday, status, certificateNumber, advocateCard,
            experience, expertise, phone, email;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getStatus() {
        return status;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public String getAdvocateCard() {
        return advocateCard;
    }

    public String getExperience() {
        return experience;
    }

    public String getExpertise() {
        return expertise;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Advocate(String name, String address, String gender, String birthplace, String birthday, String status, String certificateNumber, String advocateCard, String experience, String expertise, String phone, String email) {
        this.name = name;
        this.address = address;
        this.gender = gender;
        this.birthplace = birthplace;
        this.birthday = birthday;
        this.status = status;
        this.certificateNumber = certificateNumber;
        this.advocateCard = advocateCard;
        this.experience = experience;
        this.expertise = expertise;
        this.phone = phone;
        this.email = email;
    }
}
