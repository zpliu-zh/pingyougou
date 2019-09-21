package cn.itcast.core.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;

/**
 * 商品分类管理
 * @author lx
 *
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

	
	@Reference
	private ItemCatService itemCatService;
	
	//商品分类列表页面查询 
	@RequestMapping("/findByParentId")
	public List<ItemCat> findByParentId(Long parentId){
		return itemCatService.findByParentId(parentId);
	}
	//通过商品分类Id查询模板Id
	@RequestMapping("/findOne")
	public ItemCat findOne(Long id){
		return itemCatService.findOne(id);
	}
	//查询所有商品分类
	@RequestMapping("/findAll")
	public List<ItemCat> findAll(){
		return itemCatService.findAll();
	}
}
