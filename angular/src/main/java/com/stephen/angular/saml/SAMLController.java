package com.stephen.angular.saml;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SAMLController {
	
	@RequestMapping("/hello")
	public String index() {
		return "index";
	}
	

}
