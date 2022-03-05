package haoyu.niubi.community.service;

import haoyu.niubi.community.mapper.UserMapper;
import haoyu.niubi.community.model.User;
import haoyu.niubi.community.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Value("sendMailAddress")
    private String address;

    @Value("mailSecret")
    private  String secret;

    public void createOrUpdatebyAuth(AuthUser authUser) {
        User user = new User() ;
        if(userMapper.findByUuid(authUser.getUuid()) == null){
            BeanUtils.copyProperties(authUser,user);
            user.setAvatar("/images/Avatar1.png");
//            user.setToken(authUser.getToken().getAccessToken());
            user.setToken(authUser.getToken().getAccessToken());
            userMapper.insert(user);
        }
    }

    public   String getUsername(String uuid){
        return  userMapper.selectUsername(uuid);
    }

    public String  getUUID(String mail){
        return userMapper.selectUUIDByMail(mail);
    }

    public boolean sendMail(String receivingAddress){
        //验证码
        int num = new Random().nextInt(9999) + 1000;
        try{ HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.163.com");
            email.setCharset("UTF-8");
            email.addTo(receivingAddress);
            email.setFrom(address, "Oland论坛");
            email.setAuthentication("Schinappi2000@163.com",secret );
            email.setSubject("用户注册");
            email.setMsg("尊敬的用户您好,您本次注册的验证码是:" +num);
            email.send();
        }catch(Exception e){
            log.warn("发送验证码出错");
            e.printStackTrace();
            return false;
        }
        //将验证码存放到redis中，设置1分钟后失效
        redisUtil.set(receivingAddress,String.valueOf(num),60);
        return true;
    }

    public boolean checkCode(String receivingAddress,String code){
        String  msg= (String)redisUtil.get(receivingAddress);
        if(StringUtils.isEmpty(msg)||!code.equals(msg)){
                return false;
        }
        return true;
    }

    public boolean existMail(String receivingAddress) {
        Integer count = userMapper.existMail(receivingAddress);
        if(count != 0){
            return true;
        }
        return false;
    }
}
