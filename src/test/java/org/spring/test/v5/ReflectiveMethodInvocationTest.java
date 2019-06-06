package org.spring.test.v5;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spring.aop.aspectj.AspectJAfterReturningAdvice;
import org.spring.aop.aspectj.AspectJAfterThrowingAdvice;
import org.spring.aop.aspectj.AspectJBeforeAdvice;
import org.spring.aop.aspectj.AspectJExpressionPointcut;
import org.spring.aop.config.AspectInstanceFactory;
import org.spring.aop.framework.ReflectiveMethodInvocation;
import org.spring.beans.factory.BeanFactory;
import org.spring.service.v5.PetStoreService;
import org.spring.tx.TransactionManager;
import org.spring.util.MessageTracker;

//测试指定次序的链式调用
//测试ReflectiveMethodInvocation和拦截器AspectJBeforeAdvice、AspectJAfterReturningAdvice、AspectJAfterThrowingAdvice
public class ReflectiveMethodInvocationTest extends AbstractV5Test{
	
	private AspectJBeforeAdvice beforeAdvice = null;
	private AspectJAfterReturningAdvice afterAdvice = null;
	private static AspectJExpressionPointcut pc = null;
	private BeanFactory beanFactory = null;
	private AspectInstanceFactory aspectInstanceFactory = null;
	
	private AspectJAfterThrowingAdvice  afterThrowingAdvice = null;
	private PetStoreService petStoreService = null;
	private TransactionManager tx;

	@Before
	public  void setUp() throws Exception{	
		petStoreService = new PetStoreService();
		tx = new TransactionManager();
		
		MessageTracker.clearMsgs();
		
		beanFactory = this.getBeanFactory("petstore-v5.xml");
		aspectInstanceFactory = this.getAspectInstanceFactory("tx");
		aspectInstanceFactory.setBeanFactory(beanFactory);
		
		beforeAdvice = new AspectJBeforeAdvice(	getAdviceMethod("start"),pc,aspectInstanceFactory);
		afterAdvice = new AspectJAfterReturningAdvice( getAdviceMethod("commit"),pc,aspectInstanceFactory);	
		afterThrowingAdvice = new AspectJAfterThrowingAdvice(getAdviceMethod("rollback"),pc,aspectInstanceFactory);
	}

	//测试AspectJBeforeAdvice、AspectJAfterReturningAdvice拦截器的执行顺序是否正确
	@Test
	public void testMethodInvocation() throws Throwable{
		Method targetMethod = PetStoreService.class.getMethod("placeOrder");
		
		List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
		interceptors.add(beforeAdvice);
		interceptors.add(afterAdvice);	
		
		ReflectiveMethodInvocation mi = new ReflectiveMethodInvocation(petStoreService,targetMethod,new Object[0],interceptors);
		
		mi.proceed();
		
		List<String> msgs = MessageTracker.getMsgs();
		Assert.assertEquals(3, msgs.size());
		Assert.assertEquals("start tx", msgs.get(0));	
		Assert.assertEquals("place order", msgs.get(1));	
		Assert.assertEquals("commit tx", msgs.get(2));	
	}
	
	//测试AspectJBeforeAdvice、AspectJAfterReturningAdvice拦截器的执行顺序是否正确
	@Test
	public void testMethodInvocation2() throws Throwable{
		Method targetMethod = PetStoreService.class.getMethod("placeOrder");
		
		List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
		interceptors.add(afterAdvice);	
		interceptors.add(beforeAdvice);
		
		ReflectiveMethodInvocation mi = new ReflectiveMethodInvocation(petStoreService,targetMethod,new Object[0],interceptors);
		
		mi.proceed();
		
		List<String> msgs = MessageTracker.getMsgs();
		Assert.assertEquals(3, msgs.size());
		Assert.assertEquals("start tx", msgs.get(0));	
		Assert.assertEquals("place order", msgs.get(1));	
		Assert.assertEquals("commit tx", msgs.get(2));	
	}
	
	@Test
	public void testAfterThrowing() throws Throwable{
		Method targetMethod = PetStoreService.class.getMethod("placeOrderWithException");
		
		List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
		interceptors.add(afterThrowingAdvice);	
		interceptors.add(beforeAdvice);
		
		ReflectiveMethodInvocation mi = new ReflectiveMethodInvocation(petStoreService,targetMethod,new Object[0],interceptors);
		try{
			mi.proceed();	
		}catch(Throwable t){
			List<String> msgs = MessageTracker.getMsgs();
			Assert.assertEquals(2, msgs.size());
			Assert.assertEquals("start tx", msgs.get(0));			
			Assert.assertEquals("rollback tx", msgs.get(1));	
			return;
		}
		//如果走到这里，说明测试失败
		Assert.fail("No Exception thrown");	
	}
}
