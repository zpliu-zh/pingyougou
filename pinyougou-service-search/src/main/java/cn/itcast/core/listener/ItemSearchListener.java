package cn.itcast.core.listener;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
/**
 * 接收消息 并自定义处理消息
 * @author lx
 *
 */
public class ItemSearchListener implements MessageListener{

	@Autowired
	private ItemDao itemDao;
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
		try {
			String id = atm.getText();
			System.out.println("搜索项目接收到的ID:" + id);
			//将此ID对应的商品信息保存到索引库
			//2:将上面的商品信息保存到索引库 
			//商品表ID  tb_goods tb_goods_desc    tb_item(库存表) (外键商品表ID)
			ItemQuery itemQuery = new ItemQuery();
			itemQuery.createCriteria().andGoodsIdEqualTo(Long.parseLong(id)).andStatusEqualTo("1").andIsDefaultEqualTo("1");
			List<Item> itemList = itemDao.selectByExample(itemQuery);
			solrTemplate.saveBeans(itemList, 1000);
			
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
