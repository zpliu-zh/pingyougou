package cn.itcast.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import entity.PageResult;
import entity.Result;

/**
 * 模板管理
 * @author lx
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	
	@Autowired
	private TypeTemplateDao typeTemplateDao;
	@Autowired
	private RedisTemplate redisTemplate;
	//查询
	public PageResult search(Integer page,Integer rows,TypeTemplate typeTemplate){
		
		//1:将模板对象提前放到缓存中
		List<TypeTemplate> typeTemplateList = typeTemplateDao.selectByExample(null);
		for (TypeTemplate tt : typeTemplateList) {
			//品牌列表           ///[{"id":1,"text":"联想"},{"id":3,"text":"三星"},{"id":9,"text":"苹果"},{"id":4,"text":"小米"}]
			String brandIds = tt.getBrandIds();
			List<Map> brandList = JSON.parseArray(tt.getBrandIds(),Map.class);
			redisTemplate.boundHashOps("brandList").put(tt.getId(), brandList);
			//规格列表   [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
			List<Map> specList = findBySpecList(tt.getId());
			redisTemplate.boundHashOps("specList").put(tt.getId(), specList);
		}
		PageHelper.startPage(page, rows);
		
		Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(null);
		
		return new PageResult(p.getTotal(), p.getResult());
	}
	//添加
	public void add(TypeTemplate tt){
		typeTemplateDao.insertSelective(tt);
	}
		
	//根据模板ID查询一个模板对象
	public TypeTemplate findOne(Long id){
		return typeTemplateDao.selectByPrimaryKey(id);
	}
	
	@Autowired
	private SpecificationOptionDao specificationOptionDao;
	//根据模板ID查询规格结果集
	public List<Map> findBySpecList(Long id){
		
		TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
		// [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
		String specIds = typeTemplate.getSpecIds();
		//fastjson
		List<Map> list = JSON.parseArray(specIds, Map.class);
		for (Map map : list) {
//			map.get(id)
//			map.get(text)
			SpecificationOptionQuery query = new SpecificationOptionQuery();
			query.createCriteria().andSpecIdEqualTo((long)(Integer)map.get("id"));
			List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(query);
			map.put("options", specificationOptions);
		}
//		0:Map  {"id":27,"text":"网络"}       put(id,27).put(text,网络).put(options,List)  Map 的长度是3
//		1:Map  {"id":32,"text":"机身内存"}   put(id,32).put(text,机身内存)
		return list;
	}
}
