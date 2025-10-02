package com.medianet.video.rtb;

import com.google.inject.Guice;
import com.google.inject.Stage;
import com.medianet.video.rtb.enums.ExpenseType;
import com.medianet.video.rtb.models.AmountShare;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        var injector = Guice.createInjector(Stage.PRODUCTION, new ApplicationModule());
        var driverClass = injector.getInstance(Splitwise.class);
        driverClass.addUser("1", "aditya@gmail.com", "1231247234");
        driverClass.addUser("2", "adarash@gmail.com", "654567899");
        driverClass.addUser("3", "manisha@gmail.com", "9876545678");

        var group = driverClass.createGroup("Meghalaya", List.of("1", "2", "3"));
        System.out.println("Group user " + group.getUsers());
        List<AmountShare> shares = new ArrayList<>();
        shares.add(AmountShare.builder()
                        .shareValue(5)
                        .userId("2")
                .build());
        shares.add(AmountShare.builder()
                .shareValue(1)
                .userId("3")
                .build());
        driverClass.addExpense("1", 6, shares, "Meghalaya", ExpenseType.AMOUNT);
        shares.clear();
        shares.add(AmountShare.builder()
                .shareValue(5)
                .userId("3")
                .build());
        driverClass.addExpense("2", 5, shares, "Meghalaya", ExpenseType.AMOUNT);
        shares.clear();
        shares.add(AmountShare.builder()
                .shareValue(10)
                .userId("1")
                .build());
        driverClass.addExpense("3", 10, shares, "Meghalaya", ExpenseType.AMOUNT);
        System.out.println("Transactions {}" + group.getTransactions());
        driverClass.simplifyGroup("Meghalaya");
    }
}