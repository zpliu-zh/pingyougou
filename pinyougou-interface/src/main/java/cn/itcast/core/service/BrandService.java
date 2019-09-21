package cn.itcast.core.service;

import java.util.List;
import java.util.Map;

import cn.itcast.core.pojo.good.Brand;
import entity.PageResult;

public interface BrandService {

	// 查询所有品牌
	public List<Brand> findAll();

	// 分页查询
	public PageResult findPage(Integer pageNum, Integer pageSize);

	// 分页查询 + 条件查询
	public PageResult search(Integer pageNum, Integer pageSize, Brand brand);

	//// 添加
	public void add(Brand brand);

	// 查询一个品牌
	public Brand findOne(Long id);

	// 修改 update tb_brand set name = #{name} where id = 1
	public void update(Brand brand);

	// 删除
	public void delete(Long[] ids);

	// 查询品牌结果集(为了Select2)
	public List<Map> selectOptionList();

}
