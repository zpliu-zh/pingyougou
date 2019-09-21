package cn.itcast.core.service;

import java.util.Map;

public interface ItemsearchService {
	
	
	//根据搜索条件  
	//关键词  过滤条件  排序条件....
	public Map<String,Object> search(Map<String,String> searchMap);

}
