package cn.itcast.core.service;

import java.util.List;
import java.util.Map;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojogroup.SpecificationVo;
import entity.PageResult;

public interface SpecificationService {

	// 分页查询 条件
	public PageResult search(Integer page, Integer rows, Specification specification);

	// 添加
	public void add(SpecificationVo vo);

	// 查询一个规格
	public SpecificationVo findOne(Long id);

	// 修改
	public void update(SpecificationVo vo);
	
	//查询品牌结果集(为了Select2)
	public List<Map> selectOptionList();

}
