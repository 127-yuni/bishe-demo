package haoyu.niubi.community.exception;

public enum CustomizeErrorCode implements  ICustomizeErrorCode{
    QUESTION_NOT_FOUND(2001,"你找的问题不在了"),
   TARGET_PARM_NOT_FOUND(2002,"未选中问题或评论进行回复"),
   NO_LOGIN(2003,"请登录"),
     SYS_ERROR (2004,"服务器冒烟了") ,
    TYPE_PARM_WRONG (2005,"评论类型错误或不存在") ,
    COMMENT_NOT_FOUND (2006,"回复的评论不存在了" ),
    CONTENT_IS_EMPTY (2007,"输入内容不能为空" ),
    READ_NOTIFICATION_FAIL(2008, "兄弟你这是读别人的信息呢？"),
    NOTIFICATION_NOT_FOUND(2009, "消息莫非是不翼而飞了？"),
    MAIL_EXISTED(2010,"邮箱已被注册");
    ;

    private String message;
    private  Integer code;
    CustomizeErrorCode(Integer code, String message){
        this.message=message;
        this.code =code;
    }
  @Override
    public String getMessage(){
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
