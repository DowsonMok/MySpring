package org.spring.test.v3;

import org.junit.Assert;
import org.junit.Test;
import org.spring.context.ApplicationContext;
import org.spring.context.support.ClassPathXmlApplicationContext;
import org.spring.service.v3.PetStoreService;

public class ApplicationContextTestV3 {

	@Test
	public void testGetBeanProperty() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("petstore-v3.xml");
		PetStoreService petStore = (PetStoreService)ctx.getBean("petStore");
		
		Assert.assertNotNull(petStore.getAccountDao());
		Assert.assertNotNull(petStore.getItemDao());		
		Assert.assertEquals(1, petStore.getVersion());
		
	}

}
