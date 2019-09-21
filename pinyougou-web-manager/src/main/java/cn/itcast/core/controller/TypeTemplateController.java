package cn.itcast.core.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TypeTemplateService;
import entity.PageResult;
import entity.Result;

/**
 * 模板管理
 * @author lx
 *
 */
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

	
	@Reference
	private TypeTemplateService typeTemplateService;
	//查询
	@RequestMapping("/search")
	public PageResult search(Integer page,Integer rows,@RequestBody TypeTemplate typeTemplate){
		return typeTemplateService.search(page, rows, typeTemplate);
	}
	//添加
	@RequestMapping("/add")
	public Result add(@RequestBody TypeTemplate tt){
		try {
			//添加
			typeTemplateService.add(tt);
			
			return new Result(true,"提交成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"提交失败");
		}
		
		
	}
}
