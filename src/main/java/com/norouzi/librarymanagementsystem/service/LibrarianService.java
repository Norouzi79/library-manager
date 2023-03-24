package com.norouzi.librarymanagementsystem.service;

import com.norouzi.librarymanagementsystem.model.Librarian;
import com.norouzi.librarymanagementsystem.model.Library;
import org.springframework.stereotype.Service;

@Service
public class LibrarianService {
    // Printing Librarian's Info
    public void printInfo(Librarian lib) {
        System.out.println("-----------------------------------------");
        System.out.println("\nThe details are: \n");
        System.out.println("ID: " + lib.getId());
        System.out.println("Name: " + lib.getName());
        System.out.println("Address: " + lib.getAddress());
        System.out.println("Phone No: " + lib.getPhoneNo() + "\n");
        System.out.println("Salary: " + lib.getSalary() + "\n");
        System.out.println("Office Number: " + lib.getOfficeNo());
    }

    public static boolean addLibrarian(Library library, Librarian lib) {
        //One Library can have only one Librarian
        if (library.getLibrarian() == null) {
            library.setLibrarian(lib);
            library.getPersons().add(library.getLibrarian());
            return true;
        } else
            System.out.println("\nSorry, the library already has one librarian. New Librarian can't be created.");
        return false;
    }
}
