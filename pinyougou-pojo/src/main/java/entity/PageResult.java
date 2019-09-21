package entity;
import java.io.Serializable;
import java.util.List;
/**
 * 分页结果封装对象
 * @author Administrator
 *
 */
public class PageResult implements Serializable{
	/**
	 * 运行时无任务影响   
	 * 编译时有影响 :编译速度慢  编译慢
	 */
	private static final long serialVersionUID = 1L;
	private Long total;//总记录数
	private List rows;//当前页结果		
	public PageResult(Long total, List rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	
	
}
