package com.fxm.warehouse.mapper;


import com.fxm.warehouse.pojo.dto.UserLoginDTO;
import com.fxm.warehouse.pojo.vo.UserLoginVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper {
    /**
     * 登录用户
     *
     * @param userLoginDTO
     * @return
     */
    @Select("select * from warehouse.user where username = #{username} and password = #{password} ")
    UserLoginVO login(UserLoginDTO userLoginDTO);

}
