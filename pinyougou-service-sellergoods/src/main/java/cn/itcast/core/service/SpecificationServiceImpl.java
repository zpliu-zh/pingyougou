package cn.itcast.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery.Criteria;
import cn.itcast.core.pojogroup.SpecificationVo;
import entity.PageResult;

/**
 * 规格管理
 * @author lx
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private SpecificationDao specificationDao;
	@Autowired
	private SpecificationOptionDao specificationOptionDao;
	//分页查询 条件
	public PageResult search(Integer page,Integer rows, Specification specification){
		//分页插件
		PageHelper.startPage(page, rows);
		
		SpecificationQuery query = new SpecificationQuery();
		Criteria createCriteria = query.createCriteria();
		if(null != specification.getSpecName() && !"".equals(specification.getSpecName().trim())){
			createCriteria.andSpecNameLike("%"+specification.getSpecName().trim()+"%");
		}
		//分页查询
		Page<Specification> p = (Page<Specification>) specificationDao.selectByExample(query);
		return new PageResult(p.getTotal(), p.getResult());
		
	}
	@Override
	public void add(SpecificationVo vo) {
		// TODO Auto-generated method stub
		//先保存规格表  1  主键
		specificationDao.insertSelective(vo.getSpecification());
		//再保存规格属性表  多  外键
		List<SpecificationOption> specificationOptionList = vo.getSpecificationOptionList();
		for (SpecificationOption specificationOption : specificationOptionList) {
			specificationOption.setSpecId(vo.getSpecification().getId());
			specificationOptionDao.insertSelective(specificationOption);
		}
	}
	//修改
	public void update(SpecificationVo vo){
		//规格 修改
		specificationDao.updateByPrimaryKeySelective(vo.getSpecification());
		//规格属性修改
		//1:先删除原来的规格属性
		SpecificationOptionQuery query = new SpecificationOptionQuery();
		query.createCriteria().andSpecIdEqualTo(vo.getSpecification().getId());
		specificationOptionDao.deleteByExample(query);
		//2:再添加现在的规格属性
		List<SpecificationOption> specificationOptionList = vo.getSpecificationOptionList();
		for (SpecificationOption specificationOption : specificationOptionList) {
			specificationOption.setSpecId(vo.getSpecification().getId());
			specificationOptionDao.insertSelective(specificationOption);
		}
		
	}
	//查询一个规格
	public SpecificationVo findOne(Long id){
		SpecificationVo vo = new SpecificationVo();
		//规格对象
		vo.setSpecification(specificationDao.selectByPrimaryKey(id));
		//规格属性结果集
		SpecificationOptionQuery query = new SpecificationOptionQuery();
		query.createCriteria().andSpecIdEqualTo(id);
		
		
		vo.setSpecificationOptionList(specificationOptionDao.selectByExample(query));
		return vo;
	}
	//查询品牌结果集(为了Select2)
	public List<Map> selectOptionList(){
		return specificationDao.selectOptionList();
	}
		
}
