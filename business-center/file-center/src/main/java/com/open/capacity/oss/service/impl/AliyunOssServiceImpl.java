package com.open.capacity.oss.service.impl;

import com.aliyun.oss.OSSClient;
import com.open.capacity.oss.dao.FileDao;
import com.open.capacity.oss.model.FileInfo;
import com.open.capacity.oss.model.FileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51
*  阿里云oss存储文件
*/
@Service("aliyunOssServiceImpl")
@Slf4j
public class AliyunOssServiceImpl extends AbstractFileService {

	@Autowired
	private FileDao fileDao;

	@Override
	protected FileDao getFileDao() {
		return fileDao;
	}

	@Override
	protected FileType fileType() {
		return FileType.ALIYUN;
	}

	@Autowired
	private OSSClient ossClient;

	@Value("${aliyun.oss.bucketName:xxxxx}")
	private String bucketName;
	@Value("${aliyun.oss.domain:xxxxx}")
	private String domain;

	@Override
	protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
		ossClient.putObject(bucketName, fileInfo.getName(), file.getInputStream());
		fileInfo.setUrl(domain + "/" + fileInfo.getName());
	}

	@Override
	protected boolean deleteFile(FileInfo fileInfo) {
		ossClient.deleteObject(bucketName, fileInfo.getName());
		return true;
	}

	/**
	 * 上传大文件
	 * 分片上传 每片一个临时文件
	 *
	 * @param guid
	 * @param chunk
	 * @param file
	 * @param chunks
	 * @return
	 */
	@Override
	protected void chunkFile( String guid, Integer chunk, MultipartFile file, Integer chunks,String filePath) throws Exception {

	}


	/**
	 * 合并分片文件
	 * 每一个小片合并一个完整文件
	 *
	 * @param guid
	 * @param fileName
	 * @param filePath
	 * @return
	 */
	@Override
	protected FileInfo mergeFile(String guid, String fileName, String filePath) throws Exception {
		return null;
	}
}
