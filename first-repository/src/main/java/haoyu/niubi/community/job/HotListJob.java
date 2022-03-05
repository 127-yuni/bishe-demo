package haoyu.niubi.community.job;/*
 * @author ：朱浩宇
 * @date ：2021/10/17
 *
 * */
import haoyu.niubi.community.service.QuestionService;
import haoyu.niubi.community.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class HotListJob {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private RedisUtil redisUtil;

    Logger logger = LoggerFactory.getLogger(HotListJob.class);

    /**
     * ；
     *
     * @description: 每小时执行一次
     */
    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    public void hotList() {
        logger.info("热点排行开始查询");
        //将所有问题的热度存进redis的zset
        questionService.getAll().stream().forEach(question -> {
            double hotNum = questionService.countHot(question);
            redisUtil.zAdd("hotSet",question.getId(),-hotNum);
        });
        //设定取前5     0-4
        questionService.hot(4L);
        logger.info("热点排行查询结束");
    }

}
