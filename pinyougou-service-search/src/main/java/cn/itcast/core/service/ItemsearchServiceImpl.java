package cn.itcast.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;

import cn.itcast.core.pojo.item.Item;

/**
 * 搜索管理
 * @author lx
 *
 */
@Service
public class ItemsearchServiceImpl implements ItemsearchService {

	//索引库
	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate;
	
	//根据搜索条件  
	//关键词  过滤条件  排序条件....
	public Map<String,Object> search(Map<String,String> searchMap){
		//返回值对象
		Map<String,Object> resultMap = new HashMap<>();
		
		//将关键词处理一下    三  星       手    机   
		searchMap.put("keywords", searchMap.get("keywords").replaceAll(" ", ""));
		
		
		//1:先根据关键词商品分类
		List<String> categoryList = findCategoryListByKeywords(searchMap);
		resultMap.put("categoryList", categoryList);
		
		if(null != categoryList && categoryList.size() > 0){
			//2:根据第一个商品分类 去查询品牌
			//3:根据第一个商品分类 去查询规格
			Map<String, Object> m = findBrandListAndSpecListByCategory(categoryList.get(0));
			resultMap.putAll(m);
		}
		
		//普通搜索
//		Map<String, Object> map = ptsearch(searchMap);
		//4:高亮搜索
		Map<String, Object> map = highlightSearch(searchMap);
		
		resultMap.putAll(map);
		
		return resultMap;
	}
	//2:根据第一个商品分类 去查询品牌
	//3:根据第一个商品分类 去查询规格
	public Map<String,Object> findBrandListAndSpecListByCategory(String category){
		//返回值对象
		Map<String,Object> map = new HashMap<>();
		Object typeId = redisTemplate.boundHashOps("itemCat").get(category);
		
		//品牌结果集
		List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
		//规格结果集
		List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
		map.put("brandList", brandList);
		map.put("specList", specList);
		return map;
		
	}
	
	//1:先根据关键词商品分类(索引库) 分组查询
	public List<String> findCategoryListByKeywords(Map<String,String> searchMap){
		List<String> categoryList = new ArrayList<>();
		//1:关键词
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		Query query = new SimpleQuery(criteria);
		//设置要分组的域
		GroupOptions options = new GroupOptions();
		options.addGroupByField("item_category");
		query.setGroupOptions(options);
		//执行查询
		GroupPage<Item> page = solrTemplate.queryForGroupPage(query, Item.class);
		
		GroupResult<Item> groupResult = page.getGroupResult("item_category");
		Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
		List<GroupEntry<Item>> content = groupEntries.getContent();
		for (GroupEntry<Item> groupEntry : content) {
			categoryList.add(groupEntry.getGroupValue());
		}
		return categoryList;
	}
	
	//高亮搜索
	public Map<String,Object> highlightSearch(Map<String,String> searchMap){
		//返回值对象
		Map<String,Object> resultMap = new HashMap<>();
		//1:关键词
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		HighlightQuery highlightQuery = new SimpleHighlightQuery(criteria);
		//2:设置高亮 
		HighlightOptions options = new HighlightOptions();
		options.addField("item_title");
		options.setSimplePrefix("<span style='color:red'>");
		options.setSimplePostfix("</span>");
		highlightQuery.setHighlightOptions(options);
		//3: 过滤条件查询
		//定义搜索对象的结构  category:商品分类
	//商品分类
		if(null != searchMap.get("category") && !"".equals(searchMap.get("category"))){
			FilterQuery filterQuery = new SimpleFilterQuery(new Criteria("item_category").is(searchMap.get("category")));
			highlightQuery.addFilterQuery(filterQuery);
		}
		//品牌
		if(null != searchMap.get("brand") && !"".equals(searchMap.get("brand"))){
			FilterQuery filterQuery = new SimpleFilterQuery(new Criteria("item_brand").is(searchMap.get("brand")));
			highlightQuery.addFilterQuery(filterQuery);
		}
		//规格
		if(null != searchMap.get("spec") && !"".equals(searchMap.get("spec"))){
			Map<String,String> specMap = JSON.parseObject(searchMap.get("spec"), Map.class);
			Set<Entry<String, String>> entrySet = specMap.entrySet();
			for (Entry<String, String> entry : entrySet) {
				FilterQuery filterQuery = new SimpleFilterQuery
						(new Criteria("item_spec_" + entry.getKey()).is(entry.getValue()));
				highlightQuery.addFilterQuery(filterQuery);
			}
		}
		//价格区别
		if(null != searchMap.get("price") && !"".equals(searchMap.get("price"))){
			String[] p = searchMap.get("price").split("-");
			// 0-500
			//3000-*
			if(searchMap.get("price").contains("*")){
				
				FilterQuery filterQuery = new SimpleFilterQuery(
						new Criteria("item_price").greaterThanEqual(p[0]));
				highlightQuery.addFilterQuery(filterQuery);
			}else{
				FilterQuery filterQuery = new SimpleFilterQuery(
						new Criteria("item_price").
						between(p[0], p[1], true, true));
				highlightQuery.addFilterQuery(filterQuery);
			}
		}

		//4:排序
		if(null != searchMap.get("sortField") && !"".equals(searchMap.get("sortField"))){
			
			if("ASC".equals(searchMap.get("sort"))){
				Sort sort = new Sort(Sort.Direction.ASC,"item_" + searchMap.get("sortField"));
				highlightQuery.addSort(sort);
			}else{
				Sort sort = new Sort(Sort.Direction.DESC,"item_" + searchMap.get("sortField"));
				highlightQuery.addSort(sort);
			}
		}
//		$scope.searchMap={'pageNo':1,'pageSize':40,'sort':'DESC','sortField':'updatetime'};
		
		//开始行
		Integer starRow = (Integer.parseInt(searchMap.get("pageNo")) - 1)*Integer.parseInt(searchMap.get("pageSize"));
		highlightQuery.setOffset(starRow);
		//每页数
		highlightQuery.setRows(Integer.parseInt(searchMap.get("pageSize")));
		//执行查询
		HighlightPage<Item> page = solrTemplate.queryForHighlightPage(highlightQuery, Item.class);
		
		List<HighlightEntry<Item>> highlighted = page.getHighlighted();
		for (HighlightEntry<Item> highlightEntry : highlighted) {
			if(null != highlightEntry.getHighlights() && highlightEntry.getHighlights().size() >0){
				Item item = highlightEntry.getEntity();
				item.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));
			}
		}
	
		
		
		//总条数
		resultMap.put("total", page.getTotalElements());
		//总页数
		resultMap.put("totalPages", page.getTotalPages());
		resultMap.put("rows", page.getContent());//Shift + Ctrl+ I
		return resultMap;
	}
	//普通搜索
	public Map<String,Object> ptsearch(Map<String,String> searchMap){
		//返回值对象
		Map<String,Object> resultMap = new HashMap<>();
		
		//1:关键词
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		Query query = new SimpleQuery(criteria);
		//2:.....
		//执行查询
		ScoredPage<Item> page = solrTemplate.queryForPage(query, Item.class);
		//总条数
		//结果集
		List<Item> itemList = page.getContent();
		resultMap.put("rows", itemList);
		return resultMap;
	}
	
}
