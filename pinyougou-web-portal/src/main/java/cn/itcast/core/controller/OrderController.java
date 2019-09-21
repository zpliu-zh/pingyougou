package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderService;
import entity.Result;

/**
 * 订单管理
 * @author lx
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	
	@Reference
	private OrderService orderService;
	//保存  订单表 订单详情表
	@RequestMapping("/add")
	public Result add(@RequestBody Order order){
		try {
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			order.setUserId(name);
			orderService.add(order);
			return new Result(true,"提交订单成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"提交订单失败");
		}
	}
}
