package cn.itcast.core.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.service.AddressService;

/**
 * 收货地址管理
 * @author lx
 *
 */
@RestController
@RequestMapping("/address")
public class AddressController {
	
	@Reference
	private AddressService addressService;
	//查询当前登陆人的收货地址结果集
	@RequestMapping("/findListByLoginUser")
	public List<Address> findListByLoginUser(){
		//
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		return addressService.findListByLoginUser(name);
	}

}
