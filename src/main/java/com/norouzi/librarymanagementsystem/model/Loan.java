package com.norouzi.librarymanagementsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Loan {
    @Id
    private Long id;
    private Borrower borrower;
    private Book book;

    private Staff issuer;
    private Date issuedDate;

    private Date dateReturned;
    private Staff receiver;

    private boolean finePaid;

    public Loan(Borrower bor, Book b, Staff i, Staff r, Date iDate, Date rDate, boolean fPaid)  // Para cons.
    {
        borrower = bor;
        book = b;
        issuer = i;
        receiver = r;
        issuedDate = iDate;
        dateReturned = rDate;

        finePaid = fPaid;
    }

    protected Loan() {

    }
}