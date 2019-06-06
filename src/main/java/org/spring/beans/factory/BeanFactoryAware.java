package org.spring.beans.factory;

import org.spring.beans.BeansException;

public interface BeanFactoryAware {
	void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
