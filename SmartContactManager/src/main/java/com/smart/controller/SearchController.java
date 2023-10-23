package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.MyRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class SearchController 
{
	@Autowired
	private MyRepository myRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?>search(@PathVariable("query")String query,Principal principal)
	{
		String name = principal.getName();
		User user = this.myRepository.getUserByUserName(name);
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
}
