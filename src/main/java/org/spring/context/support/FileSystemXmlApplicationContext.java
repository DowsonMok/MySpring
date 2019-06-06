package org.spring.context.support;

import org.spring.core.io.FileSystemResource;
import org.spring.core.io.Resource;

public class FileSystemXmlApplicationContext extends AbstractApplicationContext {

	public FileSystemXmlApplicationContext(String path) {
		super(path);		
	}

	@Override
	protected Resource getResourceByPath(String path) {
		return new FileSystemResource(path);
	}	

}
