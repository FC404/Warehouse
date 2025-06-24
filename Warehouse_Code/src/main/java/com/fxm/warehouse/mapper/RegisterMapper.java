package com.fxm.warehouse.mapper;


import com.fxm.warehouse.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RegisterMapper {

    /**
     * 添加用户
     * @param user
     */
    @Insert("insert into warehouse.user(username, password,phone,email,role) values " +
            "(#{username},#{password},#{phone},#{email},#{role})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")  // 添加这行，获取自增主键
    void insert(User user);

    /**
     * 检查用户名是否已存在
     * @param username
     * @return
     */
    @Select("SELECT COUNT(*) FROM warehouse.user WHERE username = #{username}")
    boolean existsByUsername(String username);
}
