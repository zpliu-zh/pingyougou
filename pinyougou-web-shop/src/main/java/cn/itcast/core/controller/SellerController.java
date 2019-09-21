package cn.itcast.core.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SellerService;
import entity.Result;

/**
 * 商家管理
 * 
 * @author lx
 *
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

	@Reference
	private SellerService sellerService;

	// 申请入驻
	@RequestMapping("/add")
	public Result add(@RequestBody Seller seller) {
		try {
			// 添加
			sellerService.add(seller);
			return new Result(true, "提交成功");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new Result(false, "提交失败");
			// TODO: handle exception
		}
	}
}
