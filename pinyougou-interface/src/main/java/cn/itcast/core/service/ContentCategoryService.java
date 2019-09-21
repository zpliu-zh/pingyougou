package cn.itcast.core.service;

import java.util.List;

import cn.itcast.core.pojo.ad.ContentCategory;
import entity.PageResult;

public interface ContentCategoryService {

	public List<ContentCategory> findAll();
	
	public PageResult findPage(ContentCategory contentCategory, Integer pageNum, Integer pageSize);
	
	public void add(ContentCategory contentCategory);
	
	public void edit(ContentCategory contentCategory);
	
	public ContentCategory findOne(Long id);
	
	public void delAll(Long[] ids);
}
