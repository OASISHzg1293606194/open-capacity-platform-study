package com.open.capacity.oss.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.open.capacity.common.web.PageResult;
import com.open.capacity.common.web.Result;
import com.open.capacity.log.annotation.LogAnnotation;
import com.open.capacity.oss.config.OssServiceFactory;
import com.open.capacity.oss.model.FileInfo;
import com.open.capacity.oss.model.FileType;
import com.open.capacity.oss.model.MergeFileDTO;
import com.open.capacity.oss.service.FileService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51
*  文件上传 同步oss db双写 目前仅实现了阿里云,七牛云
*  参考src/main/view/upload.html
*/
@RestController
@Api(tags = "FILE API")
@Slf4j
public class FileController {

	@Autowired
	private OssServiceFactory fileServiceFactory;
	@Value("${file.oss.path}")
	private String localFilePath;


	/**
	 * 文件上传
	 * 根据fileType选择上传方式
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/files-anon")
	@LogAnnotation(module = "file-center", recordRequestParam = false)
	public FileInfo upload(@RequestParam("file") MultipartFile file) throws Exception {
		
		String fileType = FileType.QINIU.toString();
		FileService fileService = fileServiceFactory.getFileService(fileType);
		return fileService.upload(file);
	}

	/**
	 * layui富文本文件自定义上传
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/files/layui")
	@LogAnnotation(module = "file-center", recordRequestParam = false)
	public Map<String, Object> uploadLayui(@RequestParam("file") MultipartFile file )
			throws Exception {
		
		FileInfo fileInfo = upload(file);

		Map<String, Object> map = new HashMap<>();
		map.put("code", 0);
		Map<String, Object> data = new HashMap<>();
		data.put("src", fileInfo.getUrl());
		map.put("data", data);

		return map;
	}

	/**
	 * 文件删除
	 * @param id
	 */
	@DeleteMapping("/files/{id}")
	@PreAuthorize("hasAuthority('file:del')") 
	@LogAnnotation(module = "file-center", recordRequestParam = false)
	public Result delete(@PathVariable String id) {

		try{
			FileInfo fileInfo = fileServiceFactory.getFileService(FileType.QINIU.toString()).getById(id);
			if (fileInfo != null) {
				FileService fileService = fileServiceFactory.getFileService(fileInfo.getSource());
				fileService.delete(fileInfo);
			}
			return Result.succeed("操作成功");
		}catch (Exception ex){
			return Result.failed("操作失败");
		}

	}
 
	/**
	 * 文件查询
	 * @param params
	 * @return
	 * @throws JsonProcessingException 
	 */
	@GetMapping("/files")
	@PreAuthorize("hasAuthority('file:query')")
	public PageResult<FileInfo> findFiles(@RequestParam Map<String, Object> params) throws JsonProcessingException {
        
		return  fileServiceFactory.getFileService(FileType.QINIU.toString()).findList(params);

	}


	/**
	 * 	注意： 上传大文件为2个方法  bigFile 用了LOCAL,mergeFile也只能用用了LOCAL
	 * 		LOCAL:本地方式存储，指单机版本下可以使用，根据 本地文件配置 d:/uploadshp 可以上传到该目录下，并且路径为当日日期分文件夹
	 *		下载方式 就是以  WebResourceConfig 配置类 规定的一样 http://127.0.0.1:9200/api-file/statics/2020-06-28/06B323130BF34AAB88936B8918D90164.avi
	 *		根据网关地址读取 statics 文件下的文件
	 *	（特别注意，该模式下只支持单台服务器，多台服务器会有问题，因为分片会在不太服务器中，暂时无法合并多台服务器的文件，有折中的方法，做共享文件夹，这样成本太大，不建议，如果多台服务器，推荐oss存储或者分布式文件存储）
	 *
	 * 		FASTDFS:分布式文件存储，即分布式系统常用的文件存储方式，适合多台服务器，逻辑是将各个分片存入FASTDFS 的存储目录中，然后在合并方法中把文件 downloadFile 下载到本地进行合并并保存文件
	 * 		最终才是一个文件提供给用户，这里有个问题，就是操作的任务耗时太久，如果经过nginx的有可能被超时返回，建议合并方法可以做异步请求，直接丢到后台任务进行最终通过消息的方式提醒用户即可
	 *
	 *		QINIU:七牛OSS上传和FASTDFS类似一样的逻辑,也是适合多台服务器往OSS服务器上传文件，然后在合并方法中把文件保存好完整文件在上传到OSS
	 *
	 *		ALIYUN:暂时没有申请key,没有实现逻辑和七牛OSS一样
	 */
	/**
	 * 上传大文件
	 * @param file
	 * @param chunks
	 */
	@PostMapping(value = "/files-anon/bigFile")
//	@ResponseStatus(code= HttpStatus.INTERNAL_SERVER_ERROR,reason="server error")
	public Result bigFile( String guid, Integer chunk, MultipartFile file, Integer chunks){
		try {
            fileServiceFactory.getFileService(FileType.LOCAL.toString()).chunk(guid,chunk,file,chunks,localFilePath);
            return Result.succeed("操作成功");
        }catch (Exception ex){
            return Result.failed("操作失败");
        }
	}


	/**
	 * 合并文件
	 * @param mergeFileDTO
	 */
	@RequestMapping(value = "/files-anon/merge",method =RequestMethod.POST )
	public Result mergeFile(@RequestBody MergeFileDTO mergeFileDTO){
		try {
			return Result.succeed(fileServiceFactory.getFileService(FileType.LOCAL.toString()).merge(mergeFileDTO.getGuid(),mergeFileDTO.getFileName(),localFilePath),"操作成功");
		}catch (Exception ex){
			return Result.failed("操作失败");
		}
	}


	/**
	 * 上传失败
	 * @param mergeFileDTO
	 * @return
	 */
	@RequestMapping(value = "/files-anon/uploadError",method =RequestMethod.POST )
	public Result uploadError(@RequestBody MergeFileDTO mergeFileDTO){
		try {
			//使用默认的 FileService
			fileServiceFactory.getFileService(null).uploadError(mergeFileDTO.getGuid(),mergeFileDTO.getFileName(),localFilePath);
			return Result.succeed("操作成功");
		}catch (Exception ex){
			return Result.failed("操作失败");
		}
	}



}
