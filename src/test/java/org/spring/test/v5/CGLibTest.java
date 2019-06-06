package org.spring.test.v5;

import java.lang.reflect.Method;
import org.junit.Test;
import org.spring.service.v5.PetStoreService;
import org.spring.tx.TransactionManager;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;

public class CGLibTest {
	
	//该代理会对被代理类任何方法被调用时都执行拦截
	@Test
	public void testCallBack(){
		Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PetStoreService.class);
        //设置拦截器
        enhancer.setCallback( new TransactionInterceptor() );
        PetStoreService petStore = (PetStoreService)enhancer.create();
        petStore.placeOrder();
	}
	
	public static class TransactionInterceptor implements MethodInterceptor {
		TransactionManager txManager = new TransactionManager();
	    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
	        txManager.start();
	        //真正的业务方法
	        Object result = proxy.invokeSuper(obj, args);
	        txManager.commit();
	        return result;
	    }
	}
	
	//只对需要进行拦截的方法进行拦截
	@Test 
	public void  testFilter(){
		
		Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PetStoreService.class);
        
        enhancer.setInterceptDuringConstruction(false);
        
        Callback[] callbacks = new Callback[]{new TransactionInterceptor(),NoOp.INSTANCE};
        
        Class<?>[] types = new Class<?>[callbacks.length];
		for (int x = 0; x < types.length; x++) {
			types[x] = callbacks[x].getClass();
		}
		
        enhancer.setCallbackFilter(new ProxyCallbackFilter());
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackTypes(types);
        
        PetStoreService petStore = (PetStoreService)enhancer.create();
        petStore.placeOrder();
        System.out.println(petStore.toString());
	}
	
	/**
	 *当调用某个method时，指定某个拦截器进行拦截
	 */
	private static class ProxyCallbackFilter implements CallbackFilter {	
		public ProxyCallbackFilter() {			
			
		}
		public int accept(Method method) {
			if(method.getName().startsWith("place")){
				//为0则调用第0个拦截器TransactionInterceptor
				return 0;
			} else{
				//为1则调用第1个拦截器NoOp.INSTANCE
				return 1;
			}
		}
	}
}
