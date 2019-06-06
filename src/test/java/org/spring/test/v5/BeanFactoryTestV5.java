package org.spring.test.v5;

import org.spring.aop.Advice;
import org.spring.aop.aspectj.AspectJAfterReturningAdvice;
import org.spring.aop.aspectj.AspectJAfterThrowingAdvice;
import org.spring.aop.aspectj.AspectJBeforeAdvice;
import org.spring.beans.factory.BeanFactory;
import org.spring.tx.TransactionManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by xiang.wei on 2018/8/16
 *
 * @author xiang.wei
 */
public class BeanFactoryTestV5 extends AbstractV5Test {

    static String expectedExpression = "execution(* org.spring.service.v5.*.placeOrder(..))";
    
    @Test
    public void  testGetBeanByType() throws Exception {
        BeanFactory factory = super.getBeanFactory("petstore-v5.xml");
        //根据类型从工厂中获取实例
        List<Object> advices = factory.getBeansByType(Advice.class);

        Assert.assertEquals(3, advices.size());
        //测试是否创建AspectJBeforeAdvice实例
        {
            AspectJBeforeAdvice advice = (AspectJBeforeAdvice) this.getAdvice(AspectJBeforeAdvice.class, advices);
            
            Assert.assertEquals(TransactionManager.class.getMethod("start"), advice.getAdviceMethod());
            
            Assert.assertEquals(expectedExpression, advice.getPointcut().getExpression());
            
            Assert.assertEquals(TransactionManager.class, advice.getAdviceInstance().getClass());
        }
        
        //测试是否创建AspectJAfterReturningAdvice实例
        {
            AspectJAfterReturningAdvice advice = (AspectJAfterReturningAdvice)this.getAdvice(AspectJAfterReturningAdvice.class, advices);

            Assert.assertEquals(TransactionManager.class.getMethod("commit"), advice.getAdviceMethod());

            Assert.assertEquals(expectedExpression,advice.getPointcut().getExpression());

            Assert.assertEquals(TransactionManager.class,advice.getAdviceInstance().getClass());

        }
        
        //测试是否创建AspectJAfterThrowingAdvice实例
        {
            AspectJAfterThrowingAdvice advice = (AspectJAfterThrowingAdvice)this.getAdvice(AspectJAfterThrowingAdvice.class, advices);

            Assert.assertEquals(TransactionManager.class.getMethod("rollback"), advice.getAdviceMethod());

            Assert.assertEquals(expectedExpression,advice.getPointcut().getExpression());

            Assert.assertEquals(TransactionManager.class,advice.getAdviceInstance().getClass());
        }
    }

    public Object getAdvice(Class<?> type, List<Object> advices) {
        for (Object advice : advices) {
            if (advice.getClass().equals(type)) {
                return advice;
            }
        }
        return null;
    }
}