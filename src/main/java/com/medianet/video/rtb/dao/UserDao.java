package com.medianet.video.rtb.dao;

import com.google.inject.Inject;
import com.medianet.video.rtb.exceptions.DaoExceptions;
import com.medianet.video.rtb.models.User;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class UserDao {
    private final Map<String, User> userMap = new HashMap<>();

    public User addUser(String id, String email, String conNo) throws DaoExceptions {
        if (userMap.containsKey(id)) {
            throw new DaoExceptions("User already exists");
        }
        var user = User.builder()
                .id(id)
                .email(email)
                .conNo(conNo)
                .build();
        userMap.put(user.getId(), user);
        return user;
    }

    public User getUser(String userId) throws DaoExceptions {
        if (!userMap.containsKey(userId)) {
            throw new DaoExceptions("User does not exist");
        }
        return userMap.get(userId);
    }
}
