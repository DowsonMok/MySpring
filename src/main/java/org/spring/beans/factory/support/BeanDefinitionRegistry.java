package org.spring.beans.factory.support;

import org.spring.beans.BeanDefinition;

//提供管理BeanDefinition的方法
public interface BeanDefinitionRegistry {
	BeanDefinition getBeanDefinition(String beanID);
	void registerBeanDefinition(String beanID, BeanDefinition bd);
}
