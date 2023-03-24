package com.norouzi.librarymanagementsystem.service;

import com.norouzi.librarymanagementsystem.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

@Service
@AllArgsConstructor
public class MainService {
    private final LibraryService libraryService;
    private final ClerkService clerkService;
    private final HoldRequestService holdRequestService;
    private final HoldRequestOperationsService operationsService;
    private final LibrarianService librarianService;
    private final LoanService loanService;
    private final BorrowerService borrowerService;
    private final BookService bookService;

    // Clearing Required Area of Screen
    public void clrscr() {
        for (int i = 0; i < 20; i++)
            System.out.println();
    }

    // Asking for Input as Choice
    public int takeInput(int min, int max) {
        String choice;
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("\nEnter Choice: ");

            choice = input.next();

            if ((!choice.matches(".*[a-zA-Z]+.*")) && (Integer.parseInt(choice) > min && Integer.parseInt(choice) < max)) {
                return Integer.parseInt(choice);
            } else
                System.out.println("\nInvalid Input.");
        }

    }

    // Functionalities of all Persons
    public void allFunctionalities(Person person, int choice) throws IOException {
        Library lib = libraryService.getLibraryInstance();

        Scanner scanner = new Scanner(System.in);
        int input = 0;

        //Search Book
        if (choice == 1) {
            libraryService.searchForBooks(lib);
        }

        //Do Hold Request
        else if (choice == 2) {
            ArrayList<Book> books = libraryService.searchForBooks(lib);

            if (books != null) {
                input = takeInput(-1, books.size());

                Book b = books.get(input);

                if ("Clerk".equals(person.getClass().getSimpleName()) || "Librarian".equals(person.getClass().getSimpleName())) {
                    Borrower bor = libraryService.findBorrower(lib);

                    if (bor != null)
                        bookService.makeHoldRequest(b, bor);
                } else
                    bookService.makeHoldRequest(b, (Borrower) person);
            }
        }

        //View borrower's personal information
        else if (choice == 3) {
            if ("Clerk".equals(person.getClass().getSimpleName()) || "Librarian".equals(person.getClass().getSimpleName())) {
                Borrower bor = libraryService.findBorrower(lib);

                if (bor != null)
                    bor.printInfo();
            } else
                person.printInfo();
        }

        //Compute Fine of a Borrower
        else if (choice == 4) {
            if ("Clerk".equals(person.getClass().getSimpleName()) || "Librarian".equals(person.getClass().getSimpleName())) {
                Borrower bor = libraryService.findBorrower(lib);

                if (bor != null) {
                    double totalFine = libraryService.computeFine2(lib, bor);
                    System.out.println("\nYour Total Fine is : Rs " + totalFine);
                }
            } else {
                double totalFine = libraryService.computeFine2(lib, (Borrower) person);
                System.out.println("\nYour Total Fine is : Rs " + totalFine);
            }
        }

        //Check hold request queue of a book
        else if (choice == 5) {
            ArrayList<Book> books = libraryService.searchForBooks(lib);

            if (books != null) {
                input = takeInput(-1, books.size());
                bookService.printHoldRequests(books.get(input));
            }
        }

        //Issue a Book
        else if (choice == 6) {
            ArrayList<Book> books = libraryService.searchForBooks(lib);

            if (books != null) {
                input = takeInput(-1, books.size());
                Book b = books.get(input);

                Borrower bor = libraryService.findBorrower(lib);

                if (bor != null) {
                    bookService.issueBook(b, bor, (Staff) person);
                }
            }
        }

        //Return a Book
        else if (choice == 7) {
            Borrower bor = libraryService.findBorrower(lib);

            if (bor != null) {
                borrowerService.printBorrowedBooks(bor);
                ArrayList<Loan> loans = bor.getBorrowedBooks();

                if (!loans.isEmpty()) {
                    input = takeInput(-1, loans.size());
                    Loan l = loans.get(input);

                    bookService.returnBook(l.getBook(), bor, l, (Staff) person);
                } else
                    System.out.println("\nThis borrower " + bor.getName() + " has no book to return.");
            }
        }

        //Renew a Book
        else if (choice == 8) {
            Borrower bor = libraryService.findBorrower(lib);

            if (bor != null) {
                borrowerService.printBorrowedBooks(bor);
                ArrayList<Loan> loans = bor.getBorrowedBooks();

                if (!loans.isEmpty()) {
                    input = takeInput(-1, loans.size());

                    loanService.renewIssuedBook(loans.get(input), new java.util.Date());
                } else
                    System.out.println("\nThis borrower " + bor.getName() + " has no issued book which can be renewed.");
            }
        }

        //Add new Borrower
        else if (choice == 9) {
            libraryService.createPerson(lib, 'b');
        }

        //Update Borrower's Personal Info
        else if (choice == 10) {
            Borrower bor = libraryService.findBorrower(lib);

            if (bor != null)
                borrowerService.updateBorrowerInfo(bor);
        }

        //Add new Book
        else if (choice == 11) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("\nEnter Title:");
            String title = reader.readLine();

            System.out.println("\nEnter Subject:");
            String subject = reader.readLine();

            System.out.println("\nEnter Author:");
            String author = reader.readLine();

            libraryService.createBook(lib, title, subject, author);
        }

        //Remove a Book
        else if (choice == 12) {
            ArrayList<Book> books = libraryService.searchForBooks(lib);

            if (books != null) {
                input = takeInput(-1, books.size());

                libraryService.removeBookfromLibrary(lib, books.get(input));
            }
        }

        //Change a Book's Info
        else if (choice == 13) {
            ArrayList<Book> books = libraryService.searchForBooks(lib);

            if (books != null) {
                input = takeInput(-1, books.size());

                bookService.changeBookInfo(books.get(input));
            }
        }

        //View clerk's personal information
        else if (choice == 14) {
            Clerk clerk = libraryService.findClerk(lib);

            if (clerk != null)
                clerk.printInfo();
        }

        // Functionality Performed.
        System.out.println("\nPress any key to continue..\n");
        scanner.next();
    }






    /*-------------------------------------MAIN---------------------------------------------------*/

    public void main(String[] args) {
        Scanner admin = new Scanner(System.in);

        //-------------------INTERFACE---------------------------//

        Library lib = libraryService.getLibraryInstance();

        // Setting some by default information like name of library ,fine, deadline and limit of hold request
        lib.setPer_day_fine(20);
        lib.setHold_request_expiry(7);
        lib.setBook_return_deadline(5);
        lib.setName("FAST Library");

        // Making connection with Database.
        Connection con = libraryService.makeConnection();

        if (con == null)    // Oops can't connnect !
        {
            System.out.println("\nError connecting to Database. Exiting.");
            return;
        }

        try {

            libraryService.populateLibrary(lib, con);   // Populating Library with all Records

            boolean stop = false;
            while (!stop) {
                clrscr();

                // FRONT END //
                System.out.println("--------------------------------------------------------");
                System.out.println("\tWelcome to Library Management System");
                System.out.println("--------------------------------------------------------");

                System.out.println("Following Functionalities are available: \n");
                System.out.println("1- Login");
                System.out.println("2- Exit");
                System.out.println("3- Admininstrative Functions"); // Administration has access only

                System.out.println("-----------------------------------------\n");

                int choice = 0;

                choice = takeInput(0, 4);

                if (choice == 3) {
                    System.out.println("\nEnter Password: ");
                    String aPass = admin.next();

                    if (aPass.equals("lib")) {
                        while (true)    // Way to Admin Portal
                        {
                            clrscr();

                            System.out.println("--------------------------------------------------------");
                            System.out.println("\tWelcome to Admin's Portal");
                            System.out.println("--------------------------------------------------------");
                            System.out.println("Following Functionalities are available: \n");

                            System.out.println("1- Add Clerk");
                            System.out.println("2- Add Librarian");
                            System.out.println("3- View Issued Books History");
                            System.out.println("4- View All Books in Library");
                            System.out.println("5- Logout");

                            System.out.println("---------------------------------------------");

                            choice = takeInput(0, 6);

                            if (choice == 5)
                                break;

                            if (choice == 1)
                                libraryService.createPerson(lib, 'c');
                            else if (choice == 2)
                                libraryService.createPerson(lib, 'l');

                            else if (choice == 3)
                                libraryService.viewHistory(lib);

                            else if (choice == 4)
                                libraryService.viewAllBooks(lib);

                            System.out.println("\nPress any key to continue..\n");
                            admin.next();
                        }
                    } else
                        System.out.println("\nSorry! Wrong Password.");
                } else if (choice == 1) {
                    Person person = libraryService.login(lib);

                    if (person == null) {
                    } else if (person.getClass().getSimpleName().equals("Borrower")) {
                        while (true)    // Way to Borrower's Portal
                        {
                            clrscr();

                            System.out.println("--------------------------------------------------------");
                            System.out.println("\tWelcome to Borrower's Portal");
                            System.out.println("--------------------------------------------------------");
                            System.out.println("Following Functionalities are available: \n");
                            System.out.println("1- Search a Book");
                            System.out.println("2- Place a Book on hold");
                            System.out.println("3- Check Personal Info of Borrower");
                            System.out.println("4- Check Total Fine of Borrower");
                            System.out.println("5- Check Hold Requests Queue of a Book");
                            System.out.println("6- Logout");
                            System.out.println("--------------------------------------------------------");

                            choice = takeInput(0, 7);

                            if (choice == 6)
                                break;

                            allFunctionalities(person, choice);
                        }
                    } else if (person.getClass().getSimpleName().equals("Clerk")) {
                        while (true) // Way to Clerk's Portal
                        {
                            clrscr();

                            System.out.println("--------------------------------------------------------");
                            System.out.println("\tWelcome to Clerk's Portal");
                            System.out.println("--------------------------------------------------------");
                            System.out.println("Following Functionalities are available: \n");
                            System.out.println("1- Search a Book");
                            System.out.println("2- Place a Book on hold");
                            System.out.println("3- Check Personal Info of Borrower");
                            System.out.println("4- Check Total Fine of Borrower");
                            System.out.println("5- Check Hold Requests Queue of a Book");
                            System.out.println("6- Check out a Book");
                            System.out.println("7- Check in a Book");
                            System.out.println("8- Renew a Book");
                            System.out.println("9- Add a new Borrower");
                            System.out.println("10- Update a Borrower's Info");
                            System.out.println("11- Logout");
                            System.out.println("--------------------------------------------------------");

                            choice = takeInput(0, 12);

                            if (choice == 11)
                                break;

                            allFunctionalities(person, choice);
                        }
                    } else if (person.getClass().getSimpleName().equals("Librarian")) {
                        while (true) // Way to Librarian Portal
                        {
                            clrscr();

                            System.out.println("--------------------------------------------------------");
                            System.out.println("\tWelcome to Librarian's Portal");
                            System.out.println("--------------------------------------------------------");
                            System.out.println("Following Functionalities are available: \n");
                            System.out.println("1- Search a Book");
                            System.out.println("2- Place a Book on hold");
                            System.out.println("3- Check Personal Info of Borrower");
                            System.out.println("4- Check Total Fine of Borrower");
                            System.out.println("5- Check Hold Requests Queue of a Book");
                            System.out.println("6- Check out a Book");
                            System.out.println("7- Check in a Book");
                            System.out.println("8- Renew a Book");
                            System.out.println("9- Add a new Borrower");
                            System.out.println("10- Update a Borrower's Info");
                            System.out.println("11- Add new Book");
                            System.out.println("12- Remove a Book");
                            System.out.println("13- Change a Book's Info");
                            System.out.println("14- Check Personal Info of Clerk");
                            System.out.println("15- Logout");
                            System.out.println("--------------------------------------------------------");

                            choice = takeInput(0, 16);

                            if (choice == 15)
                                break;

                            allFunctionalities(person, choice);
                        }
                    }

                } else
                    stop = true;

                System.out.println("\nPress any key to continue..\n");
                Scanner scanner = new Scanner(System.in);
                scanner.next();
            }

            //Loading back all the records in database
            libraryService.fillItBack(lib, con);
        } catch (Exception e) {
            System.out.println("\nExiting...\n");
        }   // System Closed!

    }    // Main Closed
}
