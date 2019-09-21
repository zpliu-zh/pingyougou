package cn.itcast.core.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.service.ItemsearchService;

/**
 * 搜索管理
 * @author lx
 *
 */
@RestController
@RequestMapping("/itemsearch")
public class ItemsearchController {
	
	@Reference
	private ItemsearchService itemsearchService;
	
	//搜索 入参:Map 返回值Map
	@RequestMapping("/search")
	public Map<String,Object> search(@RequestBody Map<String,String> searchMap){
		return itemsearchService.search(searchMap);
	}

}
