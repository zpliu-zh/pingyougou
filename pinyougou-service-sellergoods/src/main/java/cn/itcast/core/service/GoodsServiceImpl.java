package cn.itcast.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.good.GoodsQuery.Criteria;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojogroup.GoodsVo;
import entity.PageResult;
import entity.Result;

/**
 * 商品管理
 * 
 * @author lx
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private GoodsDao goodsDao;//
	@Autowired
	private GoodsDescDao goodsDescDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemCatDao itemCatDao;
	@Autowired
	private BrandDao brandDao;
	@Autowired
	private SellerDao sellerDao;

	// 商品修改
	public void update(GoodsVo vo) {
		// 商品表'
		goodsDao.updateByPrimaryKeySelective(vo.getGoods());
		// 商品详情表
		goodsDescDao.updateByPrimaryKeySelective(vo.getGoodsDesc());
		// 库存表
		// 1:将原来的全部删除
		ItemQuery itemQuery = new ItemQuery();
		itemQuery.createCriteria().andGoodsIdEqualTo(vo.getGoods().getId());
		itemDao.deleteByExample(itemQuery);
		// 2:将现在全部添加
		// 是否启用规格
		if ("1".equals(vo.getGoods().getIsEnableSpec())) {
			// 启用
			// 库存表 多个
			List<Item> itemList = vo.getItemList();
			for (Item item : itemList) {
				// 标题 名称 + " " + 规格 + " " + 联通3G
				// {"机身内存":"16G","网络":"联通3G"}
				String spec = item.getSpec();
				Map<String, String> specMap = JSON.parseObject(spec, Map.class);
				String title = vo.getGoods().getGoodsName();
				Set<Entry<String, String>> entrySet = specMap.entrySet();
				for (Entry<String, String> entry : entrySet) {
					title += " " + entry.getValue();
				}
				item.setTitle(title);
				//给库存表设置属性
				setAttribute(item,vo);
				// 添加库存表
				itemDao.insertSelective(item);
			}
		} else {
			// 未启用  像征性保存一个默认库存表数据
			Item item = new Item();
			//标题:
			item.setTitle(vo.getGoods().getGoodsName());
			//给库存表设置属性
			setAttribute(item,vo);
			//页面无任何传递
			//规格
			item.setSpec("{}");
			//价格
			item.setPrice(vo.getGoods().getPrice());
			//库存
			item.setNum(999999);
			//启用
			item.setStatus("1");
			//默认
			item.setIsDefault("1");
			// 添加库存表
			itemDao.insertSelective(item);
		}
	}
	// 商品添加
	public void add(GoodsVo vo) {
		// 商品表'
		// 商品ID 自增长的
		// 状态 未审核
		vo.getGoods().setAuditStatus("0");
		goodsDao.insertSelective(vo.getGoods());
		// 商品详情表
		vo.getGoodsDesc().setGoodsId(vo.getGoods().getId());
		goodsDescDao.insertSelective(vo.getGoodsDesc());

		// 是否启用规格
		if ("1".equals(vo.getGoods().getIsEnableSpec())) {
			// 启用
			// 库存表 多个
			List<Item> itemList = vo.getItemList();
			for (Item item : itemList) {
				// 标题 名称 + " " + 规格 + " " + 联通3G
				// {"机身内存":"16G","网络":"联通3G"}
				String spec = item.getSpec();
				Map<String, String> specMap = JSON.parseObject(spec, Map.class);
				String title = vo.getGoods().getGoodsName();
				Set<Entry<String, String>> entrySet = specMap.entrySet();
				for (Entry<String, String> entry : entrySet) {
					title += " " + entry.getValue();
				}
				item.setTitle(title);
				//给库存表设置属性
				setAttribute(item,vo);
				// 添加库存表
				itemDao.insertSelective(item);
			}
		} else {
			// 未启用  像征性保存一个默认库存表数据
			Item item = new Item();
			//标题:
			item.setTitle(vo.getGoods().getGoodsName());
			//给库存表设置属性
			setAttribute(item,vo);
			//页面无任何传递
			//规格
			item.setSpec("{}");
			//价格
			item.setPrice(vo.getGoods().getPrice());
			//库存
			item.setNum(999999);
			//启用
			item.setStatus("1");
			//默认
			item.setIsDefault("1");
			// 添加库存表
			itemDao.insertSelective(item);
		}
	}
	//给库存表设置属性
	public void setAttribute(Item item,GoodsVo vo){
		// 卖点
		item.setSellPoint(vo.getGoods().getCaption());
		// 商品多张图片 第一张
		String itemImages = vo.getGoodsDesc().getItemImages();
		List<Map> imagesList = JSON.parseArray(itemImages, Map.class);
		if (null != imagesList && imagesList.size() > 0) {
			item.setImage((String) imagesList.get(0).get("url"));
		}
		// 第三级商品分类ID
		item.setCategoryid(vo.getGoods().getCategory3Id());
		// 第三级商品分类名称
		item.setCategory(itemCatDao.selectByPrimaryKey(vo.getGoods().getCategory3Id()).getName());
		// 添加时间
		item.setCreateTime(new Date());
		// 更新时间
		item.setUpdateTime(new Date());
		// 商品表的ID 外键
		item.setGoodsId(vo.getGoods().getId());
		// 商家ID
		item.setSellerId(vo.getGoods().getSellerId());
		// 商家的名称
		item.setSeller(sellerDao.selectByPrimaryKey(vo.getGoods().getSellerId()).getName());
		// 品牌名称
		item.setBrand(brandDao.selectByPrimaryKey(vo.getGoods().getBrandId()).getName());
	}

	// 商品管理之搜索 带条件 分页
	public PageResult search(Integer page, Integer rows, Goods goods) {
		// 分页
		PageHelper.startPage(page, rows);
		// 排序
		PageHelper.orderBy("id desc");
		// select * from tb_goods where ... order by id desc limit 开始行,...
		GoodsQuery goodsQuery = new GoodsQuery();

		Criteria createCriteria = goodsQuery.createCriteria();
		if (null != goods.getAuditStatus() && !"".equals(goods.getAuditStatus())) {
			createCriteria.andAuditStatusEqualTo(goods.getAuditStatus());
		}
		if (null != goods.getGoodsName() && !"".equals(goods.getGoodsName().trim())) {
			createCriteria.andGoodsNameLike("%" + goods.getGoodsName().trim() + "%");
		}
		//默认对于已经删除的了商品不查询  只查询不删除的商品
		createCriteria.andIsDeleteIsNull();
		
		Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
		return new PageResult(p.getTotal(), p.getResult());
	}

	// 查询一个GoodsVo
	public GoodsVo findOne(Long id) {
		GoodsVo vo = new GoodsVo();
		// 商品表
		vo.setGoods(goodsDao.selectByPrimaryKey(id));
		// 商品详情表
		vo.setGoodsDesc(goodsDescDao.selectByPrimaryKey(id));
		// 库存表 商品表ID是外键
		ItemQuery itemQuery = new ItemQuery();
		itemQuery.createCriteria().andGoodsIdEqualTo(id);
		vo.setItemList(itemDao.selectByExample(itemQuery));
		return vo;
	}
	
	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private JmsTemplate jmsTemplate;
//	@Autowired
//	private JmsMessagingTemplate jmsMessagingTemplate;
	@Autowired
	private Destination topicPageAndSolrDestination;//发布 订阅
	@Autowired
	private Destination queueSolrDeleteDestination;//点对点
	//开始审核  审核通过  驳回
	public void updateStatus(Long[] ids,String status){
		//tb_goods  update tb_goods set status = 1 where id in (1,2,3)
		Goods goods = new Goods();
		//状态
		goods.setAuditStatus(status);
		for (final Long id : ids) {
			goods.setId(id);
			//1:更新
			goodsDao.updateByPrimaryKeySelective(goods);
			//必须是审核通过时 才会更新索引库
			if("1".equals(status)){
				//发消息 给MQ  destination 目地的   
				jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						// TODO Auto-generated method stub
						return session.createTextMessage(String.valueOf(id));
					}
				});

			}
			
		}
	
	}
	//删除(批量)
	public void delete(Long[] ids){
		Goods goods = new Goods();
		//是否删除字段改为1
		goods.setIsDelete("1");
		for (final Long id : ids) {
			goods.setId(id);
			//1:更新是否删除的字段
			goodsDao.updateByPrimaryKeySelective(goods);
			//发消息 
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					// TODO Auto-generated method stub
					return session.createTextMessage(String.valueOf(id));
				}
			});
		}
	}

}
