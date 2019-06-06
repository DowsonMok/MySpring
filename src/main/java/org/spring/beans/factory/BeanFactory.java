package org.spring.beans.factory;

import java.util.List;

import org.spring.aop.Advice;

public interface BeanFactory {

	Object getBean(String beanID);
	
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	List<Object> getBeansByType(Class<?> type);

}
