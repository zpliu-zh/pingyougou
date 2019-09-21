package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojogroup.GoodsVo;
import cn.itcast.core.service.GoodsService;
import entity.PageResult;
import entity.Result;

/**
 * 商品管理
 * @author lx
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
	
	@Reference
	private GoodsService goodsService;
	//商品添加
	@RequestMapping("/add")
	public Result add(@RequestBody GoodsVo vo){
		
		try {
			//当前登陆人
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			vo.getGoods().setSellerId(name);
			//添加 
			goodsService.add(vo);
			return new Result(true,"提交成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"提交失败");
		}
		
	}
	//商品修改
	@RequestMapping("/update")
	public Result update(@RequestBody GoodsVo vo){
		
		try {
			//当前登陆人
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			vo.getGoods().setSellerId(name);
			//添加 
			goodsService.update(vo);
			return new Result(true,"提交成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"提交失败");
		}
		
	}

	//商品管理之搜索  带条件 分页
	@RequestMapping("/search")
	public PageResult search(Integer page,Integer rows,@RequestBody Goods goods){
		return goodsService.search(page, rows, goods);
	}
	//查询一个GoodsVo
	@RequestMapping("/findOne")
	public GoodsVo findOne(Long id){
		return goodsService.findOne(id);
	}
	//开始审核  审核通过  驳回
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){
		
		try {
			//开始审核
			goodsService.updateStatus(ids, status);
			return new Result(true,"提交成功");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			return new Result(false,"提交失败");
		}
		
	}
	//删除(批量)
	@RequestMapping("/delete")
	public Result delete(Long[] ids){
		try {
			goodsService.delete(ids);
			return new Result(true,"提交成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"提交失败");
		}
		
	}
}
