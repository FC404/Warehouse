package com.fxm.warehouse.mapper;

import com.fxm.warehouse.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    @Select("SELECT * FROM warehouse.user WHERE username = #{username}")
    User findByUsername(String username);


    List<User> page(String username, String phone, String email, String role);

    Optional<User> findById(Long id);

    void save(User updatedUser);

    List<User> findAllById(List<Long> ids);



    void deleteAll(@Param("ids") List<Long> ids);  // 确保使用 @Param 注解

    @Select("SELECT COUNT(*) FROM warehouse.user")
    long countUsers();
}
