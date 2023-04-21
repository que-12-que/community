package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findById(int id){
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
//        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();
        if (user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        // 验证账号是否存在
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }
        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }

        // 如果信息都没问题，则保存信息且加密密码
        // 获取长度为5的随机字符串
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        // 拼接到用户设置的密码上用md5进行加密
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        // 初始化新用户，即设置初始值
        user.setType(0);
        user.setStatus(0);
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        // 激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation" + "/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        // 将内容填入模板中
        String content = templateEngine.process("/mail/activation", context);
        // 发送方邮箱、主题、内容
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }
    public Map<String,Object> login(String username, String password, long expiredSeconds) {
        Map<String,Object> map = new HashMap<>();
        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        // 如果没有激活
        if (user.getStatus() == 0) {
            map.put("usernameMsg","该账号未激活！");
            return map;
        }
        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg","密码不正确！");
            return map;
        }
        // 登录成功，生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);
        // 将凭证存入redis
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);
        // 将登录凭证放入map中
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        // 先取出来更改状态后存回去
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
//        loginTicketMapper.updateStatus(ticket,1);
    }

//    // 忘记密码，发送验证码到用户邮箱
//    public Map<String, Object> sendKaptcha(String email){
//        Map<String,Object> map = new HashMap<>();
//        // 根据邮箱查询用户
//        User user = userMapper.selectByEmail(email);
//        if (user == null) {
//            map.put("emailMsg","该邮箱不存在！");
//            return map;
//        }
//        // 发送激活邮件
//        Context context = new Context();
//        context.setVariable("kaptcha", user.getEmail());
//        // http://localhost:8080/community/activation/101/code
//        String url = domain + contextPath + "/activation" + "/" + user.getId() + "/" + user.getActivationCode();
//        context.setVariable("url",url);
//        // 将内容填入模板中
//        String content = templateEngine.process("/mail/activation", context);
//        // 发送方邮箱、主题、内容
//        mailClient.sendMail(user.getEmail(),"激活账号",content);
//        return map;
//    }

    // 重置密码
    public Map<String,Object> resetPassword(String email,String password) {

        Map<String,Object> map = new HashMap<>();

        if (StringUtils.isBlank(email)) {
            map.put("emailMsg","未填写邮箱！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg","未填写密码！");
            return map;
        }

        // 先判断验证码是否正确
        /**
         * 怎么判断???
         * 在Controller中通过session获取判断
         */

        // 判断邮箱是否存在
        // 根据邮箱查询用户
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg","该邮箱不存在！");
            return map;
        }


        // 如果邮箱存在且验证码正确，则更新密码
        userMapper.updatePassword(user.getId(), CommunityUtil.md5(password + user.getSalt()));
        clearCache(user.getId());
        return map;
    }

    public LoginTicket findLoginTicket(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
//        return loginTicketMapper.selectByTicket(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    // 更新头像链接
    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId,headerUrl);
        clearCache(userId);
        return rows;
//        return userMapper.updateHeader(userId,headerUrl);
    }
    // 更新密码
    public Map<String,Object> updatePassword(int userId, String oldPassword, String newPassword, String confirmPassword) {
        Map<String,Object> map = new HashMap<>();
        if (oldPassword == null) {
            map.put("oldPasswordMsg", "请输入密码！");
            return map;
        }
        if (newPassword == null) {
            map.put("newPasswordMsg", "请输入新密码！");
            return map;
        }
        if (confirmPassword == null) {
            map.put("confirmPasswordMsg", "请输入确认密码！");
            return map;
        }
        if (!newPassword.equals(confirmPassword)) {
            map.put("passwordError","新密码与确认密码不一致！");
            return map;
        }
        User user = userMapper.selectById(userId);
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("passwordError","密码错误！");
            return map;
        }
        // 如果原始密码正确则更新密码
        userMapper.updatePassword(userId,CommunityUtil.md5(newPassword + user.getSalt()));
        clearCache(userId);
        return map;
    }
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }
    // 3.数据变更时清楚缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    // 查询用户权限
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }
}
