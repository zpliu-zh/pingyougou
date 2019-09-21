package cn.itcast.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;

/**
 * 商品分类管理
 * @author lx
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private ItemCatDao itemCatDao;
	@Autowired
	private RedisTemplate redisTemplate;
	
	public List<ItemCat> findByParentId(Long parentId){
	
		//1:直接查询Mysql数据 提交放到缓存 前台 所有
		List<ItemCat> itemList = findAll();
		//分析数据结构:
		for (ItemCat itemCat : itemList) {
			redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
		}
		// 查询Mysql数据  只一级商品分类
		ItemCatQuery itemCatQuery = new ItemCatQuery();
		itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
		return itemCatDao.selectByExample(itemCatQuery) ;
	}
	//通过商品分类Id查询模板Id
	public ItemCat findOne(Long id){
		return itemCatDao.selectByPrimaryKey(id);
	}
	//查询所有商品分类
	public List<ItemCat> findAll(){
		return itemCatDao.selectByExample(null);
	}
}
