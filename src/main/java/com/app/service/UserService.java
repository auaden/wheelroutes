package com.app.service;

import com.app.dao.UserDao;
import com.app.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by adenau on 5/8/16.
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public String login(User user) {
        String errorMsg = null;
        String email = user.getEmail();
        String password = user.getPassword();

        if (email.trim().length() == 0 || password.trim().length() == 0 ) {
            errorMsg = "Username/password cannot be blank";
            return errorMsg;
        }

        User foundUser = userDao.find(email);
        if (foundUser == null || !password.equals(foundUser.getPassword())) {
            errorMsg = "Incorrect username/password";
        }

        return errorMsg;
    }


    public User findUser(String email) {
        return userDao.find(email);
    }
    /*
    public String register(User user) {
        String errorMsg = null;
        String email = user.getEmail().trim();
        String password = user.getPassword().trim();

        if (email.length() == 0 || password.length() == 0) {
            errorMsg = "Please fill in all the blanks";
            return errorMsg;
        }

        //check if email is taken
        User foundUser = userDao.find(email);
        if (foundUser != null) {
            errorMsg = "Username taken. Please choose another";
            return errorMsg;
        }
        
        userDao.insert(user);
        return errorMsg;
    }*/
    
    public String register(User user) {
        String errorMsg = null;
        String email = user.getEmail().trim();
        String password = user.getPassword().trim();
        int sensitivity = 0;

        if (email.length() == 0 || password.length() == 0) {
            errorMsg = "Please fill in all the blanks";
            return errorMsg;
        }

        //check if email is taken
        User foundUser = userDao.find(email);
        if (foundUser != null) {
            errorMsg = "Username taken. Please choose another";
            return errorMsg;
        }
        
        if (user.isExpPain()) { //Experiences pain
            sensitivity += 35;
        }
        if (!user.isHaveBalance()) { //Poor sitting balance
            sensitivity += 35;
        }
        
        userDao.insert(user, sensitivity);
        return errorMsg;
    }
    
    //difference 
    public void modifySensitivity(User user, int sensitivityAdjust) {
        User fullUser = userDao.find(user.getEmail());
        if (sensitivityAdjust > 0) {
            userDao.modifySensitivity(fullUser, 5); //increase
        } else {
            userDao.modifySensitivity(fullUser, -5); //decrease
        }
    }
    
}
