package org.spring.beans.factory.config;

import org.spring.beans.factory.BeanFactory;

public interface AutowireCapableBeanFactory extends BeanFactory {
	public Object resolveDependency(DependencyDescriptor descriptor);
}
