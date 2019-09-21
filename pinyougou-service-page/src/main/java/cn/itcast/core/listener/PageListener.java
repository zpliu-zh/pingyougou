package cn.itcast.core.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.core.service.StaticPageService;

/**
 * 自定义消费处理类
 * 静态化
 * @author lx
 *
 */
public class PageListener implements MessageListener{

	@Autowired
	private StaticPageService staticPageService;
	
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
		try {
			String id = atm.getText();
			System.out.println("静态化项目接收到的ID:" + id);
			//3:静态化页面 准备出来  
			staticPageService.index(Long.parseLong(id));
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
