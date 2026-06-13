package com.m26004wms.service.impl;

import com.m26004wms.entity.User;
import com.m26004wms.mapper.UserMapper;
import com.m26004wms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    /**
     * 列出用户
     */
    @Override
    public List<User> listUser(){
        return userMapper.selectList(null);
    }




}
