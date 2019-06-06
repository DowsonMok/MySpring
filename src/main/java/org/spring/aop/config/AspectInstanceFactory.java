package org.spring.aop.config;

import org.spring.beans.factory.BeanFactory;
import org.spring.beans.factory.BeanFactoryAware;
import org.spring.util.StringUtils;

public class AspectInstanceFactory implements BeanFactoryAware{
	
	private String aspectBeanName;
	
	private BeanFactory beanFactory;

	public void setAspectBeanName(String aspectBeanName) {
		this.aspectBeanName = aspectBeanName;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		if(!StringUtils.hasText(this.aspectBeanName)) {
			throw new IllegalArgumentException("'aspectBeanName' is required");
		}
	}
	
	public Object getAspectInstance() throws Exception{
		return this.beanFactory.getBean(this.aspectBeanName);
	}
}
