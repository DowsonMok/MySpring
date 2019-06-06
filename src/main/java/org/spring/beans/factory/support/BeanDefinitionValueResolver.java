package org.spring.beans.factory.support;

import org.spring.beans.BeanDefinition;
import org.spring.beans.factory.BeanCreationException;
import org.spring.beans.factory.FactoryBean;
import org.spring.beans.factory.config.RuntimeBeanReference;
import org.spring.beans.factory.config.TypedStringValue;

public class BeanDefinitionValueResolver {
    private final AbstractBeanFactory beanFactory;

    public BeanDefinitionValueResolver(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Object resolveValueIfNecessary(Object value) {
        if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference ref = (RuntimeBeanReference) value;
            String refName = ref.getBeanName();
            Object bean = this.beanFactory.getBean(refName);
            return bean;
        } else if (value instanceof TypedStringValue) {
            return ((TypedStringValue) value).getValue();
        } else if (value instanceof BeanDefinition) {
            BeanDefinition bd = (BeanDefinition) value;
            String innnerBeanName = "(inner bean)" + bd.getBeanClassName() + "#" +
                    Integer.toHexString(System.identityHashCode(bd));
            return resolveInnerBean(innnerBeanName, bd);
        } else {
            return value;
        }

    }
    
    /**
     * 1.创建内部BeanDefinition实例，判断当前实例是不是FactoryBean类型
     * 2.如果是则调用其getObject方法然后返回
     * 3.如果不是直接返回
     * 
     * 这里调用getObject方法是因为MethodLocatingFactory类型跟AspectJBeforeAdvice的第一个参数Method类型并不匹配
     * 所以需要调用getObject方法
     */
    private Object resolveInnerBean(String innerBeanName,BeanDefinition innerBd) {
        try {
            Object innerBean = this.beanFactory.createBean(innerBd);
            if (innerBean instanceof FactoryBean) {
                try {
                    return ((FactoryBean<?>) innerBean).getObject();
                } catch (Exception e) {
                    throw new BeanCreationException(innerBeanName, "FactoryBean threw exception on object creation", e);
                }
            }
            else{
                return innerBean;
            }
        } catch (BeanCreationException ex) {
            throw new BeanCreationException(
                    innerBeanName,
                    "Cannot create inner bean '" + innerBeanName + "' " +
                            (innerBd != null && innerBd.getBeanClassName() != null ? "of type [" + innerBd.getBeanClassName() + "] " : "")
                    , ex);
        }
    }
}