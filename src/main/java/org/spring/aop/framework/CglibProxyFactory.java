package org.spring.aop.framework;

/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.spring.aop.Advice;
import org.spring.aop.AopInvocationException;
import org.spring.util.Assert;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;


@SuppressWarnings("serial")
public class CglibProxyFactory implements AopProxyFactory {

	//拦截器索引 我们只使用AOP_PROXY
	private static final int AOP_PROXY = 0;
	private static final int INVOKE_TARGET = 1;
	private static final int NO_OVERRIDE = 2;
	private static final int DISPATCH_TARGET = 3;
	private static final int DISPATCH_ADVISED = 4;
	private static final int INVOKE_EQUALS = 5;
	private static final int INVOKE_HASHCODE = 6;

	/** Logger available to subclasses; static to optimize serialization */
	protected static final Log logger = LogFactory.getLog(CglibProxyFactory.class);

	protected final AopConfig config;

	private Object[] constructorArgs;

	private Class<?>[] constructorArgTypes;
	
	public CglibProxyFactory(AopConfig config) throws AopConfigException {
		Assert.notNull(config, "AdvisedSupport must not be null");
		if (config.getAdvices().size() == 0 /*&& config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE*/) {
			throw new AopConfigException("No advisors and no TargetSource specified");
		}
		this.config = config;
		
	}

	public Object getProxy() {
		return getProxy(null);
	}

	public Object getProxy(ClassLoader classLoader) {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating CGLIB proxy: target source is " + this.config.getTargetClass());
		}

		try {
			Class<?> rootClass = this.config.getTargetClass();
				
			// Configure CGLIB Enhancer...
			Enhancer enhancer = new Enhancer();
			if (classLoader != null) {
				enhancer.setClassLoader(classLoader);				
			}
			enhancer.setSuperclass(rootClass);
			
			//创建了新的类，要以什么样的命名规则默认是ByCGLIB 我们改为BySpringCGLIB
			enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE); //"BySpringCGLIB"
			enhancer.setInterceptDuringConstruction(false);

			Callback[] callbacks = getCallbacks(rootClass);
			Class<?>[] types = new Class<?>[callbacks.length];
			for (int x = 0; x < types.length; x++) {
				types[x] = callbacks[x].getClass();
			}
			
			enhancer.setCallbackFilter(new ProxyCallbackFilter(this.config));
			enhancer.setCallbackTypes(types);
			enhancer.setCallbacks(callbacks);

			Object proxy = enhancer.create();
			return proxy;
		}
		catch (CodeGenerationException ex) {
			throw new AopConfigException("Could not generate CGLIB subclass of class [" +
					this.config.getTargetClass() + "]: " +
					"Common causes of this problem include using a final class or a non-visible class",
					ex);
		}
		catch (IllegalArgumentException ex) {
			throw new AopConfigException("Could not generate CGLIB subclass of class [" +
					this.config.getTargetClass() + "]: " +
					"Common causes of this problem include using a final class or a non-visible class",
					ex);
		}
		catch (Exception ex) {
			// TargetSource.getTarget() failed
			throw new AopConfigException("Unexpected AOP exception", ex);
		}
	}


	private Callback[] getCallbacks(Class<?> rootClass) throws Exception {
		
		Callback aopInterceptor = new DynamicAdvisedInterceptor(this.config);

		Callback[] callbacks = new Callback[] {
				aopInterceptor,  // AOP_PROXY for normal advice  
				/*targetInterceptor,  // INVOKE_TARGET invoke target without considering advice, if optimized
				new SerializableNoOp(),  // NO_OVERRIDE  no override for methods mapped to this
				targetDispatcher,        //DISPATCH_TARGET
				this.advisedDispatcher,  //DISPATCH_ADVISED
				new EqualsInterceptor(this.advised),
				new HashCodeInterceptor(this.advised)*/
		};		

		return callbacks;
	}
	
	private static class DynamicAdvisedInterceptor implements MethodInterceptor, Serializable {

		private final AopConfig config;

		public DynamicAdvisedInterceptor(AopConfig advised) {
			this.config = advised;
		}

		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			
			Object target = this.config.getTargetObject();
			
			//拿到匹配这个方法的拦截器
			List<Advice> chain = this.config.getAdvices(method/*, targetClass*/);
			Object retVal;
			//没有匹配到拦截器，也就是没有拦截器对该方法进行拦截，则啥也不干，直接调用
			if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
				retVal = methodProxy.invoke(target, args);
			}
			else {
				//生成一个拦截器列表
				List<org.aopalliance.intercept.MethodInterceptor> interceptors = 
						new ArrayList<org.aopalliance.intercept.MethodInterceptor>();
				
				interceptors.addAll(chain);
				
				//按照指定次序的链式调用
				retVal = new ReflectiveMethodInvocation(target, method, args, interceptors).proceed();
			}
			//retVal = processReturnType(proxy, target, method, retVal);
			return retVal;
		}
	}

	/**
	 *
	 * 当调用某个method时，指定某个拦截器进行拦截，这里我们做了简化
	 */
	private static class ProxyCallbackFilter implements CallbackFilter {

		private final AopConfig config;

		public ProxyCallbackFilter(AopConfig advised) {
			this.config = advised;
		}

		public int accept(Method method) {
			// 注意，这里做了简化
			return AOP_PROXY;
		}
	}
}
