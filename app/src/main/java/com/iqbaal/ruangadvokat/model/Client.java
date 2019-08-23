package com.iqbaal.ruangadvokat.model;

public class Client {
    String name, gender, birthday, nik, phone, email;

    public Client(String name, String gender, String birthday, String nik, String phone, String email) {
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.nik = nik;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getNik() {
        return nik;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
