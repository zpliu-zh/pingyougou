package cn.itcast.core.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.service.ContentService;

/**
 * 网站前台之首页轮播图
 * @author lx
 *
 */
@RestController
@RequestMapping("/content")
public class ContentController {
	
	@Reference
	private ContentService contentService;
	
	//轮播图加载
	@RequestMapping("/findByCategoryId")
	public List<Content> findByCategoryId(Long categoryId){
		return contentService.findByCategoryId(categoryId);
	}

}
