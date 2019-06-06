package org.spring.test.v2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.spring.context.ApplicationContext;
import org.spring.context.support.ClassPathXmlApplicationContext;
import org.spring.dao.v2.AccountDao;
import org.spring.dao.v2.ItemDao;
import org.spring.service.v2.PetStoreService;

public class ApplicationContextTestV2 {

	@Test
	public void testGetBeanProperty() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("petstore-v2.xml");
		PetStoreService petStore = (PetStoreService)ctx.getBean("petStore");
		
		assertNotNull(petStore.getAccountDao());
		assertNotNull(petStore.getItemDao());
		
		assertTrue(petStore.getAccountDao() instanceof AccountDao);
		assertTrue(petStore.getItemDao() instanceof ItemDao);
		
		assertEquals("liuxin",petStore.getOwner());
		assertEquals(2, petStore.getVersion()); 
		
	}

}
