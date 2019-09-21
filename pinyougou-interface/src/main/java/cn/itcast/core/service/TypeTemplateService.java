package cn.itcast.core.service;

import java.util.List;
import java.util.Map;

import cn.itcast.core.pojo.template.TypeTemplate;
import entity.PageResult;

public interface TypeTemplateService {
	
	//查询
	public PageResult search(Integer page,Integer rows,TypeTemplate typeTemplate);
	
	//添加
	public void add(TypeTemplate tt);
	
	//根据模板ID查询一个模板对象
	public TypeTemplate findOne(Long id);
	
	//根据模板ID查询规格结果集
	public List<Map> findBySpecList(Long id);

}
