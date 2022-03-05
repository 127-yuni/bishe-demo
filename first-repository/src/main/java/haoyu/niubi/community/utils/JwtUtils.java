package haoyu.niubi.community.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import haoyu.niubi.community.model.User;
import haoyu.niubi.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class JwtUtils {

     @Autowired
     UserService userService;
//创建token
    public   String getToken(User user){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,5);//有效期为5天
        JWTCreator.Builder builder = JWT.create();
        String token = builder.withClaim("uuid", user.getUuid())
                                              .withExpiresAt(instance.getTime())
                                              .sign(Algorithm.HMAC256(user.getUsername()));
        return token;
    }
    //验证request传来的token
    public  DecodedJWT verify(String token,String uuid){
        String username = userService.getUsername(uuid);
        JWTVerifier build = JWT.require(Algorithm.HMAC256(username)).build();
        DecodedJWT verify = build.verify(token);
        return  verify;
    }
}
