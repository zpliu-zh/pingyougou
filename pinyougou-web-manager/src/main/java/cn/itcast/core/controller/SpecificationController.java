package cn.itcast.core.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojogroup.SpecificationVo;
import cn.itcast.core.service.SpecificationService;
import entity.PageResult;
import entity.Result;

/**
 * 规格管理
 * @author lx
 *
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {
	
	@Reference
	private SpecificationService specificationService;
	//分页查询 条件
	@RequestMapping("/search")
	public PageResult search(Integer page,Integer rows,@RequestBody Specification specification){
		return specificationService.search(page, rows, specification);
	}
	//添加
	@RequestMapping("/add")
	public Result add(@RequestBody SpecificationVo vo){
		try {
			
			//添加
			specificationService.add(vo);
			
			
			return new Result(true,"提交成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"提交失败");
		}
	}
	//修改 
	@RequestMapping("/update")
	public Result update(@RequestBody SpecificationVo vo){
		try {
			
			//修改
			specificationService.update(vo);
			
			
			return new Result(true,"提交成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"提交失败");
		}
	}
	//查询一个规格
	@RequestMapping("/findOne")
	public SpecificationVo findOne(Long id){
		return specificationService.findOne(id);
	}
	//查询品牌结果集(为了Select2)
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return specificationService.selectOptionList();
	}

}
