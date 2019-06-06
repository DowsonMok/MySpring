package org.spring.test.v4;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.spring.core.annotation.AnnotationAttributes;
import org.spring.core.io.ClassPathResource;
import org.spring.core.type.AnnotationMetadata;
import org.spring.core.type.classreading.MetadataReader;
import org.spring.core.type.classreading.SimpleMetadataReader;
import org.spring.stereotype.Component;

/*
 *  
	如果让我们直接使用Visitor是一件很麻烦 挺难理解的一件事，
	所以在做BeanDefinition之前，我们要做一个封装，把这些功能封装起来
	提供一个和ASM完全无关的接口出来，换句话说，我们要再做一个抽象，
	我们希望提供一个比较容易使用的接口，和ASM隔离

 */
public class MetadataReaderTest {
	@Test
	public void testGetMetadata() throws IOException{
		ClassPathResource resource = new ClassPathResource("org/spring/service/v4/PetStoreService.class");
		
		MetadataReader reader = new SimpleMetadataReader(resource);
		//注意：不需要单独使用ClassMetadata
		//ClassMetadata clzMetadata = reader.getClassMetadata();
		AnnotationMetadata amd = reader.getAnnotationMetadata();
		
		String annotation = Component.class.getName();
		
		Assert.assertTrue(amd.hasAnnotation(annotation));		
		AnnotationAttributes attributes = amd.getAnnotationAttributes(annotation);		
		Assert.assertEquals("petStore", attributes.get("value"));
		
		//注：下面对class metadata的测试并不充分
		Assert.assertFalse(amd.isAbstract());		
		Assert.assertFalse(amd.isFinal());
		Assert.assertEquals("org.spring.service.v4.PetStoreService", amd.getClassName());
		
	}
}
