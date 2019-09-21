package cn.itcast.core.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.common.utils.PhoneFormatCheckUtils;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import entity.Result;

/**
 * 用户管理
 * @author lx
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	
	@Reference
	private UserService userService;
	//发送短信验证码
	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		try {
			//判断手机格式是否合法  正则表达式
			if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
				return new Result(false,"手机格式不正确");
			}
			//发送短信验证码
			userService.sendCode(phone);
			
			return new Result(true,"发送成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"发送失败");
		}
	}
	//完成注册 添加
	@RequestMapping("/add")
	public Result add(@RequestBody User user,String smscode){
		try {
			userService.add(user, smscode);
			return new Result(true,"添加成功");
		} catch (RuntimeException e) {
			return new Result(false,e.getMessage());
		} catch (Exception e) {
			return new Result(false,"添加失败");
		}
	}
}
