package org.spring.beans.factory.support;

import org.spring.beans.BeanDefinition;
import org.spring.beans.factory.BeanCreationException;
import org.spring.beans.factory.config.ConfigurableBeanFactory;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {

    protected abstract Object createBean(BeanDefinition bd) throws BeanCreationException;

}