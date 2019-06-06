package org.spring.aop.config;

import java.lang.reflect.Method;

import org.spring.beans.BeanUtils;
import org.spring.beans.factory.BeanFactory;
import org.spring.beans.factory.BeanFactoryAware;
import org.spring.beans.factory.FactoryBean;
import org.spring.util.StringUtils;

public class MethodLocatingFactory implements FactoryBean<Method>,BeanFactoryAware{
	
	private String targetBeanName;

	private String methodName;

	private Method method;
	
	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public void setBeanFactory(BeanFactory beanFactory) {
		if (!StringUtils.hasText(this.targetBeanName)) {
			throw new IllegalArgumentException("Property 'targetBeanName' is required");
		}
		if (!StringUtils.hasText(this.methodName)) {
			throw new IllegalArgumentException("Property 'methodName' is required");
		}

		Class<?> beanClass = beanFactory.getType(this.targetBeanName);
		if (beanClass == null) {
			throw new IllegalArgumentException("Can't determine type of bean with name '" + this.targetBeanName + "'");
		}
		
		this.method = BeanUtils.resolveSignature(this.methodName, beanClass);

		if (this.method == null) {
			throw new IllegalArgumentException("Unable to locate method [" + this.methodName +
					"] on bean [" + this.targetBeanName + "]");
		}
	}

	public Method getObject() throws Exception {
		return this.method;
	}

	public Class<?> getObjectType() {
		return Method.class;
	}

}
