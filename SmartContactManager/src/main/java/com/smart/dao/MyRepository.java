package com.smart.dao;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.smart.entities.User;

public interface MyRepository extends CrudRepository<User, Integer>
{
	@Query("select u from User u where u.email = :email")
	public User getUserByUserName(@Param("email")String email);

}
