package org.spring.test.v5;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.spring.aop.MethodMatcher;
import org.spring.aop.aspectj.AspectJExpressionPointcut;
import org.spring.dao.v5.AccountDao;
import org.spring.service.v5.PetStoreService;


/**
 * 测试方法是否匹配
 * @author Administrator
 *
 */
public class PointcutTest {
	@Test
	public void testPointcut() throws Exception{
		
		String expression = "execution(* org.spring.service.v5.*.placeOrder(..))";
		
		AspectJExpressionPointcut pc = new AspectJExpressionPointcut();
		pc.setExpression(expression);
		
		MethodMatcher mm = pc.getMethodMatcher();
		
		{
			Class<?> targetClass = PetStoreService.class;
			
			Method method1 = targetClass.getMethod("placeOrder");		
			Assert.assertTrue(mm.matches(method1));
			
			Method method2 = targetClass.getMethod("getAccountDao");		
			Assert.assertFalse(mm.matches(method2));
		}
		
		{
			Class<?> targetClass = org.spring.service.v4.PetStoreService.class;			
		
			Method method = targetClass.getMethod("getAccountDao");		
			Assert.assertFalse(mm.matches(method));
		}
	}
}
