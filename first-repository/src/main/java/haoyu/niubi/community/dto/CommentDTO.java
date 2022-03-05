package haoyu.niubi.community.dto;

import haoyu.niubi.community.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Integer parentId;
    private  String content;
    private  Integer type;
    private  Integer id;
    private  Long gmtModified;
    private  Long gmtCreate;
    private  Integer likeCount;
    private Integer commentator;
    private User user;
    private  Integer commentCount;
}
