package cn.itcast.core.service;

import cn.itcast.core.pojo.user.User;

public interface UserService {
	
	
	//发送短信验证码
	public void sendCode(final String phone);
	
	//完成注册 添加
	public void add(User user,String smscode);

}
