package cn.itcast.core.pojogroup;

import java.io.Serializable;
import java.util.List;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;

/**
 * 组合对象 
 *   商品对象
 *   商品详情对象
 *   库存对象
 * @author lx
 *
 */
public class GoodsVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//商品对象
	private Goods goods;
	
	//商品详情对象
	private GoodsDesc goodsDesc;
	
	//库存对象
	private List<Item> itemList;

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public GoodsDesc getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(GoodsDesc goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public List<Item> getItemList() {
		return itemList;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}
	
	
	
	

}
