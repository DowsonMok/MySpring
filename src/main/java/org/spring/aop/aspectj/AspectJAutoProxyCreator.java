package org.spring.aop.aspectj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.spring.aop.Advice;
import org.spring.aop.MethodMatcher;
import org.spring.aop.Pointcut;
import org.spring.aop.framework.AopConfigSupport;
import org.spring.aop.framework.AopProxyFactory;
import org.spring.aop.framework.CglibProxyFactory;
import org.spring.aop.framework.JdkAopProxyFactory;
import org.spring.beans.BeansException;
import org.spring.beans.factory.config.BeanPostProcessor;
import org.spring.beans.factory.config.ConfigurableBeanFactory;
import org.spring.util.ClassUtils;

public class AspectJAutoProxyCreator implements BeanPostProcessor {
    ConfigurableBeanFactory beanFactory;

    public Object beforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object afterInitialization(Object bean, String beanName) throws BeansException {
    	//如果这个Bean本身就是Advice及其子类，那就不要再生成动态代理了。
        if (isInfrastructureClass(bean.getClass())) {
            return bean;
        }
        //每个advice都有pointcut 根据pointcut规则找到能够对该bean进行增强的advice 
        List<Advice> advices = getCandidateAdvices(bean);
        //如果没找到有能对该bean进行增强的advice，则直接返回
        if (advices.isEmpty()) {
            return bean;
        }
        //如果有，则进行创建代理
        return createProxy(advices, bean);
    }

    private List<Advice> getCandidateAdvices(Object bean) {
        List<Object> advices = this.beanFactory.getBeansByType(Advice.class);

        List<Advice> result = new ArrayList<Advice>();
        for (Object o : advices) {
            Pointcut pc = ((Advice) o).getPointcut();
            //该pointcut是否能对该bean进行处理
            if (canApply(pc, bean.getClass())) {
                result.add((Advice) o);
            }
        }
        return result;
    }

    protected Object createProxy(List<Advice> advices, Object bean) {
        AopConfigSupport config = new AopConfigSupport();
        for (Advice advice : advices) {
            config.addAdvice(advice);
        }
        Set<Class> targetInterfaces = ClassUtils.getAllInterfacesForClassAsSet(bean.getClass());
        for (Class<?> targetInterface : targetInterfaces) {
            config.addInterface(targetInterface);
        }
        config.setTargetObject(bean);

        AopProxyFactory proxyFactory = null;
        if (config.getProxiedInterfaces().length == 0) {
            proxyFactory = new CglibProxyFactory(config);
        } else {
            //需要实现JDK代理
            proxyFactory=new JdkAopProxyFactory(config);
        }
        return proxyFactory.getProxy();
    }

    protected boolean isInfrastructureClass(Class<?> beanClass) {
        boolean retVal = Advice.class.isAssignableFrom(beanClass);

        return retVal;
    }

    public void setBeanFactory(ConfigurableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public static boolean canApply(Pointcut pc, Class<?> targetClass) {
        MethodMatcher methodMatcher = pc.getMethodMatcher();

        LinkedHashSet<Class> classes = new LinkedHashSet<Class>(ClassUtils.getAllInterfacesAsSet(targetClass));
        classes.add(targetClass);
        for (Class<?> clazz : classes) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (methodMatcher.matches(method)) {
                    return true;
                }
            }
        }
        return false;
    }
}