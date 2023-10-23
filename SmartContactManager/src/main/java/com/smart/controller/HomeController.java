package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.MyRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController 
{
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private MyRepository myRepository;
//  Home handler
	@RequestMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title","Home-Mobile Contact Master");
		return "home";
	}	
//  About handler	
	@RequestMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title","About-Mobile Contact Master");
		return "about";
	}
//  SignUp handler	
	@RequestMapping("/signup")
	public String signup(Model model)
	{
		model.addAttribute("title","Signup-Mobile Contact Master");
		model.addAttribute("user",new User());
		return "signup";
	}
//  Handler for registering the user
	@PostMapping("/do_register")
	public String register(@Valid @ModelAttribute("user")User user,BindingResult result1, /* @RequestParam(value="agreement",defaultValue = "false")boolean agreement,*/ Model model,HttpSession session)
	{
		try
		{
//			if(!agreement)
//			{
//				System.out.println("you have not agreed the terms and conditions");
//				throw new Exception("you have not agreed the terms and conditions");
//			}
			if(result1.hasErrors())
			{
				System.out.println(user);
				model.addAttribute("user",user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
//			System.out.println("Agreement ="+agreement);
			System.out.println("user"+user);
			
			User result = this.myRepository.save(user);
			
      		model.addAttribute("user",new User());
			session.setAttribute("message",new Message("Successfully Registerd !!","alert-success"));
			return "signup";
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			model.addAttribute("user",user);
//			session.setAttribute("message",new Message("Something went wrong !!"+e.getMessage(),"alert-danger"));
			return "signup";
		}
			
	}
// handler for custom login
	@GetMapping("/signin")
	public String loginHandler(Model model)
	{
		model.addAttribute("title","Login-Mobile Contact Master");
		return "login";
	}
}

