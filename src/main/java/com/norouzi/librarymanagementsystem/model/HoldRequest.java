package com.norouzi.librarymanagementsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class HoldRequest {
    @Id
    private Long id;

    @OneToOne
    private Borrower borrower;

    @OneToOne
    private Book book;
    Date requestDate;

    public HoldRequest(Borrower bor, Book b, Date reqDate)  // para cons.
    {
        borrower = bor;
        book = b;
        requestDate = reqDate;
    }

    protected HoldRequest() {
    }
}
