package cn.itcast.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.pojo.good.BrandQuery.Criteria;
import entity.PageResult;

/**
 * 品牌管理
 * @author lx
 *
 */
@Service
@Transactional
public class BrandServiceImpl implements BrandService {

	@Autowired
	private BrandDao brandDao;
	//查询所有品牌
	public List<Brand> findAll(){
		return brandDao.selectByExample(null);
	}
	
	//分页查询
	public PageResult  findPage(Integer pageNum,Integer pageSize){
		//分页插件的使用
		PageHelper.startPage(pageNum, pageSize);
		//  分页查询
		Page<Brand>  p = (Page<Brand>) brandDao.selectByExample(null);
		// select * from tb_brand limit   开始行,每页数
		//总条数 结果集
		return new PageResult(p.getTotal(), p.getResult());
		
	}
	//分页查询 + 条件查询
	public PageResult  search(Integer pageNum,Integer pageSize,Brand brand){
		//分页插件的使用
		PageHelper.startPage(pageNum, pageSize);
		
		//条件对象
		BrandQuery brandQuery = new BrandQuery();
		Criteria createCriteria = brandQuery.createCriteria();
		//判断条件是否为NUll或是""
		if(null != brand.getName() && !"".equals(brand.getName().trim())){
			
			createCriteria.andNameLike("%"+brand.getName().trim()+"%");
		}
		if(null != brand.getFirstChar() && !"".equals(brand.getFirstChar().trim())){
			createCriteria.andFirstCharEqualTo(brand.getFirstChar().trim());
		}
		//  分页查询
		Page<Brand>  p = (Page<Brand>) brandDao.selectByExample(brandQuery);
		// select * from tb_brand limit   开始行,每页数
		//总条数 结果集
		return new PageResult(p.getTotal(), p.getResult());
		
	}
	
	////添加
	public void add(Brand brand){
		brandDao.insertSelective(brand);
	}
	//修改  update tb_brand set name = #{name} where id = 1
	public void update(Brand brand){
		brandDao.updateByPrimaryKeySelective(brand);
	}
	//删除
	public void delete(Long[] ids){
		brandDao.deleteByIds(ids);
//		for (Long id : ids) {
//			brandDao.deleteByPrimaryKey(id);
//		}
	}
	//查询一个品牌
	public Brand findOne(Long id){
		return brandDao.selectByPrimaryKey(id);
	}
	//查询品牌结果集(为了Select2)
	public List<Map> selectOptionList(){
		return brandDao.selectOptionList();
	}
}
