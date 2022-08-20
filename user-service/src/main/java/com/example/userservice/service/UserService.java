package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    List<UserEntity> getUserByAll();
}
