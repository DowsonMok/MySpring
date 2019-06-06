package org.spring.aop.aspectj;

import java.lang.reflect.Method;

import org.spring.aop.Advice;
import org.spring.aop.Pointcut;
import org.spring.aop.config.AspectInstanceFactory;

public abstract class AbstractAspectJAdvice implements Advice{
	//所通知/增强的方法
	protected Method adviceMethod;	
	protected AspectJExpressionPointcut pointcut;

	protected AspectInstanceFactory adviceObjectFactory;
	
	public AbstractAspectJAdvice(Method adviceMethod,
			AspectJExpressionPointcut pointcut,
			AspectInstanceFactory adviceObjectFactory){
		
		this.adviceMethod = adviceMethod;
		this.pointcut = pointcut;
		this.adviceObjectFactory = adviceObjectFactory;
	}
	//调用通知/增强代码
	public void invokeAdviceMethod() throws  Throwable{
		adviceMethod.invoke(adviceObjectFactory.getAspectInstance());
	}
	public Pointcut getPointcut(){
		return this.pointcut;
	}
	public Method getAdviceMethod() {
		return adviceMethod;
	}
    public Object getAdviceInstance() throws Exception {
        return adviceObjectFactory.getAspectInstance();
    }
}