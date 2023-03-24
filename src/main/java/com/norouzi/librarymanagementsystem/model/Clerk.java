package com.norouzi.librarymanagementsystem.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Clerk extends Staff {

    private Integer deskNo;     //Desk Number of the Clerk
    private Integer currentdeskNumber = 0;

    public Clerk(String n, String a, int ph, double s, int dk) // para cons.
    {
        super(n, a, ph, s);

        if (dk == -1) {
            deskNo = currentdeskNumber;
        } else {
            deskNo = dk;
        }

        currentdeskNumber++;
    }

    protected Clerk() {
    }
}