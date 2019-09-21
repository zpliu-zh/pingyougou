package cn.itcast.core.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理
 * @author lx
 *
 */
@RestController
@RequestMapping("/login")
public class LoginController {

	//获取当前登陆人
	@RequestMapping("/showName")
	public Map<String,String> showName(){
		
		Map<String,String> map = new HashMap<String, String>();
		//如何获取用户名 被SpringSecurity放在哪里了呢?
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		map.put("username", name);
//		map.put("cur_time", new Date());
		return map;
	}
}
