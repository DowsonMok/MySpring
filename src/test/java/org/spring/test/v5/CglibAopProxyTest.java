package org.spring.test.v5;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spring.aop.aspectj.AspectJAfterReturningAdvice;
import org.spring.aop.aspectj.AspectJAfterThrowingAdvice;
import org.spring.aop.aspectj.AspectJBeforeAdvice;
import org.spring.aop.aspectj.AspectJExpressionPointcut;
import org.spring.aop.config.AspectInstanceFactory;
import org.spring.aop.framework.AopConfig;
import org.spring.aop.framework.AopConfigSupport;
import org.spring.aop.framework.CglibProxyFactory;
import org.spring.beans.factory.BeanFactory;
import org.spring.service.v5.PetStoreService;
import org.spring.tx.TransactionManager;
import org.spring.util.MessageTracker;

public class CglibAopProxyTest extends AbstractV5Test{
		
	private AspectJBeforeAdvice beforeAdvice = null;
	private AspectJAfterReturningAdvice afterAdvice = null;
	private static AspectJExpressionPointcut pc = null;
	private BeanFactory beanFactory = null;
	private AspectInstanceFactory aspectInstanceFactory = null;
	
	@Before
	public  void setUp() throws Exception{	
		
		MessageTracker.clearMsgs();
		
		String expression = "execution(* org.spring.service.v5.*.placeOrder(..))";
		pc = new AspectJExpressionPointcut();
		pc.setExpression(expression);
		
		beanFactory = this.getBeanFactory("petstore-v5.xml");
		aspectInstanceFactory = this.getAspectInstanceFactory("tx");
		aspectInstanceFactory.setBeanFactory(beanFactory);
		
		beforeAdvice = new AspectJBeforeAdvice(	getAdviceMethod("start"),pc,aspectInstanceFactory);
		afterAdvice = new AspectJAfterReturningAdvice( getAdviceMethod("commit"),pc,aspectInstanceFactory);	
	}
	
	@Test
	public void testGetProxy(){
		AopConfig config = new AopConfigSupport();
		
		config.addAdvice(beforeAdvice);
		config.addAdvice(afterAdvice);
		config.setTargetObject(new PetStoreService());
		
		CglibProxyFactory proxyFactory = new CglibProxyFactory(config);
		
		PetStoreService proxy = (PetStoreService)proxyFactory.getProxy();
		
		proxy.placeOrder();				
		
		List<String> msgs = MessageTracker.getMsgs();
		Assert.assertEquals(3, msgs.size());
		Assert.assertEquals("start tx", msgs.get(0));	
		Assert.assertEquals("place order", msgs.get(1));	
		Assert.assertEquals("commit tx", msgs.get(2));	
		
		proxy.toString();
	}
}
