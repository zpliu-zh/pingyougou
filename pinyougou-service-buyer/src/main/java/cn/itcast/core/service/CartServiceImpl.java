package cn.itcast.core.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import entity.Cart;

/**
 * 购物车管理
 * @author lx
 *
 */
@Service
public class CartServiceImpl implements CartService{

	
	@Autowired
	private ItemDao itemDao;
	
	//根据库存ID去查询库存对象
	public Item findItemById(Long id){
		return itemDao.selectByPrimaryKey(id);
	}
//	将购物车装满 
	public List<Cart> findCartList(List<Cart> cartList){
		
		for (Cart cart : cartList) {
			
			List<OrderItem> orderItemList = cart.getOrderItemList();
			for (OrderItem orderItem : orderItemList) {
				 //库存ID
				Item item = findItemById(orderItem.getItemId());
				//商品图片
				orderItem.setPicPath(item.getImage());
				//标题
				orderItem.setTitle(item.getTitle());
				//单价
				orderItem.setPrice(item.getPrice());
				//数量
				//小计
				orderItem.setTotalFee(
						new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
				//商家名称
				cart.setSellerName(item.getSeller());
			}
			
		}
		return cartList;
	}
	
	@Autowired
	private RedisTemplate redisTemplate;
	//将合并后的购物车保存到缓存中
	public void mergeCartList(List<Cart> newCartList,String name){
		//1:获取缓存中购物车
		List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
		//2:将新来的购物车跟原来的购物车合并
		oldCartList = mergeNewAndOldCart(newCartList,oldCartList);
		//3:将合并后的购物车 添加到缓存中
		redisTemplate.boundHashOps("CART").put(name, oldCartList);
		
		
	}
	//新老购物车集合大合并
	public List<Cart> mergeNewAndOldCart(List<Cart> newCartList,List<Cart> oldCartList){
		if(null != newCartList && newCartList.size() > 0){
			if(null != oldCartList && oldCartList.size() > 0){
				//新车与老车都有值 大合并
				for (Cart newCart : newCartList) {
					//1)判断 当前款的商家 是否已经在上面的购物车集合中已经存在 
					int newIndexOf = oldCartList.indexOf(newCart);//判断newCart 是否在cartList 是否存在   -1 不存在   >=0 存在 角标返回来了      
					if(newIndexOf != -1){
						//--存在
						Cart oldCart = oldCartList.get(newIndexOf);
						List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
						
						List<OrderItem> newOrderItemList = newCart.getOrderItemList();
						for (OrderItem newOrderItem : newOrderItemList) {
							//2)判断当前款商品在上面的购物车中是否有同款
							int indexOf = oldOrderItemList.indexOf(newOrderItem);
							if(indexOf != -1){
								OrderItem oldOrderItem = oldOrderItemList.get(indexOf);
								//--有  追加数量
								oldOrderItem.setNum(newOrderItem.getNum() + oldOrderItem.getNum());
							}else{
								//--没有  追加新款
								oldOrderItemList.add(newOrderItem);
							}
						}
					}else{
						//--不存在  直接添加新的购物车
						oldCartList.add(newCart);
					}
				}
			}else{
				//返回新车
				return newCartList;
			}
		}
		//返回老车
		return oldCartList;
	}
	//获取缓存中的购物
	public List<Cart> findCartListByName(String name){
		return (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
	}
}
