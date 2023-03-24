package com.norouzi.librarymanagementsystem.service;

import com.norouzi.librarymanagementsystem.model.Loan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Scanner;

@Service
@AllArgsConstructor
public class LoanService {
    private final LibraryService libraryService;

    //Computes fine for a particular loan only
    public double computeFine(Loan loan) {

        //-----------Computing Fine-----------
        double totalFine = 0;

        if (!loan.isFinePaid()) {
            Date iDate = loan.getIssuedDate();
            Date rDate = new Date();

            long days = ChronoUnit.DAYS.between(rDate.toInstant(), iDate.toInstant());
            days = -days;

            days = days - libraryService.getLibraryInstance().getBook_return_deadline();

            if (days > 0)
                totalFine = days * libraryService.getLibraryInstance().getPer_day_fine();
            else
                totalFine = 0;
        }
        return totalFine;
    }


    public void payFine(Loan loan) {
        //-----------Computing Fine-----------//

        double totalFine = computeFine(loan);

        if (totalFine > 0) {
            System.out.println("\nTotal Fine generated: Rs " + totalFine);

            System.out.println("Do you want to pay? (y/n)");

            Scanner input = new Scanner(System.in);

            String choice = input.next();

            if (choice.equals("y") || choice.equals("Y"))
                loan.setFinePaid(true);

            if (choice.equals("n") || choice.equals("N"))
                loan.setFinePaid(false);
        } else {
            System.out.println("\nNo fine is generated.");
            loan.setFinePaid(true);
        }
    }


    // Extending issued Date
    public void renewIssuedBook(Loan loan, Date iDate) {
        loan.setIssuedDate(iDate);

        System.out.println("\nThe deadline of the book " + loan.getBook().getTitle() + " has been extended.");
        System.out.println("Issued Book is successfully renewed!\n");
    }
}
