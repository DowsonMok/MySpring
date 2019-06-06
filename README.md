

### 前言
转眼六月了啊 也快要离开校园步入江湖，最近都是在复习以前学的东西，这个MySpring是去年花了很多时间码出来的，现在重新温习下，也把代码放到了gitHub上

都说研究spring源码会很有收获，它的设计思想以及它核心的抽象都非常值得我们去学习，可是尝试看源码看的头晕，看过一些大佬们造出来简易版的spring,那是一个羡慕啊

所以我也尝试做了一个，过程很痛苦，但不得不说，收获不是一般大，实现过程中我体会到了设计模式、面向对象设计原则以及重构代码能力的重要性，还领悟到了为何人们常说spring接口设计的粒度非常细致的原因。

如果你想研究spring源码，探究Spring的设计思想，MySpring会是一个很好打的开胃菜。

**参考资料:**<<spring揭秘>>、网上的技术文章等



### MySpring具备功能

1.IOC :支持根据XML(支持构造器注入)和注解方式装配Bean

2.AOP:实现了Before、AfterReturning、AfterThrowing等Advice，支持根据	   JDK动态代理和CGLIB两种方式创建动态代理


### 类图
以下是主要的类图，并不包含全部
![iamge](https://github.com/sunnyColten/MySpring/blob/master/images/main.png)
