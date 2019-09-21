package cn.itcast.core.service;

import java.util.List;

import cn.itcast.core.pojo.item.Item;
import entity.Cart;

public interface CartService {
	
	
	//根据库存ID去查询库存对象
	public Item findItemById(Long id);
	
//	将购物车装满 
	public List<Cart> findCartList(List<Cart> cartList);
	
	//将合并后的购物车保存到缓存中
	public void mergeCartList(List<Cart> newCartList,String name);
	
	//获取缓存中的购物
	public List<Cart> findCartListByName(String name);

}
