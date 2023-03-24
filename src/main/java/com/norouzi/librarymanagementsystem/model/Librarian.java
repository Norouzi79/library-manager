package com.norouzi.librarymanagementsystem.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Librarian extends Staff {

    int officeNo;     //Office Number of the Librarian
    public static int currentOfficeNumber = 0;

    public Librarian(String n, String a, int p, double s, int of) // para cons.
    {
        super(n, a, p, s);

        if (of == -1)
            officeNo = currentOfficeNumber;
        else
            officeNo = of;

        currentOfficeNumber++;
    }

    protected Librarian() {
    }
}