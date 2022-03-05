package haoyu.niubi.community.controller;

import com.xkcoding.http.util.StringUtil;
import haoyu.niubi.community.dto.ResultDTO;
import haoyu.niubi.community.exception.CustomizeErrorCode;
import haoyu.niubi.community.mapper.UserMapper;
import haoyu.niubi.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthAlipayRequest;
import me.zhyd.oauth.request.AuthDingTalkRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Value("sendMailAddress")
    private String address;

    @Value("mailSecret")
    private String secret;


    @GetMapping("/logOut")
    public String logOut(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @RequestMapping("/sendCode")
    public String toSendCode(@RequestParam("receivingAddress") String receivingAddress, Model model) {
        if (StringUtil.isEmpty(receivingAddress)) {
            model.addAttribute("error", "邮箱不能为空");
            return "register";
        }
        if (!userService.sendMail(receivingAddress)) {
            model.addAttribute("error", "验证码发送失败");
        }
        return "register";
    }

    /**
     * @description: 用户注册
     * @param: receivingAddress ：接收方邮箱
     * @return:
     */
    @RequestMapping("/register")
    public String toRegister(@RequestParam("receivingAddress") String receivingAddress,
                             @RequestParam("name")String name,
                             @RequestParam("code") String code, Model model) {
        //若邮箱已注册
        if (userService.existMail(receivingAddress)) {
            model.addAttribute("error", "邮箱已注册");
            return "register";
        }
        if(!userService.checkCode(receivingAddress,code)){
            model.addAttribute("error","验证码错误");
            return "register";
        }
        AuthUser user = new AuthUser();
        user.setUsername(name);
        user.setEmail(receivingAddress);
        user.setUuid(UUID.randomUUID().toString().replace("-", "").toLowerCase());
        userService.createOrUpdatebyAuth(user);
        return "index";
    }
    @RequestMapping("/login")
    public String toLogin(@RequestParam("receivingAddress") String receivingAddress,
                          @RequestParam("code") String code, Model model,HttpServletResponse httpServletResponse) throws IOException {
        if (!userService.existMail(receivingAddress)) {
            model.addAttribute("error", "邮箱未注册");
            return "login";
        }
        if(!userService.checkCode(receivingAddress,code)){
            model.addAttribute("error","验证码错误");
            return "login";
        }
        String uuid = userService.getUUID(receivingAddress);
        if(StringUtil.isEmpty(uuid)){
            model.addAttribute("error", "用户查询失败");
            return "login";
        }
        Cookie cookie = new Cookie("token",uuid);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
        return "index"; 
    }

    //    第三方登录
    @RequestMapping("/oauth/render/dingding")
    public void logDingTalk(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequestDingDing();
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/oauth/render/ali")
    public void logAli(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequestAli();
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/oauth/callback/dingding")
    public void dingLogin(AuthCallback callback, HttpServletResponse httpServletResponse, HttpServletRequest request) throws IOException {
        AuthRequest authRequest = getAuthRequestDingDing();
        AuthResponse<AuthUser> response = authRequest.login(callback);
        if (response.ok()) {
            userService.createOrUpdatebyAuth(response.getData());
            Cookie cookie = new Cookie("token", response.getData().getUuid());
            cookie.setPath("/");
            httpServletResponse.addCookie(cookie);
        }
        httpServletResponse.sendRedirect("/");
    }

    @RequestMapping("/oauth/callback/ali")
    public void aliLogin(AuthCallback callback, HttpServletResponse httpServletResponse, HttpServletRequest request) throws IOException {
        AuthRequest authRequest = getAuthRequestDingDing();
        AuthResponse<AuthUser> response = authRequest.login(callback);
        if (response.ok()) {
            userService.createOrUpdatebyAuth(response.getData());
            Cookie cookie = new Cookie("token", response.getData().getUuid());
            cookie.setPath("/");
            httpServletResponse.addCookie(cookie);
        }
        httpServletResponse.sendRedirect("/");
    }


    private AuthRequest getAuthRequestDingDing() {
        return new AuthDingTalkRequest(AuthConfig.builder()
                .clientId("ding0qrpou5m1qofqklw")
                .clientSecret("I0JOQa5uh-V0dnZqy2vXx8rsEQmxw2A9WlSwpNY7d1pZlLh-15IlImN83QNq4gL1")
                .redirectUri("http://118.31.251.28:8832/oauth/callback/dingding")
                .build());
    }

    private AuthRequest getAuthRequestAli() {
        return new AuthAlipayRequest(AuthConfig.builder()
                .clientId("2021002188685442")
                .clientSecret("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCMfYajbrCJAElZihT74RfhpxISF9FglU9bfaX8rBQIS6S7/utRCpCNJ93ovi4988Qd7ssNFvBlXdhLS6hoC77bKfpJLM6VfulXbLJzhaBS+mMksGcfLrTyYrwtHO9+lsw7prmV7CsjeEs2gqr3/LAWB5ZlP56nc80GIIzfnTX7FrMp6xD6RLHpxehFs1q8GH5409/O7Rka7Uiims18fdkQBlQjJhMC88lsj6As1bEmDa8QFLZV0eg2tGq5X7UI2KtJjsPWU1NOmGZNnYKlozAImpjrry1s8Yt4Zu9dJMzvbZ1yHJ3N0oJ4REHO1t6lkKpufptZGxUpqzMxxL2IePktAgMBAAECggEATTetKeTr6NEcf7dSY0LL8n32g7VNoo1OadX8xoz6Ebkp2R56zjdFTiBMauEk3249K8qVFiO2i7Bx8qip7k4OX72J0HBtN2Pi8udOigo3HYR7u62tcEkmsVatYQpXZDeUF3FC7fE5oKw8gsB9IgJj8ifHdOzr0dPnK2f2pe1tvwxfdMcPG+/WjQSxN8RJ572rXaPdoUdFIm0EprVvlEX347SWOP+8MqXBCU0mQUDv/41Ehg3Trn//ZO5/K8UsKyY18hy/pJs918u01ovzJ032bcpbYfbikgxQ4MaQBaCBh73POo2OCuDG4nkXd1/uVdiZypiraYtlIT97AC/lS5cJ7QKBgQDaMM+aKC82WjsC17p/Ws8fZPX0nMx5ZoPgiH5IuMWhJtdrOfitKS7rdFsd5QPOkQ9pJqBsXUpuKP3XpMj9Foaz1jNesKJ1cRqYYBDuFfMeVtXs/Pvou96sW6LACmmyJKnbls59c/zcFoAyO6TDl3oS4KXBWNX0ZScNfE/+gaVHWwKBgQCk1da/aUVQ8PaO1cXYcdPZkF9FTAEuAKd4on5Sl2deT+doOlxJyFQwOaUNBRWHAyuvogM2PyVzfo0ebpFNsCMuR09qHMs1gDytxGFHmvHCvN7wDrjr8o7f1/8ILwsFeCpEsX1FDikv19HgkPJ1L+knE+5Qhbusq7kHSUgamoOwFwKBgBJbjPwBaYd/K/vfPre8YjTX/8Gm96U3NN1NXqKruSUH6ZQB+qRHFMWGsqvfOuTEW7GTKGg1qS9/j2v3V1nD7W1wFj5fSivrajtrycDeY+gnkjQwTNmWBPQneZgCyEXOw+PP2mu/uISj+AFB92jlc8Xm1MthuA6p/BEsZmaZbdiFAoGAfxAT3TMx6qYE+bASNbnw+3oN5qYjFqoMbrKbu+DYHtxrXm32bC3Pr+kPsQylPpEUIOw7m5prrS71a2sB+Sf4xpjTv9F4V8KFg9Gox4DKMjpThCtlIwS0XBrtjgptbYbm1lMXZJ744td0Aq7ZJ7qJ+MSlZU7hj8ZD+DjmKxPGxXsCgYEAgA2KI2L86lt9c1B5s4s1lzjk4rLvD+SibPpBfdKo5an4IybyutM2O2BJzg7KbkmCa+t9ZTb3SbaYrBbAHXtjJLLJi1OdVLNqf5TcGKZnki+NyqNqd0uEeZGMR3V7LX9yGJ5KMDeqlHMEwO0F0aFqivrnKyCcclzO37kf901tm30=")
                .alipayPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlQdPRa7Ke6E4dkksVDLWgXoU6NJtB4ziUnj0FnzjYIIs/Sg7L85/CrFEYdjUtzT+qUlLdRKZogY5iAYPpmnB2A02x53FEYz3/kHWbjuLni9axi8+YrDy5fK15hF2W2K1kZ5Cf/4T+rdPmYJ9AXkNMjr/9trunkZyzjC+QMrNmeNRHbue0lEumwYnwFYY7XgtPm7KjKPADkiTxFZTQssO8jhKt4iobFIpMHzUoaGtInl+lVv24DPxuj36Nlmdr/VNB90gL7689RHyVJlQ791eBe5BGEAPFbOynzKqc8RKoRHb1N2T4qA5z5zh31FroHj/WTVbQtdMhkztUkqy8O9YfQIDAQAB")
                .redirectUri("https://118.31.251.28/oauth/callback/ali")
                .build());
    }

}
