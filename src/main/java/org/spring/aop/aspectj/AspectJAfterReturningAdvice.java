package org.spring.aop.aspectj;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.spring.aop.Advice;
import org.spring.aop.Pointcut;
import org.spring.aop.config.AspectInstanceFactory;

//后置通知
public class AspectJAfterReturningAdvice extends AbstractAspectJAdvice{
	
	public AspectJAfterReturningAdvice(Method adviceMethod,AspectJExpressionPointcut pointcut,AspectInstanceFactory adviceObjectFactory){
		super(adviceMethod,pointcut,adviceObjectFactory);
	}
	
	public Object invoke(MethodInvocation mi) throws Throwable {
		Object o = mi.proceed();
		//例如：调用TransactionManager的commit方法
		this.invokeAdviceMethod();
		return o;
	}
}