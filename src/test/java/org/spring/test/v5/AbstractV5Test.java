package org.spring.test.v5;

import java.lang.reflect.Method;

import org.spring.aop.config.AspectInstanceFactory;
import org.spring.beans.factory.BeanFactory;
import org.spring.beans.factory.support.DefaultBeanFactory;
import org.spring.beans.factory.xml.XmlBeanDefinitionReader;
import org.spring.core.io.ClassPathResource;
import org.spring.core.io.Resource;
import org.spring.tx.TransactionManager;

public class AbstractV5Test {
	
	protected BeanFactory getBeanFactory(String configFile) {
		DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(defaultBeanFactory);
		Resource resource = new ClassPathResource(configFile);
		reader.loadBeanDefinitions(resource);
		return defaultBeanFactory;
	}
	
	protected Method getAdviceMethod(String methodName) throws Exception{
		return TransactionManager.class.getMethod(methodName);
	}
	
	protected AspectInstanceFactory getAspectInstanceFactory(String targetBeanName) {
		AspectInstanceFactory factory = new AspectInstanceFactory();
		factory.setAspectBeanName(targetBeanName);
		return factory;
	}
}
