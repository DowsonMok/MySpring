package org.spring.test.v4;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.spring.beans.factory.config.DependencyDescriptor;
import org.spring.beans.factory.support.DefaultBeanFactory;
import org.spring.beans.factory.xml.XmlBeanDefinitionReader;
import org.spring.core.io.ClassPathResource;
import org.spring.core.io.Resource;
import org.spring.dao.v4.AccountDao;
import org.spring.service.v4.PetStoreService;

public class DependencyDescriptorTest {

	@Test
	public void testResolveDependency() throws Exception{
		
		DefaultBeanFactory factory = new DefaultBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		Resource resource = new ClassPathResource("petstore-v4.xml");
		reader.loadBeanDefinitions(resource);
		
		Field f = PetStoreService.class.getDeclaredField("accountDao");
		DependencyDescriptor  descriptor = new DependencyDescriptor(f,true);
		Object o = factory.resolveDependency(descriptor);
		Assert.assertTrue(o instanceof AccountDao);
	}

}
