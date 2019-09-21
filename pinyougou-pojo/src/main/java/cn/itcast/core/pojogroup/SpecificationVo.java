package cn.itcast.core.pojogroup;

import java.io.Serializable;
import java.util.List;

import cn.itcast.core.pojo.specification.Specification;
/**
 * 包装 组合对象
 * @author lx
 *
 */
import cn.itcast.core.pojo.specification.SpecificationOption;
public class SpecificationVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//规格
	private Specification specification;//1
	//规格属性对象
	private List<SpecificationOption> specificationOptionList;//多
	
	
	
	
	public Specification getSpecification() {
		return specification;
	}
	public void setSpecification(Specification specification) {
		this.specification = specification;
	}
	public List<SpecificationOption> getSpecificationOptionList() {
		return specificationOptionList;
	}
	public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
		this.specificationOptionList = specificationOptionList;
	}
	
	

}
