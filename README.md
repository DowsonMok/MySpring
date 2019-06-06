

### 前言

都说研究spring源码会很有收获，它的设计思想以及它核心的抽象都非常值得我们去学习，可是尝试看源码看的头晕，看过一些大佬们造出来简易版的spring,那是一个羡慕啊

所以我也尝试做了一个，过程很痛苦，但不得不说，收获不是一般大，实现过程中我体会到了设计模式，面向对象设计原则，重构代码能力等的重要性，以及领悟到了为何人们常说spring接口设计的粒度非常细致。

如果你想研究spring源码，探究Spring的设计思想，MySpring会是一个很好打的开胃菜。

**参考资料:**<<spring揭秘>>、网上的技术文章等



### MySpring具备功能

1.IOC :支持根据XML(支持构造器注入)和注解方式装配Bean

2.AOP:实现了Before、AfterReturning、AfterThrowing等Advice，支持根据	   JDK动态代理和CGLIB两种方式创建动态代理


### 类图
以下是主要的类图，并不包含全部
![iamge](https://github.com/sunnyColten/MySpring/blob/master/images/main.png)
