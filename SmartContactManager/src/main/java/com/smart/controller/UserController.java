package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.smart.dao.ContactRepository;
import com.smart.dao.MyRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController 
{
	@Autowired
	private MyRepository myRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
// this data can share all handlers like index,add-contact	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal)
	{
		String username = principal.getName();
		// get the user using username(Email)from database
		User user = myRepository.getUserByUserName(username);
		model.addAttribute("user",user);
		System.out.println(user);
	}
	
	@RequestMapping("/index")
	public String dashBoard(Model model,Principal principal)
	{
		model.addAttribute("title","user dashboard");
		return "normal/dash_board";
	}
	
// open add contact form handler
	@RequestMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title","Add_contact Page");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute("contact")Contact contact,@RequestParam("profileImage")MultipartFile file,Model model, Principal principal,HttpSession session)
	{
		try
		{
		String name = principal.getName();
		User user = myRepository.getUserByUserName(name);
		
			
		
//      Processing the data of the file		
		if(file.isEmpty())
		{
//        if the file is empty and try again	
			System.out.println("File is not uploaded");
			contact.setImageURL("contact.png");
		}
		else
		{
//       update the file to folder and update the name to contact
			contact.setImageURL(file.getOriginalFilename());
			File saveFile=new ClassPathResource("static/images").getFile().getAbsoluteFile();
			Path path=Paths.get(saveFile+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
		    System.out.println("Image is uploaded in the folder");
		}
	
		contact.setUser(user);
	//  user ka pass contact ki list hai,tou usi contact ma hum add kar dega.	
		user.getContacts().add(contact);
	//  updated the date base
		 this.myRepository.save(user);
	//	model.addAttribute("contact",new Contact());
	    session.setAttribute("message",new Message("Your contact is added !!,Added more..","success"));	
		
		System.out.println(contact);
		System.out.println(" updated Data added in the datebase");
		}
		catch(Exception e)
		{
			System.out.println("ERROR"+e.getMessage());
			e.printStackTrace();
			session.setAttribute("message",new Message("Something went wrong !!,Try again..","danger"));
		}
		return "normal/add_contact_form";
	}
//   Show contacts handler
//   Per Page =5[n] 	
//   Current Page =0[page]
	
	@RequestMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page")Integer page, Model model,Principal principal)
	{
		model.addAttribute("title","Show_Contacts Page");
		String name = principal.getName();
		User user = this.myRepository.getUserByUserName(name);
//		Current Page-page
//		Contact per page-5
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		return "normal/show_contacts";
	}
//    Showing particular contact details.
	@RequestMapping("/contact/{id}")
	public String showContactDetail(@PathVariable("id")Integer id,Model model,Principal principal)
	{
		System.out.println("Contact-ID"+id);
		Optional<Contact> contactOptional = this.contactRepository.findById(id);
		Contact contact = contactOptional.get();
//      check kon sa user login hai
		String name = principal.getName();
		User user = this.myRepository.getUserByUserName(name);
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact",contact);
			model.addAttribute("title",contact.getName());
		}
		
		return "normal/contact_detail";
	}
//   Delete button handler	
	@RequestMapping("/delete/{id}")
	public String Delete(@PathVariable("id")Integer id,Model model,HttpSession session,Principal principal)
	{
		Optional<Contact> optionalContact = this.contactRepository.findById(id);
		Contact contact = optionalContact.get();
		String name = principal.getName();
		User user = this.myRepository.getUserByUserName(name);
		
//      Check
		if(user.getId()==contact.getUser().getId())
		{
		System.out.println("Contact="+contact.getId());
//      delete contact		
		user.getContacts().remove(contact);
		this.myRepository.save(user);
		session.setAttribute("message",new Message("Contact deleted successfully..","success"));
		}
		return "redirect:/user/show-contacts/0";
	}
//   Update button handler
	@PostMapping("/update/{id}")
	public String update(@PathVariable("id")Integer id,Model model)
	{
		Contact contact = this.contactRepository.findById(id).get();
		model.addAttribute("title","Update Page");
		model.addAttribute("contact",contact);
		return "normal/update_form";
	}
// Update save handler..
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute("contact")Contact contact,@RequestParam("profileImage")MultipartFile file, Principal principal,Model model,HttpSession session)
	{
		try
		{
			Contact oldContactDetail = this.contactRepository.findById(contact.getId()).get();
		if(file.isEmpty())
		{
			contact.setImageURL(oldContactDetail.getImageURL());
			
		}
		else
		{
//       Delete old photo
//			File deleteFile = new ClassPathResource("static/images").getFile().getAbsoluteFile();
//			File file1 = new File(deleteFile,oldContactDetail.getImageURL());
//			file1.delete();
//       Update new photo			
			contact.setImageURL(file.getOriginalFilename());
			File saveFile = new ClassPathResource("static/images").getFile().getAbsoluteFile();
			Path path = Paths.get(saveFile+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
		}
		String name = principal.getName();
		User user = this.myRepository.getUserByUserName(name);
		contact.setUser(user);
	 	this.contactRepository.save(contact);
		 session.setAttribute("message",new Message("Your contact is Updated !!","success"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			session.setAttribute("message",new Message("Something went wrong !!,Try again..","danger"));
		}
		return "normal/contact_detail";
	}
//    Your profile handler
	@RequestMapping("/profile")
	public String yourProfile(Model model,Principal principal)
	{
		String name = principal.getName();
		User user = this.myRepository.getUserByUserName(name);
		
		model.addAttribute("title","Profile Page");
		return "normal/profile";
	}
//   Setting 
	@RequestMapping("/setting")
	public String setting(Model model)
	{
		model.addAttribute("title","Setting Page");
		return "normal/setting";
	}
//  submit the form of change Password..
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldpassword")String oldpassword,@RequestParam("newpassword")String newpassword,Principal principal,HttpSession session)
	{
		try
		{
			String name = principal.getName();
			User currentUser = this.myRepository.getUserByUserName(name);
			System.out.println("CURRENT-PASSWORD-----------"+currentUser.getPassword());
			
			if(this.bCryptPasswordEncoder.matches(oldpassword, currentUser.getPassword()))
			{
		//    change the Password..
				currentUser.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
				this.myRepository.save(currentUser);
				session.setAttribute("message",new Message(" Your Password is changed successfully !!","success"));
			}
			else
			{
				session.setAttribute("message", new Message("Wrong old Password...Please try again !!","danger"));
				return "redirect:/user/setting";
			}
			
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		return "redirect:/user/index";
	}
}
