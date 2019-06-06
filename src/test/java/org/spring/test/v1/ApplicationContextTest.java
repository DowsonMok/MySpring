package org.spring.test.v1;

import org.junit.Assert;
import org.junit.Test;
import org.spring.context.ApplicationContext;
import org.spring.context.support.ClassPathXmlApplicationContext;
import org.spring.context.support.FileSystemXmlApplicationContext;
import org.spring.service.v1.PetStoreService;

public class ApplicationContextTest {

	@Test
	public void testGetBean() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("petstore-v1.xml");
		PetStoreService petStore = (PetStoreService)ctx.getBean("petStore");
		Assert.assertNotNull(petStore);
	}
    @Test 
	public void testGetBeanFromFileSystemContext(){
	    //这里仍然是hardcode了一个本地路径，这是不好的实践!! 如何处理，留作作业
		/*ApplicationContext ctx = new FileSystemXmlApplicationContext("C:\\Users\\MySpring\\src\\test\\resources\\petstore-v1.xml");
		PetStoreService petStore = (PetStoreService)ctx.getBean("petStore");
		Assert.assertNotNull(petStore);*/
		
	}

}
