package org.spring.beans.factory.annotation;

import org.spring.beans.BeanDefinition;
import org.spring.core.type.AnnotationMetadata;

public interface AnnotatedBeanDefinition extends BeanDefinition {
	AnnotationMetadata getMetadata();
}
