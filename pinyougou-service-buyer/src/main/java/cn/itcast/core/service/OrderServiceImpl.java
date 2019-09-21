package cn.itcast.core.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import entity.Cart;

/**
 * 保存订单
 * @author lx
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private OrderItemDao orderItemDao;
	@Autowired
	private ItemDao itemDao;
	//添加
	public void add(Order order){//收货人  手机 地址  微信
		//1:先获取购物车
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(order.getUserId());
		for (Cart cart : cartList) {
			//订单表
			//ID 
			long id = idWorker.nextId();
			order.setOrderId(id);
			//实付金额
			double totalPrice = 0;
			//付款状态
			order.setStatus("1");
			//创建时间
			//更新时间
			order.setCreateTime(new Date());
			order.setUpdateTime(new Date());
			//订单来源
			order.setSourceType("2");
			//商家ID
			order.setSellerId(cart.getSellerId());
			
			//商品结果集
			List<OrderItem> orderItemList = cart.getOrderItemList();
			for (OrderItem orderItem : orderItemList) {
				//ID
				long orderItemId = idWorker.nextId();
				orderItem.setId(orderItemId);
				//外键
				orderItem.setOrderId(id);
				//根据库存ID 查询item对象
				Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
				orderItem.setGoodsId(item.getGoodsId());
				//价格
				orderItem.setPrice(item.getPrice());
				//数量
				//小计
				orderItem.setTotalFee(
						new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
				
				//实付金额
				totalPrice += orderItem.getTotalFee().doubleValue();
				//标题
				orderItem.setTitle(item.getTitle());
				//商品图片
				//商家ID
				orderItem.setSellerId(item.getSellerId());
				//保存订单详情	//订单详情表
				orderItemDao.insertSelective(orderItem);
			}
			//设置实付金额给订单对象
			order.setPayment(new BigDecimal(totalPrice));
			//保存订单
			orderDao.insertSelective(order);
		}
		//清理购物车
		redisTemplate.boundHashOps("CART").delete(order.getUserId());
		
	}
}
