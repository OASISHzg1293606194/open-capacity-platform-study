package com.open.capacity.oss.utils;

import com.open.capacity.oss.model.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 文件工具类
*/
@Slf4j
public class FileUtil {

	public static FileInfo getFileInfo(MultipartFile file) throws Exception {
		String md5 = fileMd5(file.getInputStream());

		FileInfo fileInfo = new FileInfo();
		fileInfo.setId(md5);// 将文件的md5设置为文件表的id
		fileInfo.setName(file.getOriginalFilename());
		fileInfo.setContentType(file.getContentType());
		fileInfo.setIsImg(fileInfo.getContentType().startsWith("image/"));
		fileInfo.setSize(file.getSize());
		fileInfo.setCreateTime(new Date());

		return fileInfo;
	}

	/**
	 * 文件的md5
	 * 
	 * @param inputStream
	 * @return
	 */
	public static String fileMd5(InputStream inputStream) {
		try {
			return DigestUtils.md5Hex(inputStream);
		} catch (IOException e) {
			log.error("FileUtil->fileMd5:{}" ,e.getMessage());
		}

		return null;
	}

	public static String saveFile(MultipartFile file, String path) {
		try {
			File targetFile = new File(path);
			if (targetFile.exists()) {
				return path;
			}

			if (!targetFile.getParentFile().exists()) {
				targetFile.getParentFile().mkdirs();
			}
			file.transferTo(targetFile);

			return path;
		} catch (Exception e) {
			log.error("FileUtil->saveFile:{}" ,e.getMessage());
		}

		return null;
	}

	public static String saveBigFile(String guid ,File parentFileDir, File destTempFile) {
		try {
			if(parentFileDir.isDirectory()){
				if(!destTempFile.exists()){
					//先得到文件的上级目录，并创建上级目录，在创建文件,
					destTempFile.getParentFile().mkdir();
					try {
						//创建文件
						destTempFile.createNewFile(); //上级目录没有创建，这里会报错
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				log.info("length:{} ",parentFileDir.listFiles().length);

				for (int i = 0; i < parentFileDir.listFiles().length; i++) {
					File partFile = new File(parentFileDir, guid + "_" + i + ".part");
					FileOutputStream destTempfos = new FileOutputStream(destTempFile, true);
					//遍历"所有分片文件"到"最终文件"中
					FileUtils.copyFile(partFile, destTempfos);
					destTempfos.close();
				}
			}
		} catch (Exception e) {
			log.error("FileUtil->saveBigFile:{}" ,e.getMessage());
		}

		return null;
	}


	public static boolean deleteFile(String pathname) {
		File file = new File(pathname);
		if (file.exists()) {
			boolean flag = file.delete();

			if (flag) {
				File[] files = file.getParentFile().listFiles();
				if (files == null || files.length == 0) {
					file.getParentFile().delete();
				}
			}

			return flag;
		}

		return false;
	}

	//byte数组写到到硬盘上
	public static void byte2File(byte[] buf, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);


			if (!dir.exists() ) {
				dir.mkdirs();
			}
			file = new File(filePath + File.separator + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buf);
			log.info("byte2File -》》 成功!!!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 从网络Url中下载文件
	 * @param urlStr
	 * @param fileName
	 * @param savePath
	 * @throws IOException
	 */
	public static void downLoadByUrl(String urlStr,String savePath,String fileName){
		InputStream inputStream = null;
		FileOutputStream fos = null;
		try{
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();

			//设置超时间为3秒
			conn.setConnectTimeout(3*1000);

			//得到输入流
			inputStream = conn.getInputStream();
			//获取自己数组
			byte[] getData = readInputStream(inputStream);

			//文件保存位置
			File saveDir = new File(savePath);
			if(!saveDir.exists()){
				saveDir.mkdir();
			}
			File file = new File(saveDir + File.separator + fileName);
			fos = new FileOutputStream(file);
			fos.write(getData);

			log.info("info:"+url+" download success");
		}catch (IOException e) {
			e.printStackTrace();
			log.info("Unexpected code ");
		}finally {
			try {
				if(fos!=null){
					fos.close();
				}
				if(inputStream!=null){
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 从输入流中获取字节数组
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static  byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

}
