package com.open.capacity.oss.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FileExtend implements  Serializable{
    private static final long serialVersionUID = -3542330889450919312L;
    //  md5字段
    private String id;
    // 文件分片id
    private String guid;
    //  原始文件名
    private String name;
    //	文件大小
    private long size;
    //  冗余字段
    private String path;
    //	oss访问路径 oss需要设置公共读
    private String url;
    //	FileType字段
    private String source;
    //	主文件id
    private String fileId;
    private Date createTime;
}
