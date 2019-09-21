package cn.itcast.core.controller;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.itcast.common.utils.FastDFSClient;
import entity.Result;

/**
 * 上传图片
 * @author lx
 *
 */
@RestController
@RequestMapping("/upload")
public class UploadController {
	
	//硬编码
	@Value("${FILE_SERVER_URL}")
	private String fsu;

	//上传图片
	@RequestMapping("/uploadFile")
	public Result uploadFile(MultipartFile file){
		
		try {
			System.out.println(file.getOriginalFilename());//e1.jpg
			
			//上传图片到FastDFS上 并返回路径
			FastDFSClient fastDFSClient = 
					new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
			
			//扩展名
			String ext = FilenameUtils.getExtension(file.getOriginalFilename());
			//http://192.168.200.128/
            //	group1/M00/00/01/wKjIgFuR6J2AOZ9JAAa2MYATxeo347.jpg
			String path = fastDFSClient.uploadFile(file.getBytes(), ext, null);
			
			
			return new Result(true, fsu + path);
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"上传失败");
		}
	}
}
