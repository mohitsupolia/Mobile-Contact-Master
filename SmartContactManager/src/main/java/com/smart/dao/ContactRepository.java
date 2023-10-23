package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>
{
//  Pagination....
	@Query("from Contact as c where c.user.id =:userId")
//  Page is an interface. Page is a sublist of a list of object.
//  Pageable is an information of two things one is current page and another is contact per page.
//	Current Page-page
//	Contact per page-5
	public Page<Contact> findContactsByUser(@Param("userId")int userId,Pageable pageable);
	
//  Search method in show contacts
	public List<Contact> findByNameContainingAndUser(String name,User user);

}
