package cn.itcast.core.service;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojogroup.GoodsVo;
import entity.PageResult;

public interface GoodsService {

	// 商品添加
	public void add(GoodsVo vo);

	// 商品管理之搜索 带条件 分页
	public PageResult search(Integer page, Integer rows, Goods goods);

	// 查询一个GoodsVo
	public GoodsVo findOne(Long id);

	// 商品修改
	public void update(GoodsVo vo);
	
	
	//开始审核  审核通过  驳回
	public void updateStatus(Long[] ids,String status);
	//删除(批量)
	public void delete(Long[] ids);

}
