package cn.itcast.core.service;

import java.util.Date;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;

/**
 * 用户管理
 * 用户注册
 * 用户登陆
 * @author lx
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination smsDestination;
	@Autowired
	private UserDao userDao;
	//发送短信验证码
	public void sendCode(final String phone){

//			1:生成验证码 (6位)   手机号 
			final String randomNumeric = RandomStringUtils.randomNumeric(6);
//			2:将验证码保存到缓存中(设置时间)
			redisTemplate.boundValueOps(phone).set(randomNumeric);
//			redisTemplate.boundValueOps(phone).expire(1, TimeUnit.MINUTES);
			jmsTemplate.send(smsDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					// TODO Auto-generated method stub
					MapMessage map = session.createMapMessage();
//					手机号
					map.setString("phone", phone);
//					签名(注册) 
					map.setString("signName", "品优购商城");
//					模板ID
					map.setString("templateCode", "SMS_126462276");
				   //模板入参:验证码  ${number}
					map.setString("templateParam", "{'number':"+randomNumeric+"}");
					return map;
				}
			});


	}
	
	//完成注册 添加
	public void add(User user,String smscode){
		//判断验证码是否正确
		String sms = (String) redisTemplate.boundValueOps(user.getPhone()).get();
		if(null != sms && sms.equals(smscode)){
			//验证码是正确的
			//用户添加
//			DigestUtils.md5Hex(user.getPassword().getBytes());
			
			//时间
			user.setUpdated(new Date());
			user.setCreated(new Date());
			
			userDao.insertSelective(user);
		}else{
			//验证码不正确
			throw new RuntimeException("验证码不正确");
		}
	}
}
