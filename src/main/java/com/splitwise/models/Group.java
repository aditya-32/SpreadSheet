package com.splitwise.models;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Group {
    private String groupId;
    private Float totalExpense;
    private List<User> users;
    private List<Transaction> transactions;

    public Group(String groupId, List<User> users) {
        this.groupId = groupId;
        this.users = users;
        this.totalExpense = 0f;
        this.transactions = new ArrayList<>();
    }
}
