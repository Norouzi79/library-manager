package com.norouzi.librarymanagementsystem.service;

import com.norouzi.librarymanagementsystem.model.Clerk;

public class ClerkService {
    public void printInfo(Clerk clerk) {
        System.out.println("-----------------------------------------");
        System.out.println("\nThe details are: \n");
        System.out.println("ID: " + clerk.getId());
        System.out.println("Name: " + clerk.getName());
        System.out.println("Address: " + clerk.getAddress());
        System.out.println("Phone No: " + clerk.getPhoneNo() + "\n");
        System.out.println("Salary: " + clerk.getSalary() + "\n");
        System.out.println("Desk Number: " + clerk.getDeskNo());
    }
}
