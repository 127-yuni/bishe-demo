package haoyu.niubi.community.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.annotation.sql.DataSourceDefinition;

@Data
@Component
public class User {
    private String uuid ;
    private String  username;
    private  String avatar;
//    用JustAuth  token 用的AuthToken 用BeanUtils token传不了
    private String  token;
    private String mail;
}
