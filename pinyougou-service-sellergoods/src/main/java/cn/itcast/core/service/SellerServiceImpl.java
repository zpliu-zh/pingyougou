package cn.itcast.core.service;
/**
 * 商家管理
 * @author lx
 *
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.seller.Seller;

@Service
public class SellerServiceImpl implements SellerService {

	@Autowired
	private SellerDao sellerDao;
	//添加
	public void add(Seller seller){
		
		//密码加密的
		seller.setPassword(new BCryptPasswordEncoder().encode(seller.getPassword()));
		//状态
		seller.setStatus("0");
		//添加
		sellerDao.insertSelective(seller);
		
	}
	@Override
	public Seller findSellerById(String sellerId) {
		// TODO Auto-generated method stub
		return sellerDao.selectByPrimaryKey(sellerId);
	}
}
