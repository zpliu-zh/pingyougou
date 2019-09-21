package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;

public interface SellerService {

	//添加
	public void add(Seller seller);
	
	//通过用户名查询用户对象
	public Seller findSellerById(String sellerId);
}
