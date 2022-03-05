package haoyu.niubi.community.model;

import lombok.Data;

@Data
public class Comment {
    private Integer parentId;
    private  String content;
    private  Integer type;
    private  Integer id;
    private  Long gmtModified;
    private  Long gmtCreate;
    private  Integer likeCount;
    private String commentator;
    private  User user;
    private  Integer commentCount;
}
