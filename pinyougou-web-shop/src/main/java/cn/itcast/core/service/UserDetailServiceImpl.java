package cn.itcast.core.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import cn.itcast.core.pojo.seller.Seller;

/**
 * 自定义实现类  帮SpringSecurity从Mysql数据库查询用户名及密码的实现类
 * 面向接口
 * @author lx
 *
 */
public class UserDetailServiceImpl implements UserDetailsService{

	private SellerService sellerService;
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		//通过用户名去查询此用户的相关信息
		Seller seller = sellerService.findSellerById(username);
		if(null != seller){
			//判断审核状态
			if("1".equals(seller.getStatus())){
				//审核通过
				Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
				authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
				return new User(seller.getSellerId(),seller.getPassword(),authorities);
			}
		}
		return null;
	}

}
