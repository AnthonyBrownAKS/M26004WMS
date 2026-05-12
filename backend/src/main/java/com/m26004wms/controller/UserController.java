package com.m26004wms.controller;

import com.m26004wms.common.Result;
import com.m26004wms.entity.User;
import com.m26004wms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<User>> listUser(){

        return Result.success(userService.listUser());
    }


}
