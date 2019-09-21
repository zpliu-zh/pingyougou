package cn.itcast.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;

/**
 * 地址管理
 * @author lx
 *
 */
@Service
@Transactional
public class AddressServiceImpl implements AddressService {

	
	@Autowired
	private AddressDao addressDao;
	
	//查询地址 当前登陆人的
	//查询当前登陆人的收货地址结果集
	public List<Address> findListByLoginUser(String name){
		AddressQuery addressQuery = new AddressQuery();
		addressQuery.createCriteria().andUserIdEqualTo(name);
		
		return addressDao.selectByExample(addressQuery);
	}
}
