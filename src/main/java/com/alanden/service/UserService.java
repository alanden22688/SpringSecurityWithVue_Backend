package com.alanden.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alanden.dto.UserDTO;
import com.alanden.entity.UserEntity;
import com.alanden.repository.UserRepository;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
   
    public List<UserDTO> query(String userName){
    	List<UserEntity> users = userRepository.findByUsernameWithAuthorities(userName);
    	List<UserDTO> usersDTO = users.stream().map(u -> UserDTO.fromEntity(u)).collect(Collectors.toList());
		return usersDTO;
    }
}