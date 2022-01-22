package com.example.appuser.service;

import com.example.appuser.dao.UserMapper;
import com.example.appuser.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl {

    @Autowired
    UserMapper userMapper;

    public User getUserById(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null){
            user = new User();
        }
        return user;
    }


}
