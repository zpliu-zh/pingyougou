package cn.itcast.core.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import entity.PageResult;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private ContentDao contentDao;

	@Override
	public List<Content> findAll() {
		List<Content> list = contentDao.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Content> page = (Page<Content>) contentDao.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(Content content) {
		contentDao.insertSelective(content);
	}

	@Override
	public void edit(Content content) {
		//清除缓存
		// Map content
		// content.put(广告类型ID 1,广告结果集)
		// content.put(广告类型ID 2,广告结果集)
		// content.put(广告类型ID 3,广告结果集)
		// content.put(广告类型ID 4,广告结果集)
		// content.put(广告类型ID 5,广告结果集)
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		//判断修改是广告类型ID吗?
		Content c = contentDao.selectByPrimaryKey(content.getId());
		if(!c.getCategoryId().equals(content.getCategoryId())){
			redisTemplate.boundHashOps("content").delete(c.getCategoryId());
		}
		//修改Mysql
		contentDao.updateByPrimaryKeySelective(content);
		
		
	}

	@Override
	public Content findOne(Long id) {
		Content content = contentDao.selectByPrimaryKey(id);
		return content;
	}

	@Override
	public void delAll(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				contentDao.deleteByPrimaryKey(id);
			}
		}
	}
	
	@Autowired
	private RedisTemplate redisTemplate;

	// 轮播图加载
	/**
	 * 广告  整个网站任何页面 都有广告  600 位置 每个位置 N多个
	 * String  {""}
	 * 
	 * 
	 */
	public List<Content> findByCategoryId(Long categoryId) {
		
		//1:先去缓存查询
		List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
		if(null == contentList || contentList.size() <= 0){
			//3:没有  从Mysql数据库查询 再保存缓存一份  直接返回
			ContentQuery contentQuery = new ContentQuery();
			//外键 广告类型的ID
			contentQuery.createCriteria().andCategoryIdEqualTo(categoryId)
			.andStatusEqualTo("1");//启用的广告
			//排序
			contentQuery.setOrderByClause("sort_order desc");
			
			contentList = contentDao.selectByExample(contentQuery);
			redisTemplate.boundHashOps("content").put(categoryId, contentList);
			//设置存在时间
//		redisTemplate.boundHashOps("content").expire(60, TimeUnit.DAYS);
		}
		return contentList;
		
	}

}
