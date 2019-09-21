package cn.itcast.core.service;

import java.util.Map;

public interface PayService {
	
	//生成二维码之前 先去微信服务器那边要收款地址
	public Map<String,String> createNative();
	
	//查询
	public Map<String,String> queryPayStatus(String out_trade_no);

}
