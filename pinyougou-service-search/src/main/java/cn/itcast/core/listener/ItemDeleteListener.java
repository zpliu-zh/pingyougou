package cn.itcast.core.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

public class ItemDeleteListener implements MessageListener{


	@Autowired
	private SolrTemplate solrTemplate;
	
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
		try {
			String id = atm.getText();
			System.out.println("为了删除索引:搜索项目接收到的ID:" + id);
			
			//2: 将商品信息从索引库中删除掉  Long id tb_goods Goods  Item 外键
			Criteria criteria = new Criteria("item_goodsid").is(id);
			SolrDataQuery query = new SimpleQuery(criteria);
			solrTemplate.delete(query);
			solrTemplate.commit();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
