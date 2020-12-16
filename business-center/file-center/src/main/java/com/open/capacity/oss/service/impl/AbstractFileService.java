package com.open.capacity.oss.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.open.capacity.common.web.PageResult;
import com.open.capacity.oss.dao.FileDao;
import com.open.capacity.oss.model.FileInfo;
import com.open.capacity.oss.model.FileType;
import com.open.capacity.oss.service.FileService;
import com.open.capacity.oss.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51
 * AbstractFileService 抽取类
 * 根据filetype 实例化具体oss对象
*/
@Slf4j
public abstract class AbstractFileService implements FileService {

	protected abstract FileDao getFileDao();

	/**
	 * 文件来源
	 *
	 * @return
	 */
	protected abstract FileType fileType();

	/**
	 * 上传文件
	 *
	 * @param file
	 * @param fileInfo
	 */
	protected abstract void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception;
	/**
	 * 删除文件资源
	 *
	 * @param fileInfo
	 * @return
	 */
	protected abstract boolean deleteFile(FileInfo fileInfo);

	/**
	 * 上传大文件
	 *		分片上传 每片一个临时文件
	 * @param file
	 * @return
	 */
	protected abstract void chunkFile( String guid, Integer chunk, MultipartFile file, Integer chunks,String filePath) throws Exception;

	/**
	 * 合并分片文件
	 *		每一个小片合并一个完整文件
	 * @param fileName
	 * @return
	 */
	protected abstract FileInfo mergeFile( String guid,String fileName,String filePath ) throws Exception;

	/**
	 * 失败回调
	 * @param guid
	 * @param fileName
	 * @param filePath
	 * @throws Exception
	 */
//	protected abstract void uploadError( String guid,String fileName,String filePath ) throws Exception;

	protected static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	@Override
	public FileInfo upload(MultipartFile file  ) throws Exception {
		FileInfo fileInfo = FileUtil.getFileInfo(file);
		FileInfo oldFileInfo = getFileDao().findById(fileInfo.getId());
		if (oldFileInfo != null) {
			return oldFileInfo;
		}

		if (!fileInfo.getName().contains(".")) {
			throw new IllegalArgumentException("缺少后缀名");
		}

		uploadFile(file, fileInfo);

		fileInfo.setSource(fileType().name());// 设置文件来源
		getFileDao().save(fileInfo);// 将文件信息保存到数据库
//		// 本地保存文件
//		FileUtil.saveFile(file,fileInfo.getPath());
		log.info("上传文件：{}", fileInfo);

		return fileInfo;
	}


	@Override
	public void delete(FileInfo fileInfo) {
		deleteFile(fileInfo);
		getFileDao().delete(fileInfo.getId());
		log.info("删除文件：{}", fileInfo);
	}

	@Override
	public FileInfo getById(String id){
		return getFileDao().findById(id);
	}

	@Override
	public PageResult<FileInfo> findList(Map<String, Object> params){
		//设置分页信息，分别是当前页数和每页显示的总记录数【记住：必须在mapper接口中的方法执行之前设置该分页信息】
        PageHelper.startPage(MapUtils.getInteger(params, "page"),MapUtils.getInteger(params, "limit"),true);

        List<FileInfo> list = getFileDao().findList(params);
        PageInfo<FileInfo> pageInfo = new PageInfo<>(list);
		return PageResult.<FileInfo>builder().data(pageInfo.getList()).code(0).count(pageInfo.getTotal()).build();
	}

	@Override
	public void unZip(String filePath, String descDir) throws RuntimeException {

	}


	@Override
	public void chunk(String guid, Integer chunk, MultipartFile file, Integer chunks,String filePath) throws Exception {
		// TODO: 2020/6/16  分片提交
		chunkFile(guid,chunk,file,chunks,filePath);
	}

	@Override
	public FileInfo merge(String guid, String fileName, String filePath) throws Exception {
		 return mergeFile(guid,fileName,filePath);
	}

	@Override
	public void uploadError(String guid, String fileName, String filePath) throws Exception {
		File parentFileDir = new File(filePath + File.separator + guid);
		try {
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
