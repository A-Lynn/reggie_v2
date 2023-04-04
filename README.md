# 基于SpringBoot、Vue、MyBatisPlus等框架的外卖点餐系统
#### 介绍
黑马程序员SpringBoot课程的项目，实现了前后端分离，b站视频链接BV13a411q753。 使用JDK1.8编译运行成功，代码无bug，下载可用。 本项目适用于练习SpringBoot框架和前端代码，对于前后端传参方式的理解有一定要求，后续代码优化Redis等我会放在v2仓库中，本仓库供其他同学学习借鉴，代码中大量个人注释能帮助同学们思考。

#### 项目的配置文件
1、yml的配置要仔细修改，数据库的username、password改成自己的，下面的reggie/path也要自己修改，里面存放的是菜品图片

2、本人实现了阿里云的短信验证码功能，自己练习的时候不一定非得实现，想要实现的话注意修改utils工具类包中的SMSUtils的参数accessKeyId、accessKeySecret

3、sql和image数据模型放在对应的文件夹中自行导入

4、！！！执行用户端登陆点击获取验证码的时候，idea控制台会报错，是因为accessKeyId、accessKeySecret没设置好，要是设置好了不会爆红，还有SMS短信模板别忘记改

#### 提示
个人建议本项目一定要结合视频一起理解学习，老师讲解很细致，少许的Bug在弹幕中也有解决方案。

#### v2相较于v1的优化设计
1、加入redis缓存验证码，设置生存周期

2、Redis缓存菜品数据，在高并发背景下，减少服务器对数据库的访问量，增强服务端的响应能力。先访问redis缓存，redis没有再查MySQL数据

3、Spring Cache，利用注解实现缓存套餐信息

4、主从数据库Master（写）、Slave（读）实现读写分离，降低单点数据库的压力————后续实现，个人IP不够，可以尝试利用linux的mysql作为从库

