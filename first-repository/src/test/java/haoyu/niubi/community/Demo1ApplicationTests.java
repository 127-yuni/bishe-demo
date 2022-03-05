package haoyu.niubi.community;


import com.auth0.jwt.interfaces.DecodedJWT;
import haoyu.niubi.community.CommunityApplication;
import haoyu.niubi.community.model.User;
import haoyu.niubi.community.utils.JwtUtils;
import haoyu.niubi.community.utils.RedisUtil;
import lombok.AllArgsConstructor;
import org.testng.annotations.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes= CommunityApplication.class)
public class Demo1ApplicationTests {

    @Test
    void contextLoads() {
    }




}
