package com.open.capacity.oss.service.impl;

import com.open.capacity.common.util.UUIDUtils;
import com.open.capacity.oss.dao.FileDao;
import com.open.capacity.oss.dao.FileExtendDao;
import com.open.capacity.oss.model.FileExtend;
import com.open.capacity.oss.model.FileInfo;
import com.open.capacity.oss.model.FileType;
import com.open.capacity.oss.utils.FileUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51 
 * 七牛云oss存储文件
 */
@Service("qiniuOssServiceImpl")
@Slf4j
public class QiniuOssServiceImpl extends AbstractFileService implements InitializingBean {

	@Autowired
	private FileDao fileDao;

	@Autowired
	private FileExtendDao fileExtendDao;

	@Override
	protected FileDao getFileDao() {
		return fileDao;
	}

	@Override
	protected FileType fileType() {
		return FileType.QINIU;
	}

	@Autowired
	private UploadManager uploadManager;

	@Autowired
	private BucketManager bucketManager;

	@Autowired
	private Auth auth;

	@Value("${qiniu.oss.bucketName:xxxxx}")
	private String bucket;

	@Value("${qiniu.oss.endpoint:xxxxx}")
	private String endpoint;
	
	 
	
	private StringMap putPolicy;

	/**
	 * 获取上传凭证
	 * 
	 * @return
	 */
	private String getUploadToken() {
		return this.auth.uploadToken(bucket, null, 3600, putPolicy);
	}

	@Override
	protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
		String fileName = file.getOriginalFilename();
		// 检查文件后缀格式
		String fileEnd = fileName.substring(
				fileName.lastIndexOf(".") + 1)
				.toLowerCase();
		String fileId = UUIDUtils.getGUID32();
		StringBuffer tempFilePath = new StringBuffer();
		tempFilePath.append(fileId).append(".").append(fileEnd);

		try {
			// 调用put方法上传
			uploadManager.put(file.getBytes(),  tempFilePath.toString() , auth.uploadToken(bucket));
			// 打印返回的信息
		} catch (Exception e) {
		}
		fileInfo.setUrl(endpoint+"/"+ tempFilePath);
		fileInfo.setPath(endpoint+"/"+ tempFilePath);
		

	}

	@Override
	protected boolean deleteFile(FileInfo fileInfo) {
		try {
			Response response = bucketManager.delete(this.bucket, fileInfo.getPath());
			int retry = 0;
			while (response.needRetry() && retry++ < 3) {
			    response = bucketManager.delete(bucket, fileInfo.getPath());
			}
		} catch (QiniuException e) {
			return false ;
		}
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
	protected void chunkFile(String guid, Integer chunk, MultipartFile file, Integer chunks,String filePath)throws Exception {

		log.info("guid:{},chunkNumber:{}",guid,chunk);
		if(Objects.isNull(chunk)){
			chunk = 0;
		}

		// TODO: 2020/6/16 从RequestContextHolder上下文中获取 request对象
		boolean isMultipart = ServletFileUpload.isMultipartContent(((ServletRequestAttributes)
				RequestContextHolder.currentRequestAttributes()).getRequest());
		if (isMultipart) {
			StringBuffer tempFilePath = new StringBuffer();
			tempFilePath.append(guid).append("_").append(chunk).append(".part");

			FileExtend fileExtend = new FileExtend();
			String md5 = FileUtil.fileMd5(file.getInputStream());
			fileExtend.setId(md5);
			fileExtend.setGuid(guid);
			fileExtend.setSize(file.getSize());
			fileExtend.setName(tempFilePath.toString());
			fileExtend.setSource(fileType().name());
			fileExtend.setCreateTime(new Date());

			FileExtend oldFileExtend = fileExtendDao.findById(fileExtend.getId());
			if (oldFileExtend != null) {
				return;
			}

			try {
				// 调用put方法上传
				uploadManager.put(file.getBytes(),  tempFilePath.toString() , auth.uploadToken(bucket));
				// 打印返回的信息
			} catch (Exception e) {
			}
			fileExtend.setUrl(endpoint+"/"+ tempFilePath.toString() );
			fileExtend.setPath(endpoint+"/"+ tempFilePath.toString() );

			fileExtendDao.save(fileExtend);
		}
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
		// 得到 destTempFile 就是最终的文件
		log.info("guid:{},fileName:{}",guid,fileName);

		//根据guid 获取 全部临时分片数据
		List<FileExtend> fileExtends = fileExtendDao.findByGuid(guid);
		log.info("fileExtends -> size ：{}",fileExtends.size());

		File parentFileDir = new File(filePath + File.separator + guid);
		File destTempFile = new File(filePath , fileName);
		try {
			if (CollectionUtils.isEmpty(fileExtends)){
				return null;
			}

			// TODO: 2020/6/29 下载到本地进行操作
			for (FileExtend extend:  fileExtends) {
				// TODO: 2020/6/30 下载
				FileUtil.downLoadByUrl(extend.getUrl(),filePath + File.separator + guid,extend.getName());
			}

			FileUtil.saveBigFile(guid, parentFileDir, destTempFile);

			// TODO: 2020/6/17 保存到数据库中 QINIU
			FileInputStream fileInputStream = new FileInputStream(destTempFile);
			MultipartFile multipartFile = new MockMultipartFile(destTempFile.getName(), destTempFile.getName(),
					ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);

			FileInfo fileInfo = FileUtil.getFileInfo(multipartFile);
			fileInfo.setName(fileName);
			FileInfo oldFileInfo = getFileDao().findById(fileInfo.getId());

			if (oldFileInfo != null) {
				return oldFileInfo;
			}

			// 检查文件后缀格式
			String fileEnd = fileName.substring(
					fileName.lastIndexOf(".") + 1)
					.toLowerCase();
			String fileId = UUIDUtils.getGUID32();
			StringBuffer tempFilePath = new StringBuffer();
			tempFilePath.append(fileId).append(".").append(fileEnd);

			try {
				// 调用put方法上传
				uploadManager.put(multipartFile.getBytes(),  tempFilePath.toString() , auth.uploadToken(bucket));
				// 打印返回的信息
			} catch (Exception e) {
			}
			fileInfo.setUrl(endpoint+"/"+ tempFilePath.toString() );
			fileInfo.setPath(endpoint+"/"+ tempFilePath.toString() );

			fileInfo.setSource(fileType().name());// 设置文件来源
			getFileDao().save(fileInfo);// 将文件信息保存到数据库

			// TODO: 2020/6/29 更新分片文件的FileId
			fileExtends.stream().forEach(vo->vo.setFileId(fileInfo.getId()));
			fileExtendDao.batchUpdateSelective(fileExtends);
			return  fileInfo;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}finally {
			// 删除临时目录中的分片文件
			try {
				destTempFile.delete();
				FileUtils.deleteDirectory(parentFileDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.putPolicy = new StringMap();
		putPolicy.put("returnBody",
				"{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
	}

	 
}
