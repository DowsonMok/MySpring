package org.spring.context;

import org.spring.beans.factory.BeanFactory;

//用于封装BeanFactory和XmlBeanDefinitionReader，让我们获取bean时不用关心底层的实现细节和处理的细节
public interface ApplicationContext extends BeanFactory{

}
