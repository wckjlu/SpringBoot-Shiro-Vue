package com.heeexy.example.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.heeexy.example.dao.LoginDao;
import com.heeexy.example.service.LoginService;
import com.heeexy.example.service.PermissionService;
import com.heeexy.example.util.CommonUtil;
import com.heeexy.example.util.constants.Constants;
import com.heeexy.example.util.jwt.JWTUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: hxy
 * @description: 登录service实现类
 * @date: 2017/10/24 11:53
 */
@Service
public class LoginServiceImpl implements LoginService {
    private Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private LoginDao loginDao;
    @Autowired
    private PermissionService permissionService;

    /**
     * 登录表单提交
     *
     * @param jsonObject
     * @return
     */
    @Override
    public JSONObject authLogin(JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        JSONObject user = loginDao.getUser(username, password);
        JSONObject returnData = new JSONObject();
        if (user == null) {
            returnData.put("result", "fail");
        } else {
            returnData.put("result", "success");
            String value = JWTUtil.sign(username);
            logger.info("jwt:    " + value);
            returnData.put("token", value);
        }
        return CommonUtil.successJson(returnData);
    }

    /**
     * 根据用户名和密码查询对应的用户
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @Override
    public JSONObject getUser(String username, String password) {
        return loginDao.getUser(username, password);
    }

    /**
     * 查询当前登录用户的权限等信息
     *
     * @return
     */
    @Override
    public JSONObject getInfo() {
        String username =  SecurityUtils.getSubject().getPrincipal().toString();
        JSONObject returnData = new JSONObject();
        JSONObject userPermission = permissionService.getUserPermission(username);
        returnData.put("userPermission", userPermission);
        return CommonUtil.successJson(returnData);
    }

    /**
     * 退出登录
     * 应该redis内删除用户的登录信息
     *
     * @return
     */
    @Override
    public JSONObject logout() {
        return CommonUtil.successJson();
    }
}
