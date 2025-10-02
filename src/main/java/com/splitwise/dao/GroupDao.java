package com.splitwise.dao;

import com.google.inject.Inject;
import com.splitwise.exceptions.DaoExceptions;
import com.splitwise.models.Group;
import com.splitwise.models.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class GroupDao {
    private final Map<String, Group> groupMap = new HashMap<>();

    public Group creatGroup(String groupId, List<User> users) throws DaoExceptions {
        if (groupMap.containsKey(groupId)) {
            throw new DaoExceptions("Group already exists");
        }
        var group = new Group(groupId, users);
        groupMap.put(group.getGroupId(), group);
        return group;
    }
    public void addUser(User userId, String groupId) throws DaoExceptions{
        if (!groupMap.containsKey(groupId)) {
            throw new DaoExceptions("Group does not exist");
        }
        groupMap.get(groupId).getUsers().add(userId);
    }

    public Group getGroup(String groupId) throws DaoExceptions {
        if (!groupMap.containsKey(groupId)) {
            throw new DaoExceptions("Group does not exist");
        }
        return groupMap.get(groupId);
    }
}
