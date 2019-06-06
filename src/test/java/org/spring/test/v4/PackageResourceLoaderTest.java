package org.spring.test.v4;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.spring.core.io.Resource;
import org.spring.core.io.support.PackageResourceLoader;

public class PackageResourceLoaderTest {

	@Test
	public void testGetResources() throws IOException{
		PackageResourceLoader loader = new PackageResourceLoader();
		Resource[] resources = loader.getResources("org.spring.dao.v4");
		Assert.assertEquals(2, resources.length);
	}
}
