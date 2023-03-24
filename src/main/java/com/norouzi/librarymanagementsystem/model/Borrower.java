package com.norouzi.librarymanagementsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Entity
public class Borrower extends Person {
    @OneToMany
    private ArrayList<Loan> borrowedBooks;          //Those books which are currently borrowed by this borrower

    @OneToMany
    private ArrayList<HoldRequest> onHoldBooks;  //Those books which are currently requested by this borrower to be on hold


    public Borrower(String name, String address, int phoneNum) // para. cons
    {
        super(name, address, phoneNum);

        borrowedBooks = new ArrayList();
        onHoldBooks = new ArrayList();
    }

    protected Borrower() {
        super(null, null, 0);
    }
}
