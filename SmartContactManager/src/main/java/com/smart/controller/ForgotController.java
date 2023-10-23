package com.smart.controller;

import java.security.Principal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.MyRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;


@Controller
public class ForgotController 
{
	@Autowired
	private MyRepository myRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private BCryptPasswordEncoder bCrypt;
	
	
	
//  Email id form open handler...	
	@RequestMapping("/forgot")
	public String openEmailForm(Model model)
	{
		model.addAttribute("title","Forgot Page");
		return "forgot_email_form";
	}
// send otp	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email")String email,Model model,HttpSession session)
	{
		model.addAttribute("title","OTP Page");
		System.out.println("EMAIL-------"+email);
	//  generating 4 digit OTP
		Random random = new Random(1000);
		int otp = random.nextInt(99999);
		System.out.println("OTP--------"+otp);
	//  write code for send otp to email	
		String subject="OTP From SCM";
		String message=""
				+"<div style='border:1px solid #e2e2e2; padding:20px'>"
				+"<h1>"
				+"OTP is"
				+"<b>"+otp
				+"</n>"
				+"</h1>"
				+"</div>";
		String to=email;
		boolean flag= this.emailService.SendEmail(to, subject, message);
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else
		{
			session.setAttribute("message", "Check your Email Id !!!");
			return "forgot_email_form";
		}
		
	}
	
	//  verify OTP
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp")int otp,HttpSession session)
	{
		int  myotp=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		if(myotp==otp)
		{
	//   Change password form
			User user = this.myRepository.getUserByUserName(email);
			if(user==null)
			{
				// show error message
				session.setAttribute("message","User does not exists with this Email");
				return "forgot_email_form";
			}
			else
			{
				// show change password form
				return "change_password_form";
			}
			
		}
		else
		{
			session.setAttribute("message","You have entered wrong OTP !!");
			return "verify_otp";
		}
	}
//   Finally change password to complete all steps...
	@PostMapping("/change-password")
	public String newPassword(@RequestParam("password")String password,HttpSession session) {
		
	String email=(String) session.getAttribute("email");
		User user=this.myRepository.getUserByUserName(email);
		System.out.println(user);
		user.setPassword(this.bCrypt.encode(password));
		this.myRepository.save(user);
		return "redirect:/signin?change=password changed successfully......";
	}
}