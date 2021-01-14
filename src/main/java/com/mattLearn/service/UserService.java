package com.mattLearn.service;

import com.mattLearn.dao.UserDAO;
import com.mattLearn.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Modified by Matt on Jan. 14th 2021
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    // overload the get method, can also use name to get the user.
    public User getUser(String name){
        return userDAO.selectByName(name);
    }
}
