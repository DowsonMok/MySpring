package org.spring.test.v1;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.spring.core.io.ClassPathResource;
import org.spring.core.io.FileSystemResource;
import org.spring.core.io.Resource;

public class ResourceTest {

	@Test
	public void testClassPathResource() throws Exception {

		Resource r = new ClassPathResource("petstore-v1.xml");

		InputStream is = null;

		try {
			is = r.getInputStream();
			// 注意：这个测试其实并不充分！！
			Assert.assertNotNull(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}

	}

	@Test
	public void testFileSystemResource() throws Exception {

		/*Resource r = new FileSystemResource("C:\\Users\\MySpring\\src\\test\\resources\\petstore-v1.xml");

		InputStream is = null;

		try {
			is = r.getInputStream();
			// 注意：这个测试其实并不充分！！
			Assert.assertNotNull(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
*/
	}

}
