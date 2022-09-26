package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.entity.User;
import com.reggie.service.UserService;
import com.reggie.utils.ValidateCodeUtils;
import com.reggie.utils.duanxing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;


// /user/sendMsg'
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

//    @Value("${spring.mail.username}")
//    private String from;   // 邮件发送人

    @Autowired
    private JavaMailSender mailSender;
//    @Autowired
//    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 手机验证码登录
     */
    @Autowired
    private UserService userService;
    private duanxing send;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpSession Session){

        //获取手机号
        String phone = user.getPhone();

        if (!StringUtils.isEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务api完成发送短信
            //duanxing.send("瑞吉外卖","",phone.code);

            //需要将生成的验证码保存到session
            //Session.setAttribute(phone,code);

            //将缓存的验证码缓存到redis中，并且设置有效期为5分钟
            redisTemplate.opsForValue().set(phone,code,5,TimeUnit.MINUTES);


            return R.success("手机号验证码发送成功");
        }


        return R.error("短信发送失败");
    }


    /**
     * 移动端用户登录
     * @param map
     * @param Session
     * @return
    */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession Session){

        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从session中获取保存的验证码
        //Object codeInSession = Session.getAttribute(phone);

        //从redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //进行验证码的比对（页面提交的验证码和session中保存的验证码对比）
        if (codeInSession != null && codeInSession.equals(code)){

            //如果能够比对成功。说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if (user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就完成自动注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);

            }
            Session.setAttribute("user",user.getId());

            //如果用户登录成功，删除Redis中缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("登录失败");
    }


    /**
     * 邮箱注册
     */

    /*
    // 发送邮箱验证码
    @PostMapping("/sendMsg") // sendMsg
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //  获取邮箱账号
        String phone = user.getPhone();

        String subject = "迅磊餐购登录验证码";
        if (!StringUtils.isEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String context = "欢迎使用迅磊餐购，登录验证码为: " + code + ",五分钟内有效，请妥善保管!";

            log.info("code={}",code);

            // 真正地发送邮箱验证码
            userService.sendMsg(phone,subject,context);

            //  将随机生成的验证码保存到session中
//            session.setAttribute(phone,code);

            // 验证码由保存到session 优化为 缓存到Redis中，并且设置验证码的有效时间为 5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("验证码发送成功，请及时查看!");
        }

        return R.error("验证码发送失败，请重新输入!");
    }

    // 消费者端 用户登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        log.info("userMap:{}"+map.toString());
        // 获取登录表单的 邮箱账号
        String phone = map.get("phone").toString();
        // 获取 验证码
        String code = map.get("code").toString();

        // 从Session中 获取保存的验证码,session 邮箱账号为 key，验证码为value
//        Object codeInSession = session.getAttribute(phone);

        // 从Redis中获取缓存验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //  页面提交的验证码 和 Session中保存的验证码 进行比对
        if (codeInSession != null && codeInSession.equals(code)){
            //  验证比对无误后，可以成功登录
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if (user == null){  // 数据库中没有当前用户，即当前用户为新用户
                //  新用户 自动注册，其信息保存到数据库中
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);  // 设置用户的状态为1，表示用户可以正常使用，为0，则禁用

                userService.save(user);
            }
            // 用户保存到数据库中后，会自动生成userId,
            session.setAttribute("user",user.getId());

            // 如果用户登录成功，则删除Redis中缓存的验证码
            redisTemplate.delete(phone);

            //  需要在浏览器端保存用户信息，故返回的数据类型为 Result<User>
            return R.success(user);

        }
        return R.error("登录失败，请重新登录!");
    }*/



}
