package cn.itcast.core.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.CartService;
import entity.Cart;
import entity.Result;

/**
 * 购物车管理
 * 
 * @author lx
 *
 */
@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference
	private CartService cartService;

	// 加入购物车 CO RS W3C 标准 跨域源 解决方案 jsonp(底层 <script src="加入购物车成功" 浏览器 CORS
	// 解决方案最好的 W3C 标准
	@RequestMapping("/addGoodsToCartList")
	// @CrossOrigin(origins={"http://localhost:9003","http://localhost:9004"},allowCredentials="true")
	@CrossOrigin(origins = { "http://localhost:9003", "http://localhost:9004" })
	public Result addGoodsToCartList(Long itemId, Integer num, HttpServletResponse response,
			HttpServletRequest request) {
		// 声明
		List<Cart> cartList = null;
		try {
			// 未登陆
			// 1:获取Cookie
			Cookie[] cookies = request.getCookies();
			if (null != cookies && cookies.length > 0) {
				// 2:获取Cookie中的购物车
				for (Cookie cookie : cookies) {
					if ("CART".equals(cookie.getName())) {
						// 有购物车
						cartList = JSON.parseArray(cookie.getValue(), Cart.class);
						break;
					}
				}

			}
			// 3:没有 创建购物车
			if (null == cartList) {
				cartList = new ArrayList<>();
			}
			// 手中有的数据 只有 库存 ID 购买数量 准备工作
			Item item = cartService.findItemById(itemId);
			// 准备出新的购物车
			Cart newCart = new Cart();
			// 只存三个数据 就能表示此商品的全部信息
			// 1:商家ID (商家名称不存)
			newCart.setSellerId(item.getSellerId());
			// 2:库存ID (表示此商品的全部信息)
			List<OrderItem> newOrderItemList = new ArrayList<>();
			OrderItem newOrderItem = new OrderItem();
			newOrderItem.setItemId(itemId);
			// 3:数量 (用户买了多少件)
			newOrderItem.setNum(num);
			newOrderItemList.add(newOrderItem);
			newCart.setOrderItemList(newOrderItemList);

			// 4:追加当前款商品到购物车
			// 1)判断 当前款的商家 是否已经在上面的购物车集合中已经存在
			int newIndexOf = cartList.indexOf(newCart);// 判断newCart 是否在cartList
														// 是否存在 -1 不存在 >=0 存在
														// 角标返回来了
			if (newIndexOf != -1) {
				// --存在
				Cart oldCart = cartList.get(newIndexOf);
				List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
				// 2)判断当前款商品在上面的购物车中是否有同款
				int indexOf = oldOrderItemList.indexOf(newOrderItem);
				if (indexOf != -1) {
					OrderItem oldOrderItem = oldOrderItemList.get(indexOf);
					// --有 追加数量
					oldOrderItem.setNum(newOrderItem.getNum() + oldOrderItem.getNum());
				} else {
					// --没有 追加新款
					oldOrderItemList.add(newOrderItem);
				}
			} else {
				// --不存在 直接添加新的购物车
				cartList.add(newCart);
			}

			// 获取当前登陆人
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			// 如果匿名 未登陆
			if (!"anonymousUser".equals(name)) {
				// 登陆了
				// 5:将合并后的数据保存到缓存中 清空Cookie
				cartService.mergeCartList(cartList, name);
				Cookie cookie = new Cookie("CART", null);
				// -1:关闭浏览器 销毁 0 马上销毁 >0 时间 秒
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
			} else {
				// 未登陆
				// 5:将最新的购物车集合再保存到Cookie中 json格式字符串 再保存到Cookie (Cookie存不下 炸了)
				// 火狐浏览器 Cookie上限7900+
				Cookie cookie = new Cookie("CART", JSON.toJSONString(cartList));
				// -1:关闭浏览器 销毁 0 马上销毁 >0 时间 秒
				cookie.setMaxAge(60 * 60 * 24 * 365);
				cookie.setPath("/");
				response.addCookie(cookie);
			}

			return new Result(true, "加入购物车成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false, "加入购物车失败");
		}
	}

	// 查询购物车列表
	@RequestMapping("/findCartList")
	public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {
		// 声明
		List<Cart> cartList = null;
		// 未登陆
		// 1:获取Cookie
		Cookie[] cookies = request.getCookies();
		if (null != cookies && cookies.length > 0) {
			// 2:获取Cookie中的购物车
			for (Cookie cookie : cookies) {
				if ("CART".equals(cookie.getName())) {
					// 有购物车
					cartList = JSON.parseArray(cookie.getValue(), Cart.class);
					break;
				}
			}

		}
		
		// 获取当前登陆人
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		// 如果匿名 未登陆
		if (!"anonymousUser".equals(name)) {
			// 登陆了
//			3:有  将Cookie中的购物车合并缓存中  清空Cookie
			if(null != cartList){
				cartService.mergeCartList(cartList, name);
				Cookie cookie = new Cookie("CART", null);
				// -1:关闭浏览器 销毁 0 马上销毁 >0 时间 秒
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
			}
//			4:从缓存中取出购物车
			cartList = cartService.findCartListByName(name);
		} 

		// 5:有 将购物车装满
		if (null != cartList) {
			cartList = cartService.findCartList(cartList);
		}

		// 6:回显
		return cartList;
	}

}
