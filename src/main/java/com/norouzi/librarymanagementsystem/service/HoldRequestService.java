package com.norouzi.librarymanagementsystem.service;

import com.norouzi.librarymanagementsystem.model.HoldRequest;
import org.springframework.stereotype.Service;

@Service
public class HoldRequestService {
    // Print Hold Request Info
    public void print(HoldRequest holdRequest) {
        System.out.print(holdRequest.getBook().getTitle() + "\t\t\t\t" + holdRequest.getBorrower().getName() + "\t\t\t\t" + holdRequest.getRequestDate() + "\n");
    }
}
