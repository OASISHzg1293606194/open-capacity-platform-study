package com.open.capacity.oss.dao;

import com.open.capacity.oss.model.FileExtend;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileExtendDao {

  

    @Insert("insert into file_info_extend(id, guid, name,  size, path, url, source, file_id,create_time) "
            + "values(#{id}, #{guid}, #{name}, #{size}, #{path}, #{url}, #{source}, #{fileId},#{createTime})")
    int save(FileExtend fileExtend);

    @Select("select id, guid, name,  size, path, url, source, file_id fileId ,create_time createTime from file_info_extend t where t.id = #{id} ")
    FileExtend findById(String id);
    
    @Select("select id, guid, name,  size, path, url, source, file_id fileId ,create_time createTime from file_info_extend t where t.guid = #{guid} order by create_time")
    List<FileExtend> findByGuid(String guid);

    int batchUpdateSelective(List<FileExtend> fileExtends);


}
