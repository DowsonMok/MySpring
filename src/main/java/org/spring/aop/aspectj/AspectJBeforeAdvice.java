package org.spring.aop.aspectj;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.spring.aop.config.AspectInstanceFactory;

//前置通知
public class AspectJBeforeAdvice extends AbstractAspectJAdvice {
	
	public AspectJBeforeAdvice(Method adviceMethod,AspectJExpressionPointcut pointcut,AspectInstanceFactory adviceObjectFactory){
		super(adviceMethod,pointcut,adviceObjectFactory);
	}
	
	public Object invoke(MethodInvocation mi) throws Throwable {
		//例如： 调用TransactionManager的start方法
		this.invokeAdviceMethod();
		Object o = mi.proceed();
		return o;
	}
}