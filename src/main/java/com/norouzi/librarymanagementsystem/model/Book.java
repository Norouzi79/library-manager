package com.norouzi.librarymanagementsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Book {
    @Id
    private int bookID;           // ID given by a library to a book to make it distinguishable from other books
    private String title;         // Title of a book
    private String subject;       // Subject to which a book is related!
    private String author;        // Author of book!
    private Boolean isIssued;    // this will be true if the book is currently issued to some borrower.

    @OneToOne
    private HoldRequestOperations holdRequestsOperations = new HoldRequestOperations();

    protected Book() {
    }


    public Book(String t, String s, String a, boolean issued)    // Parameterise cons.
    {
        title = t;
        subject = s;
        author = a;
        isIssued = issued;

    }
}
