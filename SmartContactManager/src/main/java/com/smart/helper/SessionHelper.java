package com.smart.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper 
{
	public void removeMessageFromSession()
	{
		try
		{
			System.out.println("Removing message from session");
			HttpSession session = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		    session.removeAttribute("message");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}
