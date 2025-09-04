package com.aoao.xiaoaoshu.auth.runner;

import cn.hutool.core.collection.CollUtil;

import com.aoao.framework.common.util.JsonUtil;
import com.aoao.framework.common.constant.RedisKeyConstants;
import com.aoao.xiaoaoshu.auth.domain.entity.PermissionDO;
import com.aoao.xiaoaoshu.auth.domain.mapper.PermissionDOMapper;
import com.aoao.xiaoaoshu.auth.domain.mapper.RoleDOMapper;
import com.aoao.xiaoaoshu.auth.model.dto.RolePermissionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author aoao
 * @create 2025-08-30-20:14
 */
@Component
@Slf4j
public class PushRolePermissions2RedisRunner implements CommandLineRunner {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RoleDOMapper roleDOMapper;
    @Autowired
    private PermissionDOMapper permissionDOMapper;

    // 权限同步标记 Key
    private static final String PUSH_PERMISSION_FLAG = "push.permission.flag";
    @Override

    public void run(String... args) throws Exception {
        try {
            // 是否能够同步数据: 原子操作，只有在键 PUSH_PERMISSION_FLAG 不存在时，才会设置该键的值为 "1"，并设置过期时间为 1 天
            boolean canPushed = stringRedisTemplate.opsForValue().setIfAbsent(PUSH_PERMISSION_FLAG, "1", 1, TimeUnit.DAYS);
            // 如果无法同步权限数据
            if (!canPushed) {
                log.warn("==> 角色权限数据已经同步至 Redis 中，不再同步...");
                return;
            }
            // 1.查询出所有已启用的角色
            List<Long> roleIds = roleDOMapper.selectEnabledList();
            // 2.查询角色拥有的权限
            if (CollUtil.isNotEmpty(roleIds)){
                List<RolePermissionDTO> rolePermissionDTOS = permissionDOMapper.selectByRoleIds(roleIds);
                // 以roleId分类
                HashMap<Long, List<PermissionDO>> map = new HashMap<>();
                for (RolePermissionDTO rolePermissionDTO : rolePermissionDTOS){
                    Long roleId = rolePermissionDTO.getRoleId();
                    // 创建permission
                    PermissionDO permissionDO = new PermissionDO();
                    BeanUtils.copyProperties(rolePermissionDTO, permissionDO);
                    map.computeIfAbsent(roleId, k -> new ArrayList<>()).add(permissionDO);
                }
                // 3.同步redis
                map.forEach((roleId, permissions) -> {
                    String key = RedisKeyConstants.buildRolePermissionsKey(roleId);
                    stringRedisTemplate.opsForValue().set(key, JsonUtil.toJson(permissions));
                });
            }
            log.info("==> 服务启动，成功同步角色权限数据到 Redis 中...");
        } catch (Exception e) {
            log.error("==> 同步角色权限数据到 Redis 中失败: ", e);
        }

    }
}
