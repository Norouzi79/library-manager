package com.norouzi.librarymanagementsystem.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public class Person {
    @Id
    protected int id;           // ID of every person related to library
    protected String password;  // Password of every person related to library
    protected String name;      // Name of every person related to library
    protected String address;   // Address of every person related to library
    protected int phoneNo;      // PhoneNo of every person related to library

    public Person(String name, String address, int phoneNum)   // para cons.
    {
        password = Integer.toString(id);
        this.name = name;
        this.address = address;
        phoneNo = phoneNum;
    }

    // Printing Info of a Person
    public void printInfo() {
        System.out.println("-----------------------------------------");
        System.out.println("\nThe details are: \n");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Address: " + address);
        System.out.println("Phone No: " + phoneNo + "\n");
    }
}
