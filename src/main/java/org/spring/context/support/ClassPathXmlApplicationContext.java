package org.spring.context.support;

import org.spring.core.io.ClassPathResource;
import org.spring.core.io.Resource;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

	public ClassPathXmlApplicationContext(String configFile) {
		super(configFile);
		
	}

	@Override
	protected Resource getResourceByPath(String path) {
		
		return new ClassPathResource(path,this.getBeanClassLoader());
	}

}
