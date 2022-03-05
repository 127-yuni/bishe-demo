# 我的第一个项目
 1. 这是一个社区论坛 有搜索，提问，点赞，评论，相关问题推荐，登录等功能。
 2. 项目用到    ***Springboot*** ***mybatis*** ***redis*** ***SpringSercurity***
    

##开发步骤
### 1,bootstrap获取样式并修改 并通过git上传到github仓库 
### 2,实现登录功能：在底部寻找api   用Oauth模式进行登录功能搭建，后来用了整合第三方登录的仓库
### 3,[原理](file:///C:/Users/%E6%96%87%E8%89%BA%E5%A7%94%E5%91%98/Pictures/Screenshots/1.png)

###存在问题
1. 事先没有对整个项目框架进行构建，比如模块间之间的联系不明确，导致了
    经常对整体重新构建，效率低下;
2.重新配置maven,先随意导入一个项目，再修改配置：
(1)File->New Projects Settings->Settings for New Projects
   ->Build,Execution, Deployment->Build Tools->Maven:
    1)Maven home directory:'修改为maven的安装路径';
    2)User settings file:'修改为setting.xml配置文件的路径';
    3)Local repository:'修改为你创建的库文件路径'
    4)点击apple->ok
(2)File->settings->Build,Execution, Deployment->Build Tools->Maven:
    1)Maven home directory:'修改为maven的安装路径';
    2)User settings file:'修改为setting.xml配置文件的路径';
    3)Local repository:'修改为你创建的库文件路径'
    4)点击apple->ok
(3)修改jdk:File->Project Structure->project->Project SDK->修改为可用版本；
参考文献：(1)https://blog.csdn.net/huo920/article/details/82082403
        (2)https://blog.csdn.net/fn0723/article/details/81160156
        (3)https://blog.csdn.net/ITvegetable/article/details
        /112424069?utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.no_search_link&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.no_search_link
3.导入自己的项目，发现包的依赖有问题：
(1)在idea首页 Confige-->setting-->build,execution,deployment-->buildTool-->maven-->work office勾选,apply,ok
(2)进入导入的项目中,先点击右侧Maven Project-->lifecycle-->install,下载jar包;
(3)下载完毕后,关闭项目,到首页Confige-->setting-->build,execution,deployment-->buildTool-->maven-->work office取消勾选,apply,ok
(4)进入项目,就解决问题了
参考文献:https://blog.csdn.net/weixin_42420249/article/details/81191861

其他问题:Plugin 'org.springframework.boot:spring-boot-maven-plugin:' not found
   解决方案：通过给spring-boot-maven-plugin指定版本
参考文献：https://blog.csdn.net/qq_45351320/article/details/105591664
    

###正在完成
1. 使用redis的bitmap对评论数进行优化
2.使用redis的hyperloglog记录UV数
 
     
 








