package com.norouzi.librarymanagementsystem.model;


// Including Header Files.

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.ArrayList;

@Entity
@Data
public class Library {
    @Id
    private Long id;

    private String name;

    @OneToOne// name of library
    public Librarian librarian;                        // object of Librarian (only one)

    private ArrayList<Person> persons;                 // all clerks and borrowers

    @OneToMany
    private ArrayList<Book> booksInLibrary;            // all books in library are here!

    @OneToMany
    private ArrayList<Loan> loans;                     // history of all books which have been issued

    private int book_return_deadline;                   //return deadline after which fine will be generated each day
    private double per_day_fine;

    private int hold_request_expiry;                    //number of days after which a hold request will expire

    @OneToOne
    private HoldRequestOperations holdRequestsOperations = new HoldRequestOperations();

    public Library()   // default cons.
    {
        name = null;
        librarian = null;
        persons = new ArrayList();

        booksInLibrary = new ArrayList();
        loans = new ArrayList();
    }
}