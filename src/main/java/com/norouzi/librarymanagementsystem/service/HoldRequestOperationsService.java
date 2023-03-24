package com.norouzi.librarymanagementsystem.service;

import com.norouzi.librarymanagementsystem.model.HoldRequest;
import com.norouzi.librarymanagementsystem.model.HoldRequestOperations;
import org.springframework.stereotype.Service;

@Service
public class HoldRequestOperationsService {
    // adding a hold req.
    public void addHoldRequest(HoldRequestOperations operations, HoldRequest hr) {
        operations.getHoldRequests().add(hr);
    }

    // removing a hold req.
    public void removeHoldRequest(HoldRequestOperations operations) {
        if (!operations.getHoldRequests().isEmpty()) {
            operations.getHoldRequests().remove(0);
        }
    }
}
