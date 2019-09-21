package cn.itcast.core.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.service.PayService;
import entity.Result;

/**
 * 支付管理
 * @author lx
 *
 */
@RestController
@RequestMapping("/pay")
public class PayController {
	
	@Reference
	private PayService payService;
	
	//生成二维码之前 先去微信服务器那边要收款地址
	@RequestMapping("/createNative")
	public Map<String,String> createNative(){
		return payService.createNative();
		
	}
	//查询
	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no){
		try {
			//给你5分钟
			int x = 0;
			while (true) {
				//调用查询程序
				Map<String, String> map = payService.queryPayStatus(out_trade_no);
				if(map.get("trade_state").equals("NOTPAY")){
					Thread.sleep(3000);
					x++;
					if(x >100){
						//调用关闭订单API
						return new Result(false,"二维码超时");
					}
				}else{
					return new Result(true,"付款成功");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"付款失败");
		}
		
	}
	

}
