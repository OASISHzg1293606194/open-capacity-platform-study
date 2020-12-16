package com.open.capacity.oss.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.tobato.fastdfs.FdfsClientConfig;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.open.capacity.oss.dao.FileDao;
import com.open.capacity.oss.dao.FileExtendDao;
import com.open.capacity.oss.model.FileExtend;
import com.open.capacity.oss.model.FileInfo;
import com.open.capacity.oss.model.FileType;
import com.open.capacity.oss.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
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
 * fastdfs存储文件
 * @author pm 1280415703@qq.com
 * @date 2019/8/11 16:22
 */
@Import(FdfsClientConfig.class)
@Service("fastDfsOssServiceImpl")
@Slf4j
public class FastDfsOssServiceImpl extends AbstractFileService {

	@Autowired
	private FileDao fileDao;

	@Autowired
	private FileExtendDao fileExtendDao;

	@Autowired
    private FastFileStorageClient storageClient;

	@Override
	protected FileDao getFileDao() {
		return fileDao;
	}
 
	/**
	 * nginx安装了fastdfs的地址
	 */
	@Value("${fdfs.oss.domain:}")
	private String domain;
	 

	@Override
	protected FileType fileType() {
		return FileType.FASTDFS;
	}

	 @Override
     protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
         StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
         fileInfo.setUrl(domain+ storePath.getFullPath());
         fileInfo.setPath(storePath.getFullPath());
     }

     @Override
     protected boolean deleteFile(FileInfo fileInfo) {
         if (fileInfo != null && StrUtil.isNotEmpty(fileInfo.getPath())) {
             StorePath storePath = StorePath.parseFromUrl(fileInfo.getPath());
             storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
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

            // TODO: 2020/6/29 fastdfs上传
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),  FilenameUtils.getExtension(tempFilePath.toString()), null);
            fileExtend.setUrl(domain+ storePath.getFullPath());
            fileExtend.setPath(storePath.getFullPath());

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
                DownloadByteArray callback = new DownloadByteArray();
                byte[] buf = storageClient.downloadFile("group1", extend.getPath().substring(extend.getPath().lastIndexOf("group1/")+7),callback);
                FileUtil.byte2File(buf,filePath + File.separator + guid,extend.getName());
            }

            FileUtil.saveBigFile(guid, parentFileDir, destTempFile);

            // TODO: 2020/6/17 保存到数据库中 FASTDFS
            FileInputStream fileInputStream = new FileInputStream(destTempFile);
            MultipartFile multipartFile = new MockMultipartFile(destTempFile.getName(), destTempFile.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);

            FileInfo fileInfo = FileUtil.getFileInfo(multipartFile);
            fileInfo.setName(fileName);
            FileInfo oldFileInfo = getFileDao().findById(fileInfo.getId());

            if (oldFileInfo != null) {
                return oldFileInfo;
            }

            StorePath storePath = storageClient.uploadFile(multipartFile.getInputStream(), multipartFile.getSize(), FilenameUtils.getExtension(multipartFile.getOriginalFilename()), null);
            fileInfo.setUrl(domain+ storePath.getFullPath());
            fileInfo.setPath(storePath.getFullPath());

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
}
