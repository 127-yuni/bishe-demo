package haoyu.niubi.community.interceptor;

import haoyu.niubi.community.mapper.UserMapper;
import haoyu.niubi.community.model.User;
import haoyu.niubi.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class SessionInterceptor implements HandlerInterceptor {
@Autowired
    UserMapper userMapper;

@Autowired
private NotificationService notificationService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    User user = userMapper.findByUuid(token);
                    if (user != null) {
                        HttpSession session = request.getSession();
//                        设置session在服务器端生存时间
                        session.setMaxInactiveInterval(3*60);
                        session.setAttribute("user", user);
                        String unreadCount = notificationService.unreadCount(user.getUuid());
                        session.setAttribute("unreadCount",unreadCount);
                    }
                    cookie.setMaxAge(0);
                    break;
                }
            }
       }
        return  true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
