package org.spring.test.v4;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.spring.core.annotation.AnnotationAttributes;
import org.spring.core.io.ClassPathResource;
import org.spring.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.spring.core.type.classreading.ClassMetadataReadingVisitor;
import org.springframework.asm.ClassReader;


/**
 * 2.使用ASM读取Resource中类的元信息和注解
 * @author Administrator
 *
 */
public class ClassReaderTest {
	/**
	 * ClassMetadataReadingVisitor：ASM的Reader调用ClassMetadataReadingVisitor的visit方法 
	 * 告知被Reader读取的类的元信息 比如是否是抽象类 是否是接口 是否为final
	 * @throws IOException
	 */
	@Test
	public void testGetClasMetaData() throws IOException {
		ClassPathResource resource = new ClassPathResource("org/spring/service/v4/PetStoreService.class");
		ClassReader reader = new ClassReader(resource.getInputStream());
		
		ClassMetadataReadingVisitor visitor = new ClassMetadataReadingVisitor();
		
		reader.accept(visitor, ClassReader.SKIP_DEBUG);
		
		Assert.assertFalse(visitor.isAbstract());
		Assert.assertFalse(visitor.isInterface());
		Assert.assertFalse(visitor.isFinal());		
		Assert.assertEquals("org.spring.service.v4.PetStoreService", visitor.getClassName());
		Assert.assertEquals("java.lang.Object", visitor.getSuperClassName());
		Assert.assertEquals(0, visitor.getInterfaceNames().length);
	}
	
	/**
	 * ASM的Reader调用AnnotationMetadataReadingVisitor的visitAnnotation方法 告知类中的注解和注解中的属性
	 * @throws Exception
	 */
	@Test
	public void testGetAnnonation() throws Exception{
		ClassPathResource resource = new ClassPathResource("org/spring/service/v4/PetStoreService.class");
		ClassReader reader = new ClassReader(resource.getInputStream());
		
		AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor();
		
		//1.读取ClassMetadata-->通过调用visit
		//2.读取AnnotationMetadata-->通过调用visitAnnotation
		//3.读取AnnotationMetadata中的属性-->通过调用visitAnnotation
		reader.accept(visitor, ClassReader.SKIP_DEBUG);
		
		String annotation = "org.spring.stereotype.Component";
		//判断是否有annotation这个注解
		Assert.assertTrue(visitor.hasAnnotation(annotation));
		
		//比如注解@Component(value="petStore")
		//根据key(annotation)的值"org.spring.stereotype.Component"
		//在visitor的Map<String, AnnotationAttributes>找对应的值AnnotationAttributes(key为注解的属性，value为属性的值)
		AnnotationAttributes attributes = visitor.getAnnotationAttributes(annotation);
		//从AnnotationAttributes()中取key为"value"属性的值，值为"petStore"
		Assert.assertEquals("petStore", attributes.get("value"));		
		
	}
}
