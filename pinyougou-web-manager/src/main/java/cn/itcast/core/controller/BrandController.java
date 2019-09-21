package cn.itcast.core.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import entity.PageResult;
import entity.Result;

/**
 * 品牌管理
 * @author lx
 *
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;
	//查询所有
	@RequestMapping("/findAll")
	public List<Brand> findAll(){
		
		return brandService.findAll();
	}
	//分页查询
	@RequestMapping("/findPage")
	public PageResult  findPage(Integer pageNum,Integer pageSize){
		//二个值: 总条数 结果集
		return brandService.findPage(pageNum, pageSize);
	}
	//分页查询 + 条件查询  
	// 400 403 404  
	//1:项目名
	//2:路径是否正确
	//3:入参是否正确     第三个入参:要求必须 有值 
	@RequestMapping("/search")
	public PageResult  search(Integer pageNum,Integer pageSize,@RequestBody Brand brand){
//			,@RequestBody(required = false) Brand brand){
		return brandService.search(pageNum, pageSize, brand);
		
	}
	//添加 {"name":"宝马","firstChar":"B"};
	@RequestMapping("/add")
	public Result  add(@RequestBody Brand brand){
		try {
			//添加
			brandService.add(brand);
			return new Result(true,"保存成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"保存失败");
		}
		
		
	}
	//修改 {"name":"宝马","firstChar":"B"};
	@RequestMapping("/update")
	public Result  update(@RequestBody Brand brand){
		try {
			//修改
			brandService.update(brand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"修改失败");
		}
	}
	//删除
	@RequestMapping("/delete")
	public Result  delete(Long[] ids){
		try {
			//真删除
			brandService.delete(ids);
			
			return new Result(true,"删除成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"删除失败");
		}
	}
	
	//查询一个品牌
	@RequestMapping("/findOne")
	public Brand findOne(Long id){
		return brandService.findOne(id);
	}
	//查询品牌结果集(为了Select2)
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return brandService.selectOptionList();
	}
}
