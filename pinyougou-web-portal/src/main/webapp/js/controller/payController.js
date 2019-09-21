app.controller('payController' ,function($scope ,$location,payService){
	
	//二维码 生成
	$scope.createNative=function(){
		payService.createNative().success(
			function(response){
				
				//显示订单号和金额
				$scope.money= (response.total_fee/100).toFixed(2);
				$scope.out_trade_no=response.out_trade_no;
				
				//生成二维码
				 var qr=new QRious({
					    element:document.getElementById('qrious'),
						size:250,
						value:response.code_url,//手机上微信客户端   付钱    扫一扫 weixin://地址 引导 微信服务器端付钱
						level:'H'
			     });
				 
				 queryPayStatus();//调用查询
				
			}	
		);	
	}
	
	//调用查询
	queryPayStatus=function(){
		payService.queryPayStatus($scope.out_trade_no).success(
			function(response){
				if(response.flag){
					location.href="paysuccess.html#?money="+$scope.money;
				}else{
					if(response.message=='二维码超时'){
//						$scope.createNative();//重新生成二维码
						alert(response.message);
						location.href="payfail.html";
					}else{
						location.href="payfail.html";
					}
				}				
			}		
		);		
	}
	
	//获取金额
	$scope.getMoney=function(){
		return $location.search()['money'];
	}
	
});