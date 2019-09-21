package cn.itcast.core.service;

import java.util.List;

import cn.itcast.core.pojo.ad.Content;
import entity.PageResult;

public interface ContentService {

	public List<Content> findAll();

	public PageResult findPage(Content content, Integer pageNum, Integer pageSize);

	public void add(Content content);

	public void edit(Content content);

	public Content findOne(Long id);

	public void delAll(Long[] ids);

	// 轮播图加载
	public List<Content> findByCategoryId(Long categoryId);
}
