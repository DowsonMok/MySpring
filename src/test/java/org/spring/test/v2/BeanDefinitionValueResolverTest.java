package org.spring.test.v2;

import org.junit.Assert;
import org.junit.Test;
import org.spring.beans.factory.config.RuntimeBeanReference;
import org.spring.beans.factory.config.TypedStringValue;
import org.spring.beans.factory.support.BeanDefinitionValueResolver;
import org.spring.beans.factory.support.DefaultBeanFactory;
import org.spring.beans.factory.xml.XmlBeanDefinitionReader;
import org.spring.core.io.ClassPathResource;
import org.spring.dao.v2.AccountDao;

public class BeanDefinitionValueResolverTest {

	@Test
	public void testResolveRuntimeBeanReference() {
		DefaultBeanFactory factory = new DefaultBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);		
		reader.loadBeanDefinitions(new ClassPathResource("petstore-v2.xml"));
		
		BeanDefinitionValueResolver resolver = new BeanDefinitionValueResolver(factory);
		
		RuntimeBeanReference reference = new RuntimeBeanReference("accountDao");
		Object value = resolver.resolveValueIfNecessary(reference);
		
		Assert.assertNotNull(value);		
		Assert.assertTrue(value instanceof AccountDao);				
	}
	@Test
	public void testResolveTypedStringValue() {
		DefaultBeanFactory factory = new DefaultBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);		
		reader.loadBeanDefinitions(new ClassPathResource("petstore-v2.xml"));
		
		BeanDefinitionValueResolver resolver = new BeanDefinitionValueResolver(factory);
		
		TypedStringValue stringValue = new TypedStringValue("test");
		Object value = resolver.resolveValueIfNecessary(stringValue);		
		Assert.assertNotNull(value);		
		Assert.assertEquals("test", value);
		
	}
}
