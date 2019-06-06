package org.spring.test.v1;

import static org.junit.Assert.*;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spring.beans.BeanDefinition;
import org.spring.beans.factory.BeanCreationException;
import org.spring.beans.factory.BeanDefinitionStoreException;
import org.spring.beans.factory.support.DefaultBeanFactory;
import org.spring.beans.factory.xml.XmlBeanDefinitionReader;
import org.spring.core.io.ClassPathResource;
import org.spring.service.v1.PetStoreService;


//通过xml配置文件创建BeanDefinition，实例化对应的Bean
public class BeanFactoryTest {
	DefaultBeanFactory factory = null;
	XmlBeanDefinitionReader reader = null;
	
	@Before
	public void setUp(){
		factory = new DefaultBeanFactory();
		reader = new XmlBeanDefinitionReader(factory);
		
	}
	@Test
	public void testGetBean() {
		
		reader.loadBeanDefinitions(new ClassPathResource("petstore-v1.xml"));
		
		BeanDefinition bd = factory.getBeanDefinition("petStore");
		
		assertTrue(bd.isSingleton());
		
		assertFalse(bd.isPrototype());
		
		assertEquals(BeanDefinition.SCOPE_DEFAULT,bd.getScope());
		
		assertEquals("org.spring.service.v1.PetStoreService",bd.getBeanClassName());
		
		PetStoreService petStore = (PetStoreService)factory.getBean("petStore");
		
		assertNotNull(petStore);
		
		PetStoreService petStore1 = (PetStoreService)factory.getBean("petStore");
		
		assertTrue(petStore.equals(petStore1));
	}
	
//	@Test
//	public void testInvalidBean(){
//
//		reader.loadBeanDefinitions(new ClassPathResource("petstore-v1.xml"));
//		try{
//			factory.getBean("invalidBean");
//		}catch(BeanCreationException e){
//			return;
//		}
//		Assert.fail("expect BeanCreationException ");
//	}
//	
//	@Test
//	public void testInvalidXML(){
//		
//		try{
//			reader.loadBeanDefinitions(new ClassPathResource("xxxx.xml"));
//		}catch(BeanDefinitionStoreException e){
//			return;
//		}
//		Assert.fail("expect BeanDefinitionStoreException ");
//	}
}
