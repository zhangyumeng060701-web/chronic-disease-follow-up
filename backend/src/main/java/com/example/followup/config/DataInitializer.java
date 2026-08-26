package com.example.followup.config;

import com.example.followup.constant.DomainConstants;
import com.example.followup.entity.SysUser;
import com.example.followup.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createDefaultUserIfMissing("admin", "管理员", DomainConstants.ROLE_ADMIN);
        createDefaultUserIfMissing("doctor", "李医生", DomainConstants.ROLE_DOCTOR);
    }

    private void createDefaultUserIfMissing(String username, String realName, String role) {
        if (sysUserMapper.findByUsername(username) != null) {
            return;
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRealName(realName);
        user.setRole(role);
        user.setPhone("");
        user.setStatus(1);
        sysUserMapper.insert(user);
        log.info("初始化默认用户: {}", username);
    }
}
