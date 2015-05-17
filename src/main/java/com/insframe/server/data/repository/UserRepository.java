package com.insframe.server.data.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.insframe.server.model.User;

public interface UserRepository extends MongoRepository<User, String>{
	public User findById(String id);
	public User findByFirstName(String firstName);
    public List<User> findByLastName(String lastName);
    public User findByUserName(String username);
    public User findByEmailAddress(String email);
    
    public Long deleteUserByUserName(String username);
    public Long deleteUserById(String id);
    
    public List<User> findByRole(String role);
}
