package com.open.capacity.oss.service.impl;

import com.open.capacity.common.util.UUIDUtils;
import com.open.capacity.oss.dao.FileDao;
import com.open.capacity.oss.model.FileInfo;
import com.open.capacity.oss.model.FileType;
import com.open.capacity.oss.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.http.entity.ContentType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * 本地存储文件
 * 该实现文件服务只能部署一台 
 * 如多台机器nfs文件存储解决
 * @author pm 1280415703@qq.com
 * @date 2019/8/11 16:22
 */
  
@Service("localOssServiceImpl")
@Slf4j
public class LocalOssServiceImpl extends AbstractFileService {

	@Autowired
	private FileDao fileDao;

	@Override
	protected FileDao getFileDao() {
		return fileDao;
	}

	@Value("${file.oss.prefix:xxxxx}")
	private String urlPrefix;
	/**
	 * 网关访问路径
	 */
	@Value("${file.oss.domain:xxxxx}")
	private String domain;
	
	@Value("${file.oss.path:xxxxx}")
	private String localFilePath;

	@Override
	protected FileType fileType() {
		return FileType.LOCAL;
	}

	@Override
	protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
		int index = fileInfo.getName().lastIndexOf(".");
		// 文件扩展名
		String fileSuffix = fileInfo.getName().substring(index);

		String suffix = "/" + LocalDate.now().toString().replace("-", "/") + "/" + fileInfo.getId() + fileSuffix;

		String path = localFilePath + suffix;
		String url = domain + urlPrefix + suffix;
		fileInfo.setPath(path);
		fileInfo.setUrl(url);

		FileUtil.saveFile(file, path);
	}

	@Override
	protected boolean deleteFile(FileInfo fileInfo) {
		return FileUtil.deleteFile(fileInfo.getPath());
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
			// 临时目录用来存放所有分片文件
			String tempFileDir = filePath + File.separator + guid;
			File parentFileDir = new File(tempFileDir);
			if (!parentFileDir.exists()) {
				parentFileDir.mkdirs();
			}
			// 分片处理时，前台会多次调用上传接口，每次都会上传文件的一部分到后台
			File tempPartFile = new File(parentFileDir, tempFilePath.toString());
			FileUtils.copyInputStreamToFile(file.getInputStream(), tempPartFile);
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

		File parentFileDir = new File(filePath + File.separator + guid);

		try {
			int index = fileName.lastIndexOf(".");

			// 文件扩展名
			String fileSuffix = fileName.substring(index);
			String suffix = "/" + LocalDate.now().toString() + "/"  + UUIDUtils.getGUID32() + fileSuffix;

			File destTempFile = new File(filePath , suffix);

			FileUtil.saveBigFile(guid, parentFileDir, destTempFile);

			// TODO: 2020/6/17 保存到数据库中 LOCAL
			FileInputStream fileInputStream = new FileInputStream(destTempFile);
			MultipartFile multipartFile = new MockMultipartFile(destTempFile.getName(), destTempFile.getName(),
					ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);

			FileInfo fileInfo = FileUtil.getFileInfo(multipartFile);
			fileInfo.setName(fileName);
			FileInfo oldFileInfo = getFileDao().findById(fileInfo.getId());

			if (oldFileInfo != null) {
				destTempFile.delete();
				return oldFileInfo;
			}

			String path = localFilePath + suffix;
			String url = domain + urlPrefix + suffix;
			fileInfo.setPath(path);
			fileInfo.setUrl(url);
			fileInfo.setSource(fileType().name());// 设置文件来源
			getFileDao().save(fileInfo);// 将文件信息保存到数据库
			return  fileInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			// 删除临时目录中的分片文件
			try {
				FileUtils.deleteDirectory(parentFileDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}



}
