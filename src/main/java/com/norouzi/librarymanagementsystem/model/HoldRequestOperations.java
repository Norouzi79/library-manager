package com.norouzi.librarymanagementsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;

@Entity
@Data
public class HoldRequestOperations {
    @Id
    private Long id;

    @OneToMany
    private ArrayList<HoldRequest> holdRequests;

    public HoldRequestOperations() {
        holdRequests = new ArrayList<>();
    }
}
