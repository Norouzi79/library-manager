package com.norouzi.librarymanagementsystem.service;

import com.norouzi.librarymanagementsystem.model.Borrower;
import com.norouzi.librarymanagementsystem.model.HoldRequest;
import com.norouzi.librarymanagementsystem.model.Loan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

@Service
@AllArgsConstructor
public class BorrowerService {
    private final BookService bookService;

    // Printing Borrower's Info
    public void printInfo(Borrower borrower) {
        System.out.println("-----------------------------------------");
        System.out.println("\nThe details are: \n");
        System.out.println("ID: " + borrower.getId());
        System.out.println("Name: " + borrower.getName());
        System.out.println("Address: " + borrower.getAddress());
        System.out.println("Phone No: " + borrower.getPhoneNo() + "\n");

        printBorrowedBooks(borrower);
        printOnHoldBooks(borrower);
    }

    // Printing Book's Info Borrowed by Borrower
    public void printBorrowedBooks(Borrower borrower) {
        if (!borrower.getBorrowedBooks().isEmpty()) {
            System.out.println("\nBorrowed Books are: ");

            System.out.println("------------------------------------------------------------------------------");
            System.out.println("No.\t\tTitle\t\t\tAuthor\t\t\tSubject");
            System.out.println("------------------------------------------------------------------------------");

            for (int i = 0; i < borrower.getBorrowedBooks().size(); i++) {
                System.out.print(i + "-" + "\t\t");
                bookService.printInfo(borrower.getBorrowedBooks().get(i).getBook());
                System.out.print("\n");
            }
        } else
            System.out.println("\nNo borrowed books.");
    }

    // Printing Book's Info kept on Hold by Borrower
    public void printOnHoldBooks(Borrower borrower) {
        if (!borrower.getOnHoldBooks().isEmpty()) {
            System.out.println("\nOn Hold Books are: ");

            System.out.println("------------------------------------------------------------------------------");
            System.out.println("No.\t\tTitle\t\t\tAuthor\t\t\tSubject");
            System.out.println("------------------------------------------------------------------------------");

            for (int i = 0; i < borrower.getOnHoldBooks().size(); i++) {
                System.out.print(i + "-" + "\t\t");
                bookService.printInfo(borrower.getOnHoldBooks().get(i).getBook());
                System.out.print("\n");
            }
        } else
            System.out.println("\nNo On Hold books.");
    }

    // Updating Borrower's Info
    public void updateBorrowerInfo(Borrower borrower) throws IOException {
        String choice;

        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        System.out.println("\nDo you want to update " + borrower.getName() + "'s Name ? (y/n)");
        choice = sc.next();

        updateBorrowerName(borrower, choice, reader);


        System.out.println("\nDo you want to update " + borrower.getName() + "'s Address ? (y/n)");
        choice = sc.next();

        updateBorrowerAddress(borrower, choice, reader);

        System.out.println("\nDo you want to update " + borrower.getName() + "'s Phone Number ? (y/n)");
        choice = sc.next();

        updateBorrowerPhoneNumber(borrower, choice, sc);

        System.out.println("\nBorrower is successfully updated.");
    }

    private void updateBorrowerPhoneNumber(Borrower borrower, String choice, Scanner sc) {
        if (choice.equals("y")) {
            System.out.println("\nType New Phone Number: ");
            borrower.setPhoneNo(sc.nextInt());
            System.out.println("\nThe phone number is successfully updated.");
        }
    }

    private void updateBorrowerAddress(Borrower borrower, String choice, BufferedReader reader) throws IOException {
        if (choice.equals("y")) {
            System.out.println("\nType New Address: ");
            borrower.setAddress(reader.readLine());
            System.out.println("\nThe address is successfully updated.");
        }
    }

    private void updateBorrowerName(Borrower borrower, String choice, BufferedReader reader) throws IOException {
        if (choice.equals("y")) {
            System.out.println("\nType New Name: ");
            borrower.setName(reader.readLine());
            System.out.println("\nThe name is successfully updated.");
        }
    }

    /*-- Adding and Removing from Borrowed Books---*/
    public void addBorrowedBook(Borrower borrower, Loan iBook) {
        borrower.getBorrowedBooks().add(iBook);
    }

    public void removeBorrowedBook(Borrower borrower, Loan iBook) {
        borrower.getBorrowedBooks().remove(iBook);
    }

    /*-------------------------------------------*/

    /*-- Adding and Removing from On Hold Books---*/
    public void addHoldRequest(Borrower borrower, HoldRequest hr) {
        borrower.getOnHoldBooks().add(hr);
    }

    public void removeHoldRequest(Borrower borrower, HoldRequest hr) {
        borrower.getOnHoldBooks().remove(hr);
    }

    /*-------------------------------------------*/
}
