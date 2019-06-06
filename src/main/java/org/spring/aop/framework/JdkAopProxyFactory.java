package org.spring.aop.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.spring.aop.Advice;
import org.spring.util.Assert;
import org.spring.util.ClassUtils;


public class JdkAopProxyFactory implements AopProxyFactory,InvocationHandler {
    private static final Log logger = LogFactory.getLog(JdkAopProxyFactory.class);

    private final AopConfig aopConfig;

    public JdkAopProxyFactory(AopConfig config) {
        Assert.notNull(config, "AdvisedSupport must not be null");
        if (config.getAdvices().size() == 0) {
            throw new AopConfigException("No advice specified");
        }
        this.aopConfig = config;
    }

    /**
     * 获取代理
     * @return
     */
    public Object getProxy() {
        return getProxy(ClassUtils.getDefaultClassLoader());
    }

    public Object getProxy(ClassLoader classLoader) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating JDK dynamic proxy: target source is " + this.aopConfig.getTargetObject());
        }
        Class<?>[] proxiedInterfaces = aopConfig.getProxiedInterfaces();
        return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
    }

    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    	//获取目标对象
        Object target = this.aopConfig.getTargetObject();
        Object retVal;

        //获取通知点
        List<Advice> chain = this.aopConfig.getAdvices(method);
        if (chain.isEmpty()) {
            retVal = method.invoke(target, args);
        } else {
            List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
            interceptors.addAll(chain);

            retVal = new ReflectiveMethodInvocation(target, method, args, interceptors).proceed();
        }
        return retVal;
    }
}