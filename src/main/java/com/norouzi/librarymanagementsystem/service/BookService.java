package com.norouzi.librarymanagementsystem.service;

import com.norouzi.librarymanagementsystem.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

@Service
@AllArgsConstructor
public class BookService {
    private final HoldRequestService holdRequestService;
    private final HoldRequestOperationsService operationsService;
    private final BorrowerService borrowerService;
    private final LibraryService libraryService;
    private final LoanService loanService;

    // printing all hold req on a book.
    public void printHoldRequests(Book book) {
        if (!book.getHoldRequestsOperations().getHoldRequests().isEmpty()) {
            System.out.println("\nHold Requests are: ");

            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("No.\t\tBook's Title\t\t\tBorrower's Name\t\t\tRequest Date");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < book.getHoldRequestsOperations().getHoldRequests().size(); i++) {
                System.out.print(i + "-" + "\t\t");
                holdRequestService.print(book.getHoldRequestsOperations().getHoldRequests().get(i));
            }
        } else
            System.out.println("\nNo Hold Requests.");
    }

    // printing book's Info
    public void printInfo(Book book) {
        System.out.println(book.getTitle() + "\t\t\t" + book.getAuthor() + "\t\t\t" + book.getSubject());
    }

    // changing Info of a Book
    public void changeBookInfo(Book book) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String author = book.getAuthor();
        String subject = book.getSubject();
        String title = book.getTitle();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nUpdate Author? (y/n)");
        input = scanner.next();

        if (input.equals("y")) {
            System.out.println("\nEnter new Author: ");
            author = reader.readLine();
        }

        System.out.println("\nUpdate Subject? (y/n)");
        input = scanner.next();

        if (input.equals("y")) {
            System.out.println("\nEnter new Subject: ");
            subject = reader.readLine();
        }

        System.out.println("\nUpdate Title? (y/n)");
        input = scanner.next();

        if (input.equals("y")) {
            System.out.println("\nEnter new Title: ");
            title = reader.readLine();
        }

        System.out.println("\nBook is successfully updated.");

    }


    //-------------------------------------------------------------------//

    // Placing book on Hold
    public void placeBookOnHold(Book book, Borrower bor) {
        HoldRequest hr = new HoldRequest(bor, book, new Date());

        operationsService.addHoldRequest(book.getHoldRequestsOperations(), hr);        //Add this hold request to holdRequests queue of this book
        borrowerService.addHoldRequest(bor, hr);      //Add this hold request to that particular borrower's class as well

        System.out.println("\nThe book " + book.getTitle() + " has been successfully placed on hold by borrower " + bor.getName() + ".\n");
    }


    // Request for Holding a Book
    public void makeHoldRequest(Book book, Borrower borrower) {
        boolean makeRequest = true;

        //If that borrower has already borrowed that particular book. Then he isn't allowed to make request for that book. He will have to renew the issued book in order to extend the return deadline.
        for (int i = 0; i < borrower.getBorrowedBooks().size(); i++) {
            if (borrower.getBorrowedBooks().get(i).getBook() == book) {
                System.out.println("\n" + "You have already borrowed " + book.getTitle());
                return;
            }
        }


        //If that borrower has already requested for that particular book. Then he isn't allowed to make the same request again.
        for (int i = 0; i < book.getHoldRequestsOperations().getHoldRequests().size(); i++) {
            if ((book.getHoldRequestsOperations().getHoldRequests().get(i).getBorrower() == borrower)) {
                makeRequest = false;
                break;
            }
        }

        if (makeRequest) {
            placeBookOnHold(book, borrower);
        } else
            System.out.println("\nYou already have one hold request for this book.\n");
    }


    // Getting Info of a Hold Request
    public void serviceHoldRequest(Book book, HoldRequest hr) {
        operationsService.removeHoldRequest(book.getHoldRequestsOperations());
        borrowerService.removeHoldRequest(hr.getBorrower(), hr);
    }


    // Issuing a Book
    public void issueBook(Book book, Borrower borrower, Staff staff) {
        //First deleting the expired hold requests
        Date today = new Date();

        ArrayList<HoldRequest> hRequests = book.getHoldRequestsOperations().getHoldRequests();

        for (int i = 0; i < hRequests.size(); i++) {
            HoldRequest hr = hRequests.get(i);

            //Remove that hold request which has expired
            long days = ChronoUnit.DAYS.between(today.toInstant(), hr.getRequestDate().toInstant());
            days = -days;

            if (days > libraryService.getLibraryInstance().getHold_request_expiry()) {
                operationsService.removeHoldRequest(book.getHoldRequestsOperations());
                borrowerService.removeHoldRequest(hr.getBorrower(), hr);
            }
        }

        if (book.getIsIssued()) {
            System.out.println("\nThe book " + book.getTitle() + " is already issued.");
            System.out.println("Would you like to place the book on hold? (y/n)");

            Scanner sc = new Scanner(System.in);
            String choice = sc.next();

            if (choice.equals("y")) {
                makeHoldRequest(book, borrower);
            }
        } else {
            if (!book.getHoldRequestsOperations().getHoldRequests().isEmpty()) {
                boolean hasRequest = false;

                for (int i = 0; i < book.getHoldRequestsOperations().getHoldRequests().size() && !hasRequest; i++) {
                    if (book.getHoldRequestsOperations().getHoldRequests().get(i).getBorrower() == borrower) {
                        hasRequest = true;
                        break;
                    }

                }

                if (hasRequest) {
                    //If this particular borrower has the earliest request for this book
                    if (book.getHoldRequestsOperations().getHoldRequests().get(0).getBorrower() == borrower)
                        serviceHoldRequest(book, book.getHoldRequestsOperations().getHoldRequests().get(0));

                    else {
                        System.out.println("\nSorry some other users have requested for this book earlier than you. So you have to wait until their hold requests are processed.");
                        return;
                    }
                } else {
                    System.out.println("\nSome users have already placed this book on request and you haven't, so the book can't be issued to you.");

                    System.out.println("Would you like to place the book on hold? (y/n)");

                    Scanner sc = new Scanner(System.in);
                    String choice = sc.next();

                    if (choice.equals("y")) {
                        makeHoldRequest(book, borrower);
                    }

                    return;
                }
            }

            //If there are no hold requests for this book, then simply issue the book.
            book.setIsIssued(true);

            Loan iHistory = new Loan(borrower, book, staff, null, new Date(), null, false);

            libraryService.addLoan(libraryService.getLibraryInstance(), iHistory);
            borrowerService.addBorrowedBook(borrower, iHistory);

            System.out.println("\nThe book " + book.getTitle() + " is successfully issued to " + borrower.getName() + ".");
            System.out.println("\nIssued by: " + staff.getName());
        }
    }


    // Returning a Book
    public void returnBook(Book book, Borrower borrower, Loan l, Staff staff) {
        l.getBook().setIsIssued(false);
        l.setDateReturned(new Date());
        l.setReceiver(staff);

        borrowerService.removeBorrowedBook(borrower, l);

        loanService.payFine(l);

        System.out.println("\nThe book " + l.getBook().getTitle() + " is successfully returned by " + borrower.getName() + ".");
        System.out.println("\nReceived by: " + staff.getName());
    }
}
