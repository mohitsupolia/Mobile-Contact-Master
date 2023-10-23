package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.MyRepository;
import com.smart.entities.User;

public class UserDetailsServiceImple implements UserDetailsService
{
	@Autowired
	private MyRepository myRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
//   Fetching user from database	
		User user= myRepository.getUserByUserName(username);
		if(user==null)
		{
			throw new UsernameNotFoundException("User not found !!");
		}
		CustmorUserDetails custmorUserDetails = new CustmorUserDetails(user);
		return custmorUserDetails;
	}
	
}
