package cn.itcast.core.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 静态化页面处理实现类
 * @author lx
 *
 */
@Service
public class StaticPageServiceImpl implements StaticPageService,ServletContextAware {

	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@Autowired
	private GoodsDao goodsDao;
	@Autowired
	private ItemCatDao itemCatDao;
	@Autowired
	private GoodsDescDao goodsDescDao;
	@Autowired
	private ItemDao itemDao;
	
	//模板+数据==输出 静态化程序
	//  流读     -->    流写  
	
	public void index(Long id){
		Configuration conf = freeMarkerConfigurer.getConfiguration();
		//静态化后的页面的输出路径
		String path =  getPath("/" + id + ".html");
		//数据
		Map<String,Object> root = new HashMap<>();
		//TODO
		//商品对象
		Goods goods = goodsDao.selectByPrimaryKey(id);
		root.put("goods", goods);
		//商品分类1
		root.put("itemCat1", itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName());
		//商品分类2
		root.put("itemCat2", itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName());
		//商品分类3
		root.put("itemCat3", itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());
		//商品详情对象
		GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
		//json格式的字符串   转成对象   [{color:红色,url:http://192.16....},{color:红色,url:http://192.16....},{color:红色,url:http://192.16....}]
		//String itemImages = goodsDesc.getItemImages();
		root.put("goodsDesc", goodsDesc);
		//库存表集合
		ItemQuery itemQuery = new ItemQuery();
		itemQuery.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1");
		List<Item> itemList = itemDao.selectByExample(itemQuery);
		root.put("itemList", itemList);
		
		
		//输出流
		Writer out = null;
		try {
			//已经有模板目录了   读
			Template template = conf.getTemplate("item.ftl");
			//输出流  写  字符流 字节流
			out = new OutputStreamWriter(new FileOutputStream(new File(path)), "UTF-8");
			//处理
			template.process(root, out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(null != out){
					out.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//获取Webapp全路径的方法
	public String getPath(String path){
		return servletContext.getRealPath(path);
	}


	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		this.servletContext = servletContext;
	}
}
