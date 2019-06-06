package org.spring.core.io.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.spring.core.io.FileSystemResource;
import org.spring.core.io.Resource;
import org.spring.util.Assert;
import org.spring.util.ClassUtils;

//把一个package下面的class 变成resource
public class PackageResourceLoader {
    private static final Log logger = LogFactory.getLog(PackageResourceLoader.class);

    private final ClassLoader classLoader;

    public PackageResourceLoader() {
        this.classLoader = ClassUtils.getDefaultClassLoader();
    }

    public PackageResourceLoader(ClassLoader classLoader) {
        Assert.notNull(classLoader, "ResourceLoader must not be null");
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

	public Resource[] getResources(String basePackage) throws IOException {
		Assert.notNull(basePackage, "basePackage  must not be null");
		String location = ClassUtils.convertClassNameToResourcePath(basePackage);
		ClassLoader cl = getClassLoader();
		URL url = cl.getResource(location);
		String urlFile = url.getFile();
		String filePath = URLDecoder.decode(urlFile, "UTF-8");
		File rootDir = new File(filePath);
		
		Set<File> matchingFiles = retrieveMatchingFiles(rootDir);
		Resource[] result = new Resource[matchingFiles.size()];
		int i=0;
		for (File file : matchingFiles) {
			result[i++]=new FileSystemResource(file);
		}
		return result;
		
	}
    protected Set<File> retrieveMatchingFiles(File rootDir) throws IOException {
        if (!rootDir.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping [" + rootDir.getAbsolutePath() + "] because it does not exist");
            }
            return Collections.emptySet();
        }
        if (!rootDir.isDirectory()) {
            if (logger.isWarnEnabled()) {
                logger.warn("Skipping [" + rootDir.getAbsolutePath() + "] because it does not denote a directory");
            }
            return Collections.emptySet();
        }
        if (!rootDir.canRead()) {
            if (logger.isWarnEnabled()) {
                logger.warn("Cannot search for matching files underneath directory [" + rootDir.getAbsolutePath() +
                        "] because the application is not allowed to read the directory");
            }
            return Collections.emptySet();
        }
        Set<File> result = new LinkedHashSet<File>(8);
        doRetrieveMatchingFiles(rootDir, result);
        return result;
    }

    protected void doRetrieveMatchingFiles(File dir, Set<File> result) {
        File[] dirContents = dir.listFiles();
        if (dirContents == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not retrieve contents of directory [" + dir.getAbsolutePath() + "]");
            }
            return;
        }
        for (File dirContent : dirContents) {
            if (dirContent.isDirectory()) {
                if (!dirContent.canRead()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Skipping subdirectory [" + dir.getAbsolutePath() +
                                "] because the application is not allowed to read the directory");
                    }
                } else {
                    doRetrieveMatchingFiles(dirContent, result);
                }
            } else {
                result.add(dirContent);
            }

        }
    }
}