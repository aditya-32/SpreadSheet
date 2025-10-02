package com.medianet.video.rtb;

import com.google.inject.Inject;
import com.medianet.video.rtb.dao.GroupDao;
import com.medianet.video.rtb.dao.UserDao;
import com.medianet.video.rtb.enums.ExpenseType;
import com.medianet.video.rtb.exceptions.DaoExceptions;
import com.medianet.video.rtb.exceptions.ManagerException;
import com.medianet.video.rtb.models.AmountShare;
import com.medianet.video.rtb.models.Group;
import com.medianet.video.rtb.models.Transaction;
import com.medianet.video.rtb.models.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class Splitwise {
    private final UserDao userDao;
    private final GroupDao groupDao;

    public Group createGroup(String groupId, List<String> users) {
        try {
            var userEntities = new ArrayList<User>();
            for (var user : users) {
                userEntities.add(userDao.getUser(user));
            }
            return groupDao.creatGroup(groupId, userEntities);
        } catch (DaoExceptions e) {
            log.error("Error creating group", e);
            return null;
        }
    }

    public User addUser(String userId, String email, String conNo) {
        try {
            return userDao.addUser(userId, email, conNo);
        } catch (DaoExceptions e) {
            log.error("Error creating user", e);
        }
        return null;
    }

    public void addUserToGroup(String userId, String groupId) {
        try {
            var user = userDao.getUser(userId);
            groupDao.addUser(user, groupId);
        } catch (DaoExceptions e) {
            log.error("Error adding user {} to group{}",userId, groupId, e);
        }
    }

    public void addExpense(String paidBy, float amount, List<AmountShare> shares, String groupId, ExpenseType type) throws ManagerException{
        try {
            var group = groupDao.getGroup(groupId);
            float total = 0;
            if (type.equals(ExpenseType.AMOUNT)) {
                for (var share: shares) {
                    total+= share.getShareValue();
                }
                if (total != amount ) {
                    throw new ManagerException("Total amount does not add upto " + amount);
                }
            } else if (type.equals(ExpenseType.PERCENTAGE)) {
                float percent = 0;
                for (var share: shares) {
                    percent+= share.getShareValue();
                }
                if (percent != 100) {
                    throw new ManagerException("Total percentage does not add to 100%");
                }
            }
            for (var share : shares) {
                float shareValue = getUserShare(type, share.getShareValue(), amount);
                var transaction = Transaction.builder()
                        .amount(shareValue)
                        .paidBy(paidBy)
                        .paidTo(share.getUserId())
                        .build();
                group.getTransactions().add(transaction);
            }
        } catch (DaoExceptions e) {
            log.error("Error adding expense", e);
            throw new ManagerException(e.getMessage());
        }
    }

    private float getUserShare(ExpenseType type, float shareValue, float amt) {
        return switch (type) {
            case RATIO -> shareValue*amt;
            case PERCENTAGE -> amt*shareValue/100;
            case AMOUNT -> shareValue;
        };
    }

    public void simplifyGroup(String groupId) {
        try {
            var group = groupDao.getGroup(groupId);
            var transactions = group.getTransactions();
            var balance = new HashMap<String, Float>();
            for (var transaction : transactions) {
                var from = transaction.getPaidBy();
                var to = transaction.getPaidTo();
                var amount = transaction.getAmount();
                balance.put(from, balance.getOrDefault(from, 0f) - amount);
                balance.put(to, balance.getOrDefault(to, 0f) + amount);
            }
            String rootUser = "";
            for (var entry : balance.entrySet()) {
                if (entry.getValue() != 0) {
                    rootUser = entry.getKey();
                    break;
                }
            }
            if (rootUser.isEmpty()) {
                System.out.println("Already simplified not more transaction needed to settle");
                return;
            }
            var finalTransaction = new ArrayList<Transaction>();
            dfs(balance, rootUser, finalTransaction, new HashSet<>());
            System.out.println(finalTransaction);
            group.setTransactions(finalTransaction);

        } catch (DaoExceptions e) {
            log.error("Error simplifying group", e);
            throw new ManagerException(e.getMessage());
        }
    }

    private void dfs(Map<String, Float> balance, String userId, List<Transaction> settles, Set<String> settledUser) {
        if (balance.get(userId) == 0f) {
            settledUser.add(userId);
            if (settledUser.size() == balance.size()) {
                settles.forEach(System.out::println);
                return;
            }
        }
        if (settledUser.size() == balance.size()) {
            settles.forEach(System.out::println);
            return;
        }
        settledUser.add(userId);
        var itr = balance.entrySet().iterator();
        var oldCurrentValue = balance.get(userId);
        balance.put(userId, 0f);
        for (var entry : balance.entrySet()) {
            if (entry.getValue() == 0f) {
                settledUser.add(entry.getKey());
                continue;
            }
            if (!Objects.equals(userId, entry.getKey()) && entry.getValue()* oldCurrentValue < 0) {
                var oldValue = entry.getValue();
                Transaction settleTransaction = null;
                if (oldCurrentValue < 0) {
                    settleTransaction = Transaction.builder()
                            .paidBy(entry.getKey())
                            .paidTo(userId)
                            .amount(oldCurrentValue)
                            .build();
                } else {
                    settleTransaction = Transaction.builder()
                            .paidBy(userId)
                            .paidTo(entry.getKey())
                            .amount(oldCurrentValue)
                            .build();
                }
                settles.addLast(settleTransaction);
                balance.put(entry.getKey(), oldValue + oldCurrentValue);
                dfs(balance, entry.getKey(), settles, settledUser);
                settles.removeLast();
                balance.put(entry.getKey(), oldValue);
            }
        }
        settledUser.remove(userId);
        balance.put(userId, oldCurrentValue);
    }

    @Data
    @AllArgsConstructor
    public static class Settle {
        private String from;
        private String to;
        private Float amount;
    }
}
