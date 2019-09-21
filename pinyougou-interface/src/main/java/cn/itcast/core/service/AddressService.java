package cn.itcast.core.service;

import java.util.List;

import cn.itcast.core.pojo.address.Address;

public interface AddressService {
	
	//查询当前登陆人的收货地址结果集
	public List<Address> findListByLoginUser(String name);

}
