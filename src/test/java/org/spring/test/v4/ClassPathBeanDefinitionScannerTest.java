package org.spring.test.v4;

import org.junit.Assert;
import org.junit.Test;
import org.spring.beans.BeanDefinition;
import org.spring.beans.factory.support.DefaultBeanFactory;
import org.spring.context.annotation.ClassPathBeanDefinitionScanner;
import org.spring.context.annotation.ScannedGenericBeanDefinition;
import org.spring.core.annotation.AnnotationAttributes;
import org.spring.core.type.AnnotationMetadata;
import org.spring.stereotype.Component;

public class ClassPathBeanDefinitionScannerTest {
	
	@Test
	public void testParseScanedBean(){
		
		DefaultBeanFactory factory = new DefaultBeanFactory();
		
		String basePackages = "org.spring.service.v4,org.spring.dao.v4";
		
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(factory);
		scanner.doScan(basePackages);
		
		
		String annotation = Component.class.getName();
		
		{
			BeanDefinition bd = factory.getBeanDefinition("petStore");
			Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
			ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition)bd;
			AnnotationMetadata amd = sbd.getMetadata();
			
			
			Assert.assertTrue(amd.hasAnnotation(annotation));		
			AnnotationAttributes attributes = amd.getAnnotationAttributes(annotation);		
			Assert.assertEquals("petStore", attributes.get("value"));
		}
		{
			BeanDefinition bd = factory.getBeanDefinition("accountDao");
			Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
			ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition)bd;			
			AnnotationMetadata amd = sbd.getMetadata();
			Assert.assertTrue(amd.hasAnnotation(annotation));
		}
		{
			BeanDefinition bd = factory.getBeanDefinition("itemDao");
			Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
			ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition)bd;			
			AnnotationMetadata amd = sbd.getMetadata();
			Assert.assertTrue(amd.hasAnnotation(annotation));
		}
	}
}
