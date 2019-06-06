package org.spring.aop.framework;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

//关键类：能够实现拦截器以正确的次序执行
public class ReflectiveMethodInvocation implements MethodInvocation {
	//目标对象	如petStoreService
	protected final Object targetObject;
	//目标对象的目标方法	如placeOrder方法
	protected final Method targetMethod; 
	//方法参数
	protected Object[] arguments;
	//存放拦截器的集合
	protected final List<MethodInterceptor> interceptors;
	//当前所执行拦截器在集合中的索引
	private int currentInterceptorIndex = -1;

	public ReflectiveMethodInvocation(
			Object target, Method method, Object[] arguments,
			List<MethodInterceptor> interceptors) {
		this.targetObject = target;		
		this.targetMethod = method;
		this.arguments = arguments;
		this.interceptors = interceptors;
	}

	public Object proceed() throws Throwable {
		//如果所有的拦截器已经调用完成，调用目标对象的目标方法
		if (this.currentInterceptorIndex == this.interceptors.size() - 1) {
			return invokeJoinpoint();
		}
		//从interceptors集合中取出并调用拦截器
		this.currentInterceptorIndex ++;
		MethodInterceptor interceptor =	this.interceptors.get(this.currentInterceptorIndex);
		return interceptor.invoke(this);
	}
	//调用目标对象的目标方法
	protected Object invokeJoinpoint() throws Throwable {		
		return this.targetMethod.invoke(this.targetObject, this.arguments);
	}

	public AccessibleObject getStaticPart() {		
		return this.targetMethod;
	}
	
	public final Object getThis() {
		return this.targetObject;
	}

	public final Method getMethod() {
		return this.targetMethod;
	}

	public final Object[] getArguments() {
		return (this.arguments != null ? this.arguments : new Object[0]);
	}
}


