package com.example.followup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.followup.entity.SysUser;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT * FROM t_user WHERE username = #{username} AND status = 1")
    SysUser findByUsername(@Param("username") String username);
}
