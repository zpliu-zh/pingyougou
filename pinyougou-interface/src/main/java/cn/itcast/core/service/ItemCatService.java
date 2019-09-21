package cn.itcast.core.service;

import java.util.List;

import cn.itcast.core.pojo.item.ItemCat;

public interface ItemCatService {
	
	public List<ItemCat> findByParentId(Long parentId);
	
	//通过商品分类Id查询模板Id
	public ItemCat findOne(Long id);
	
	//查询所有商品分类
	public List<ItemCat> findAll();

}
