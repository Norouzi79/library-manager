package com.norouzi.librarymanagementsystem.service;

import com.norouzi.librarymanagementsystem.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class LibraryService {
    private final HoldRequestService holdRequestService;
    private final HoldRequestOperationsService operationsService;
    private final BorrowerService borrowerService;
    private final BookService bookService;
    private final LibrarianService librarianService;
    private final LoanService loanService;
    /*----Following Singleton Design Pattern (Lazy Instantiation)------------*/
    private static Library obj;

    public Library getLibraryInstance() {
        if (obj == null) {
            obj = new Library();
        }

        return obj;
    }

    /*-----Adding other People in Library----*/
    public void addClerk(Library library, Clerk c) {
        library.getPersons().add(c);
    }

    public void addBorrower(Library library, Borrower b) {
        library.getPersons().add(b);
    }


    public void addLoan(Library library, Loan l) {
        library.getLoans().add(l);
    }
    /*----------------------------------------------*/

    /*-----------Finding People in Library--------------*/
    public Borrower findBorrower(Library library) {
        System.out.println("\nEnter Borrower's ID: ");

        int id = 0;

        Scanner scanner = new Scanner(System.in);

        try {
            id = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("\nInvalid Input");
        }

        for (int i = 0; i < library.getPersons().size(); i++) {
            if (library.getPersons().get(i).getId() == id && library.getPersons().get(i).getClass().getSimpleName().equals("Borrower"))
                return (Borrower) (library.getPersons().get(i));
        }

        System.out.println("\nSorry this ID didn't match any Borrower's ID.");
        return null;
    }

    public Clerk findClerk(Library library) {
        System.out.println("\nEnter Clerk's ID: ");

        int id = 0;

        Scanner scanner = new Scanner(System.in);

        try {
            id = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("\nInvalid Input");
        }

        for (int i = 0; i < library.getPersons().size(); i++) {
            if (library.getPersons().get(i).getId() == id && library.getPersons().get(i).getClass().getSimpleName().equals("Clerk"))
                return (Clerk) (library.getPersons().get(i));
        }

        System.out.println("\nSorry this ID didn't match any Clerk's ID.");
        return null;
    }

    /*------- FUNCS. on Books In Library--------------*/
    public void addBookinLibrary(Library library, Book b) {
        library.getBooksInLibrary().add(b);
    }

    //When this function is called, only the pointer of the book placed in booksInLibrary is removed. But the real object of book
    //is still there in memory because pointers of that book placed in IssuedBooks and ReturnedBooks are still pointing to that book. And we
    //are maintaining those pointers so that we can maintain history.
    //But if we donot want to maintain history then we can delete those pointers placed in IssuedBooks and ReturnedBooks as well which are
    //pointing to that book. In this way the book will be really removed from memory.
    public void removeBookfromLibrary(Library library, Book b) {
        boolean delete = true;

        //Checking if this book is currently borrowed by some borrower
        for (int i = 0; i < library.getPersons().size() && delete; i++) {
            if (library.getPersons().get(i).getClass().getSimpleName().equals("Borrower")) {
                ArrayList<Loan> borBooks = ((Borrower) (library.getPersons().get(i))).getBorrowedBooks();

                for (int j = 0; j < borBooks.size() && delete; j++) {
                    if (borBooks.get(j).getBook() == b) {
                        delete = false;
                        System.out.println("This particular book is currently borrowed by some borrower.");
                    }
                }
            }
        }

        if (delete) {
            System.out.println("\nCurrently this book is not borrowed by anyone.");
            ArrayList<HoldRequest> hRequests = b.getHoldRequestsOperations().getHoldRequests();

            if (!hRequests.isEmpty()) {
                System.out.println("\nThis book might be on hold requests by some borrowers. Deleting this book will delete the relevant hold requests too.");
                System.out.println("Do you still want to delete the book? (y/n)");

                Scanner sc = new Scanner(System.in);

                while (true) {
                    String choice = sc.next();

                    if (choice.equals("y") || choice.equals("n")) {
                        if (choice.equals("n")) {
                            System.out.println("\nDelete Unsuccessful.");
                            return;
                        } else {
                            //Empty the books hold request array
                            //Delete the hold request from the borrowers too
                            for (int i = 0; i < hRequests.size() && delete; i++) {
                                HoldRequest hr = hRequests.get(i);
                                borrowerService.removeHoldRequest(hr.getBorrower(), hr);
                                operationsService.removeHoldRequest(library.getHoldRequestsOperations());
                            }
                        }
                    } else
                        System.out.println("Invalid Input. Enter (y/n): ");
                }

            } else
                System.out.println("This book has no hold requests.");

            library.getBooksInLibrary().remove(b);
            System.out.println("The book is successfully removed.");
        } else
            System.out.println("\nDelete Unsuccessful.");
    }


    // Searching Books on basis of title, Subject or Author
    public ArrayList<Book> searchForBooks(Library library) throws IOException {
        String choice;
        String title = "", subject = "", author = "";

        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("\nEnter either '1' or '2' or '3' for search by Title, Subject or Author of Book respectively: ");
            choice = sc.next();

            if (choice.equals("1") || choice.equals("2") || choice.equals("3"))
                break;
            else
                System.out.println("\nWrong Input!");
        }

        if (choice.equals("1")) {
            System.out.println("\nEnter the Title of the Book: ");
            title = reader.readLine();
        } else if (choice.equals("2")) {
            System.out.println("\nEnter the Subject of the Book: ");
            subject = reader.readLine();
        } else {
            System.out.println("\nEnter the Author of the Book: ");
            author = reader.readLine();
        }

        ArrayList<Book> matchedBooks = new ArrayList();

        //Retrieving all the books which matched the user's search query
        for (int i = 0; i < library.getBooksInLibrary().size(); i++) {
            Book b = library.getBooksInLibrary().get(i);

            if (choice.equals("1")) {
                if (b.getTitle().equals(title))
                    matchedBooks.add(b);
            } else if (choice.equals("2")) {
                if (b.getSubject().equals(subject))
                    matchedBooks.add(b);
            } else {
                if (b.getAuthor().equals(author))
                    matchedBooks.add(b);
            }
        }

        //Printing all the matched Books
        if (!matchedBooks.isEmpty()) {
            System.out.println("\nThese books are found: \n");

            System.out.println("------------------------------------------------------------------------------");
            System.out.println("No.\t\tTitle\t\t\tAuthor\t\t\tSubject");
            System.out.println("------------------------------------------------------------------------------");

            for (int i = 0; i < matchedBooks.size(); i++) {
                System.out.print(i + "-" + "\t\t");
                bookService.printInfo(matchedBooks.get(i));
                System.out.print("\n");
            }

            return matchedBooks;
        } else {
            System.out.println("\nSorry. No Books were found related to your query.");
            return null;
        }
    }


    // View Info of all Books in Library
    public void viewAllBooks(Library library) {
        if (!library.getBooksInLibrary().isEmpty()) {
            System.out.println("\nBooks are: ");

            System.out.println("------------------------------------------------------------------------------");
            System.out.println("No.\t\tTitle\t\t\tAuthor\t\t\tSubject");
            System.out.println("------------------------------------------------------------------------------");

            for (int i = 0; i < library.getBooksInLibrary().size(); i++) {
                System.out.print(i + "-" + "\t\t");
                bookService.printInfo(library.getBooksInLibrary().get(i));
                System.out.print("\n");
            }
        } else
            System.out.println("\nCurrently, Library has no books.");
    }


    //Computes total fine for all loans of a borrower
    public double computeFine2(Library library, Borrower borrower) {
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("No.\t\tBook's Title\t\tBorrower's Name\t\t\tIssued Date\t\t\tReturned Date\t\t\t\tFine(Rs)");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        double totalFine = 0;
        double per_loan_fine = 0;

        for (int i = 0; i < library.getLoans().size(); i++) {
            Loan l = library.getLoans().get(i);

            if ((l.getBorrower() == borrower)) {
                per_loan_fine = loanService.computeFine(l);
                System.out.print(i + "-" + "\t\t" + library.getLoans().get(i).getBook().getTitle() + "\t\t\t" +
                        library.getLoans().get(i).getBorrower().getName() + "\t\t" + library.getLoans().get(i).getIssuedDate() +
                        "\t\t\t" + library.getLoans().get(i).getDateReturned() + "\t\t\t\t" + per_loan_fine + "\n");

                totalFine += per_loan_fine;
            }
        }

        return totalFine;
    }


    public void createPerson(Library library, char x) {
        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nEnter Name: ");
        String n = "";
        try {
            n = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Enter Address: ");
        String address = "";
        try {
            address = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
        }

        int phone = 0;

        try {
            System.out.println("Enter Phone Number: ");
            phone = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("\nInvalid Input.");
        }

        //If clerk is to be created
        if (x == 'c') {
            double salary = 0;

            try {
                System.out.println("Enter Salary: ");
                salary = sc.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("\nInvalid Input.");
            }

            Clerk c = new Clerk(n, address, phone, salary, -1);
            addClerk(library, c);

            System.out.println("\nClerk with name " + n + " created successfully.");
            System.out.println("\nYour ID is : " + c.getId());
            System.out.println("Your Password is : " + c.getPassword());
        }

        //If librarian is to be created
        else if (x == 'l') {
            double salary = 0;
            try {
                System.out.println("Enter Salary: ");
                salary = sc.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("\nInvalid Input.");
            }

            Librarian l = new Librarian(n, address, phone, salary, -1);
            if (LibrarianService.addLibrarian(library, l)) {
                System.out.println("\nLibrarian with name " + n + " created successfully.");
                System.out.println("\nYour ID is : " + l.getId());
                System.out.println("Your Password is : " + l.getPassword());
            }
        }

        //If borrower is to be created
        else {
            Borrower b = new Borrower(n, address, phone);
            addBorrower(library, b);
            System.out.println("\nBorrower with name " + n + " created successfully.");

            System.out.println("\nYour ID is : " + b.getId());
            System.out.println("Your Password is : " + b.getPassword());
        }
    }


    public void createBook(Library library, String title, String subject, String author) {
        Book b = new Book(title, subject, author, false);

        addBookinLibrary(library, b);

        System.out.println("\nBook with Title " + b.getTitle() + " is successfully created.");
    }


    // Called when want an access to Portal
    public Person login(Library library) {
        Scanner input = new Scanner(System.in);

        int id = 0;
        String password = "";

        System.out.println("\nEnter ID: ");

        try {
            id = input.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("\nInvalid Input");
        }

        System.out.println("Enter Password: ");
        password = input.next();

        for (int i = 0; i < library.getPersons().size(); i++) {
            if (library.getPersons().get(i).getId() == id && library.getPersons().get(i).getPassword().equals(password)) {
                System.out.println("\nLogin Successful");
                return library.getPersons().get(i);
            }
        }

        if (library.getLibrarian() != null) {
            if (library.getLibrarian().getId() == id && library.getLibrarian().getPassword().equals(password)) {
                System.out.println("\nLogin Successful");
                return library.getLibrarian();
            }
        }

        System.out.println("\nSorry! Wrong ID or Password");
        return null;
    }


    // History when a Book was Issued and was Returned!
    public void viewHistory(Library library) {
        if (!library.getLoans().isEmpty()) {
            System.out.println("\nIssued Books are: ");

            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("No.\tBook's Title\tBorrower's Name\t  Issuer's Name\t\tIssued Date\t\t\tReceiver's Name\t\tReturned Date\t\tFine Paid");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < library.getLoans().size(); i++) {
                if (library.getLoans().get(i).getIssuer() != null)
                    System.out.print(i + "-" + "\t" + library.getLoans().get(i).getBook().getTitle() + "\t\t\t" +
                            library.getLoans().get(i).getBorrower().getName() + "\t\t" + library.getLoans().get(i).getIssuer().getName() + "\t    " +
                            library.getLoans().get(i).getIssuedDate());

                if (library.getLoans().get(i).getReceiver() != null) {
                    System.out.print("\t" + library.getLoans().get(i).getReceiver().getName() + "\t\t"
                            + library.getLoans().get(i).getDateReturned() + "\t   " + library.getLoans().get(i).isFinePaid() + "\n");
                } else
                    System.out.print("\t\t" + "--" + "\t\t\t" + "--" + "\t\t" + "--" + "\n");
            }
        } else
            System.out.println("\nNo issued books.");
    }


    //---------------------------------------------------------------------------------------//
    /*--------------------------------IN- COLLABORATION WITH DATA BASE------------------------------------------*/

    // Making Connection With Database
    public Connection makeConnection() {
        try {
            String host = "jdbc:derby://localhost:1527/LMS";
            String uName = "haris";
            String uPass = "123";
            Connection con = DriverManager.getConnection(host, uName, uPass);
            return con;
        } catch (SQLException err) {
            System.out.println(err.getMessage());
            return null;
        }
    }


    // Loading all info in code via Database.
    public void populateLibrary(Library library, Connection con) throws SQLException, IOException {
        Statement stmt = con.createStatement();

        /* --- Populating Book ----*/
        String SQL = "SELECT * FROM BOOK";
        ResultSet rs = stmt.executeQuery(SQL);

        if (!rs.next()) {
            System.out.println("\nNo Books Found in Library");
        } else {
            int maxID = 0;

            do {
                if (rs.getString("TITLE") != null && rs.getString("AUTHOR") != null && rs.getString("SUBJECT") != null && rs.getInt("ID") != 0) {
                    String title = rs.getString("TITLE");
                    String author = rs.getString("AUTHOR");
                    String subject = rs.getString("SUBJECT");
                    int id = rs.getInt("ID");
                    boolean issue = rs.getBoolean("IS_ISSUED");
                    Book b = new Book(title, subject, author, issue);
                    addBookinLibrary(library, b);

                    if (maxID < id)
                        maxID = id;
                }
            } while (rs.next());
        }

        /* ----Populating Clerks----*/

        SQL = "SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO,SALARY,DESK_NO FROM PERSON INNER JOIN CLERK ON ID=C_ID INNER JOIN STAFF ON S_ID=C_ID";

        rs = stmt.executeQuery(SQL);

        if (!rs.next()) {
            System.out.println("No clerks Found in Library");
        } else {
            do {
                String cname = rs.getString("PNAME");
                String adrs = rs.getString("ADDRESS");
                int phn = rs.getInt("PHONE_NO");
                double sal = rs.getDouble("SALARY");
                int desk = rs.getInt("DESK_NO");
                Clerk c = new Clerk(cname, adrs, phn, sal, desk);

                addClerk(library, c);
            }
            while (rs.next());

        }

        /*-----Populating Librarian---*/
        SQL = "SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO,SALARY,OFFICE_NO FROM PERSON INNER JOIN LIBRARIAN ON ID=L_ID INNER JOIN STAFF ON S_ID=L_ID";

        rs = stmt.executeQuery(SQL);
        if (!rs.next()) {
            System.out.println("No Librarian Found in Library");
        } else {
            do {
                String lname = rs.getString("PNAME");
                String adrs = rs.getString("ADDRESS");
                int phn = rs.getInt("PHONE_NO");
                double sal = rs.getDouble("SALARY");
                int off = rs.getInt("OFFICE_NO");
                Librarian l = new Librarian(lname, adrs, phn, sal, off);

                LibrarianService.addLibrarian(library, l);

            } while (rs.next());

        }

        /*---Populating Borrowers (partially)!!!!!!--------*/

        SQL = "SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO FROM PERSON INNER JOIN BORROWER ON ID=B_ID";

        rs = stmt.executeQuery(SQL);

        if (!rs.next()) {
            System.out.println("No Borrower Found in Library");
        } else {
            do {
                String name = rs.getString("PNAME");
                String adrs = rs.getString("ADDRESS");
                int phn = rs.getInt("PHONE_NO");

                Borrower b = new Borrower(name, adrs, phn);
                addBorrower(library, b);

            } while (rs.next());

        }

        /*----Populating Loan----*/

        SQL = "SELECT * FROM LOAN";

        rs = stmt.executeQuery(SQL);
        if (!rs.next()) {
            System.out.println("No Books Issued Yet!");
        } else {
            do {
                int borid = rs.getInt("BORROWER");
                int bokid = rs.getInt("BOOK");
                int iid = rs.getInt("ISSUER");
                Integer rid = (Integer) rs.getObject("RECEIVER");
                int rd = 0;
                java.util.Date rdate;

                java.util.Date idate = new java.util.Date(rs.getTimestamp("ISS_DATE").getTime());

                if (rid != null)    // if there is a receiver
                {
                    rdate = new java.util.Date(rs.getTimestamp("RET_DATE").getTime());
                    rd = rid;
                } else {
                    rdate = null;
                }

                boolean fineStatus = rs.getBoolean("FINE_PAID");

                boolean set = true;

                Borrower bb = null;


                for (int i = 0; i < library.getPersons().size() && set; i++) {
                    if (library.getPersons().get(i).getId() == borid) {
                        set = false;
                        bb = (Borrower) (library.getPersons().get(i));
                    }
                }

                set = true;
                Staff[] s = new Staff[2];

                if (iid == library.getLibrarian().getId()) {
                    s[0] = library.getLibrarian();
                } else {
                    for (int k = 0; k < library.getPersons().size() && set; k++) {
                        if (library.getPersons().get(k).getId() == iid && library.getPersons().get(k).getClass().getSimpleName().equals("Clerk")) {
                            set = false;
                            s[0] = (Clerk) (library.getPersons().get(k));
                        }
                    }
                }

                set = true;
                // If not returned yet...
                if (rid == null) {
                    s[1] = null;  // no reciever
                    rdate = null;
                } else {
                    if (rd == library.getLibrarian().getId())
                        s[1] = library.getLibrarian();

                    else {    //System.out.println("ff");
                        for (int k = 0; k < library.getPersons().size() && set; k++) {
                            if (library.getPersons().get(k).getId() == rd && library.getPersons().get(k).getClass().getSimpleName().equals("Clerk")) {
                                set = false;
                                s[1] = (Clerk) (library.getPersons().get(k));
                            }
                        }
                    }
                }

                set = true;

                ArrayList<Book> books = library.getBooksInLibrary();

                for (int k = 0; k < books.size() && set; k++) {
                    if (books.get(k).getBookID() == bokid) {
                        set = false;
                        Loan l = new Loan(bb, books.get(k), s[0], s[1], idate, rdate, fineStatus);
                        library.getLoans().add(l);
                    }
                }

            } while (rs.next());
        }

        /*----Populationg Hold Books----*/

        SQL = "SELECT * FROM ON_HOLD_BOOK";

        rs = stmt.executeQuery(SQL);
        if (!rs.next()) {
            System.out.println("No Books on Hold Yet!");
        } else {
            do {
                int borid = rs.getInt("BORROWER");
                int bokid = rs.getInt("BOOK");
                java.util.Date off = new java.util.Date(rs.getDate("REQ_DATE").getTime());

                boolean set = true;
                Borrower bb = null;

                ArrayList<? extends Person> persons = library.getPersons();

                for (int i = 0; i < persons.size() && set; i++) {
                    if (persons.get(i).getId() == borid) {
                        set = false;
                        bb = (Borrower) (persons.get(i));
                    }
                }

                set = true;

                ArrayList<Book> books = library.getBooksInLibrary();

                for (int i = 0; i < books.size() && set; i++) {
                    if (books.get(i).getBookID() == bokid) {
                        set = false;
                        HoldRequest hbook = new HoldRequest(bb, books.get(i), off);
                        operationsService.addHoldRequest(library.getHoldRequestsOperations(), hbook);
                        borrowerService.addHoldRequest(bb, hbook);
                    }
                }
            } while (rs.next());
        }

        /* --- Populating Borrower's Remaining Info----*/

        // Borrowed Books
        SQL = "SELECT ID,BOOK FROM PERSON INNER JOIN BORROWER ON ID=B_ID INNER JOIN BORROWED_BOOK ON B_ID=BORROWER ";

        rs = stmt.executeQuery(SQL);

        if (!rs.next()) {
            System.out.println("No Borrower has borrowed yet from Library");
        } else {

            do {
                int id = rs.getInt("ID");      // borrower
                int bid = rs.getInt("BOOK");   // book

                Borrower bb = null;
                boolean set = true;
                boolean okay = true;

                for (int i = 0; i < library.getPersons().size() && set; i++) {
                    if (library.getPersons().get(i).getClass().getSimpleName().equals("Borrower")) {
                        if (library.getPersons().get(i).getId() == id) {
                            set = false;
                            bb = (Borrower) (library.getPersons().get(i));
                        }
                    }
                }

                set = true;

                ArrayList<Loan> books = library.getLoans();

                for (int i = 0; i < books.size() && set; i++) {
                    if (books.get(i).getBook().getBookID() == bid && books.get(i).getReceiver() == null) {
                        set = false;
                        Loan bBook = new Loan(bb, books.get(i).getBook(), books.get(i).getIssuer(), null, books.get(i).getIssuedDate(), null, books.get(i).isFinePaid());
                        borrowerService.addBorrowedBook(bb, bBook);
                    }
                }

            } while (rs.next());
        }

        ArrayList<? extends Person> persons = library.getPersons();

        /* Setting Person ID Count */
        int max = 0;

        for (int i = 0; i < persons.size(); i++) {
            if (max < persons.get(i).getId())
                max = persons.get(i).getId();
        }
    }


    // Filling Changes back to Database
    public void fillItBack(Library library, Connection con) throws SQLException {
        /*-----------Loan Table Cleared------------*/

        String template = "DELETE FROM LIBRARY.LOAN";
        PreparedStatement stmts = con.prepareStatement(template);

        stmts.executeUpdate();

        /*-----------Borrowed Books Table Cleared------------*/

        template = "DELETE FROM LIBRARY.BORROWED_BOOK";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();

        /*-----------OnHoldBooks Table Cleared------------*/

        template = "DELETE FROM LIBRARY.ON_HOLD_BOOK";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();

        /*-----------Books Table Cleared------------*/

        template = "DELETE FROM LIBRARY.BOOK";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();

        /*-----------Clerk Table Cleared------------*/

        template = "DELETE FROM LIBRARY.CLERK";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();

        /*-----------Librarian Table Cleared------------*/

        template = "DELETE FROM LIBRARY.LIBRARIAN";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();

        /*-----------Borrower Table Cleared------------*/

        template = "DELETE FROM LIBRARY.BORROWER";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();

        /*-----------Staff Table Cleared------------*/

        template = "DELETE FROM LIBRARY.STAFF";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();

        /*-----------Person Table Cleared------------*/

        template = "DELETE FROM LIBRARY.PERSON";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();

        /* Filling Person's Table*/
        for (int i = 0; i < library.getPersons().size(); i++) {
            template = "INSERT INTO LIBRARY.PERSON (ID,PNAME,PASSWORD,ADDRESS,PHONE_NO) values (?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);

            stmt.setInt(1, library.getPersons().get(i).getId());
            stmt.setString(2, library.getPersons().get(i).getName());
            stmt.setString(3, library.getPersons().get(i).getPassword());
            stmt.setString(4, library.getPersons().get(i).getAddress());
            stmt.setInt(5, library.getPersons().get(i).getPhoneNo());

            stmt.executeUpdate();
        }

        /* Filling Clerk's Table and Staff Table*/
        for (int i = 0; i < library.getPersons().size(); i++) {
            if (library.getPersons().get(i).getClass().getSimpleName().equals("Clerk")) {
                template = "INSERT INTO LIBRARY.STAFF (S_ID,TYPE,SALARY) values (?,?,?)";
                PreparedStatement stmt = con.prepareStatement(template);

                stmt.setInt(1, library.getPersons().get(i).getId());
                stmt.setString(2, "Clerk");
                stmt.setDouble(3, ((Clerk) (library.getPersons().get(i))).getSalary());

                stmt.executeUpdate();

                template = "INSERT INTO LIBRARY.CLERK (C_ID,DESK_NO) values (?,?)";
                stmt = con.prepareStatement(template);

                stmt.setInt(1, library.getPersons().get(i).getId());
                stmt.setInt(2, ((Clerk) (library.getPersons().get(i))).getDeskNo());

                stmt.executeUpdate();
            }

        }

        if (library.getLibrarian() != null)    // if  libraryrarian is there
        {
            template = "INSERT INTO LIBRARY.STAFF (S_ID,TYPE,SALARY) values (?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);

            stmt.setInt(1, library.getLibrarian().getId());
            stmt.setString(2, "Librarian");
            stmt.setDouble(3, library.getLibrarian().getSalary());

            stmt.executeUpdate();

            template = "INSERT INTO LIBRARY.LIBRARIAN (L_ID,OFFICE_NO) values (?,?)";
            stmt = con.prepareStatement(template);

            stmt.setInt(1, library.getLibrarian().getId());
            stmt.setInt(2, library.getLibrarian().getOfficeNo());

            stmt.executeUpdate();
        }

        /* Filling Borrower's Table*/
        for (int i = 0; i < library.getPersons().size(); i++) {
            if (library.getPersons().get(i).getClass().getSimpleName().equals("Borrower")) {
                template = "INSERT INTO LIBRARY.BORROWER(B_ID) values (?)";
                PreparedStatement stmt = con.prepareStatement(template);

                stmt.setInt(1, library.getPersons().get(i).getId());

                stmt.executeUpdate();
            }
        }

        ArrayList<Book> books = library.getBooksInLibrary();

        /*Filling Book's Table*/
        for (int i = 0; i < books.size(); i++) {
            template = "INSERT INTO LIBRARY.BOOK (ID,TITLE,AUTHOR,SUBJECT,IS_ISSUED) values (?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);

            stmt.setInt(1, books.get(i).getBookID());
            stmt.setString(2, books.get(i).getTitle());
            stmt.setString(3, books.get(i).getAuthor());
            stmt.setString(4, books.get(i).getSubject());
            stmt.setBoolean(5, books.get(i).getIsIssued());
            stmt.executeUpdate();

        }

        /* Filling Loan Book's Table*/
        for (int i = 0; i < library.getLoans().size(); i++) {
            template = "INSERT INTO LIBRARY.LOAN(L_ID,BORROWER,BOOK,ISSUER,ISS_DATE,RECEIVER,RET_DATE,FINE_PAID) values (?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);

            stmt.setInt(1, i + 1);
            stmt.setInt(2, library.getLoans().get(i).getBorrower().getId());
            stmt.setInt(3, library.getLoans().get(i).getBook().getBookID());
            stmt.setInt(4, library.getLoans().get(i).getIssuer().getId());
            stmt.setTimestamp(5, new java.sql.Timestamp(library.getLoans().get(i).getIssuedDate().getTime()));
            stmt.setBoolean(8, library.getLoans().get(i).isFinePaid());
            if (library.getLoans().get(i).getReceiver() == null) {
                stmt.setNull(6, Types.INTEGER);
                stmt.setDate(7, null);
            } else {
                stmt.setInt(6, library.getLoans().get(i).getReceiver().getId());
                stmt.setTimestamp(7, new java.sql.Timestamp(library.getLoans().get(i).getDateReturned().getTime()));
            }

            stmt.executeUpdate();

        }

        /* Filling On_Hold_ Table*/

        int x = 1;
        for (int i = 0; i < library.getBooksInLibrary().size(); i++) {
            for (int j = 0; j < library.getBooksInLibrary().get(i).getHoldRequestsOperations().getHoldRequests().size(); j++) {
                template = "INSERT INTO LIBRARY.ON_HOLD_BOOK(REQ_ID,BOOK,BORROWER,REQ_DATE) values (?,?,?,?)";
                PreparedStatement stmt = con.prepareStatement(template);

                stmt.setInt(1, x);
                stmt.setInt(3, library.getBooksInLibrary().get(i).getHoldRequestsOperations().getHoldRequests().get(j).getBorrower().getId());
                stmt.setInt(2, library.getBooksInLibrary().get(i).getHoldRequestsOperations().getHoldRequests().get(j).getBook().getBookID());
                stmt.setDate(4, new java.sql.Date(library.getBooksInLibrary().get(i).getHoldRequestsOperations().getHoldRequests().get(j).getRequestDate().getTime()));

                stmt.executeUpdate();
                x++;

            }
        }
        /*for(int i=0;i<library.getBooks().size();i++)
        {
            for(int j=0;j<library.getBooks().get(i).getHoldRequests().size();j++)
            {
            template = "INSERT INTO LIBRARY.ON_HOLD_BOOK(REQ_ID,BOOK,BORROWER,REQ_DATE) values (?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);

            stmt.setInt(1,i+1);
            stmt.setInt(3,library.getBooks().get(i).getHoldRequests().get(j).getBorrower().getId());
            stmt.setInt(2,library.getBooks().get(i).getHoldRequests().get(j).getBook().getBookID());
            stmt.setDate(4,new java.sql.Date(library.getBooks().get(i).getHoldRequests().get(j).getRequestDate().getTime()));

            stmt.executeUpdate();
            }
        }*/

        /* Filling Borrowed Book Table*/
        for (int i = 0; i < library.getBooksInLibrary().size(); i++) {
            if (library.getBooksInLibrary().get(i).getIsIssued()) {
                boolean set = true;
                for (int j = 0; j < library.getLoans().size() && set; j++) {
                    if (library.getBooksInLibrary().get(i).getBookID() == library.getLoans().get(j).getBook().getBookID()) {
                        if (library.getLoans().get(j).getReceiver() == null) {
                            template = "INSERT INTO LIBRARY.BORROWED_BOOK(BOOK,BORROWER) values (?,?)";
                            PreparedStatement stmt = con.prepareStatement(template);
                            stmt.setInt(1, library.getLoans().get(j).getBook().getBookID());
                            stmt.setInt(2, library.getLoans().get(j).getBorrower().getId());

                            stmt.executeUpdate();
                            set = false;
                        }
                    }

                }

            }
        }
    }
}
