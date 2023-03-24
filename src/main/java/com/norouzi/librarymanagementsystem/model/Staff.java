package com.norouzi.librarymanagementsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class Staff extends Person {
    protected double salary;

    public Staff(String n, String a, int p, double s) {
        super(n, a, p);
        salary = s;
    }

    protected Staff() {
        super(null, null, 0);
    }
}