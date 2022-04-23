/**
 *  Copyright 2007 Seyed Razavi
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and limitations under the License. 
 */
package starcorp.common.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * starcorp.common.util.PackageExplorer
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class PackageExplorer {

	private static List<Class> loadJar(List<Class> classes, String packageName, URL resource) throws IOException, ClassNotFoundException {
		JarURLConnection conn = (JarURLConnection) resource.openConnection();
		JarFile jarFile = conn.getJarFile();
		Enumeration<JarEntry> entries = jarFile.entries();
		String packagePath = packageName.replace('.', '/');
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if ((entry.getName().startsWith(packagePath) || entry.getName().startsWith("WEB-INF/classes/" + packagePath)) && 
					entry.getName().indexOf('$') == -1 && entry.getName().endsWith(".class")) {
				
				String className = entry.getName();
				if (className.startsWith("/"))
					className = className.substring(1);
				className = className.replace('/', '.');
				
				className = className.substring(0, className.length() - ".class".length());				
				
				Class clazz = Class.forName(className);
				classes.add(clazz);
			}
		}
		return classes;
	}
	
	private static List<Class> loadDirectory(List<Class> classes, String packageName, URL resource) throws IOException, ClassNotFoundException {
		return loadDirectory(classes, packageName, URLDecoder.decode(resource.getFile(),"UTF-8"));
	}
	
	private static List<Class> loadDirectory(List<Class> classes, String packageName, String fullPath) throws IOException, ClassNotFoundException {
		File directory = new File(fullPath);
		if (!directory.isDirectory()) 
			throw new IOException("Invalid directory " + directory.getAbsolutePath());
		
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory())
				loadDirectory(classes, packageName + '.' + file.getName(), file.getAbsolutePath());
			else if (file.getName().indexOf('$') == -1 && file.getName().endsWith(".class")) {
				String simpleName = file.getName();
				simpleName = simpleName.substring(0, simpleName.length() - ".class".length());
				String className = String.format("%s.%s", packageName, simpleName);
				Class clazz = Class.forName(className);
				classes.add(clazz);
			}
		}
		return classes;
	}
	
    public static List<Class> getClassesForPackage(String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = cld.getResources(path);
            while (resources.hasMoreElements()) {
            	URL resource = resources.nextElement();
            	if(resource.getProtocol().equalsIgnoreCase("FILE")) {
            		loadDirectory(classes, packageName, resource);
            	}
            	else if(resource.getProtocol().equalsIgnoreCase("JAR")){
            		loadJar(classes, packageName, resource);
            	}
            	else {
            		throw new ClassNotFoundException("Unknown protocol " + resource.getProtocol());
            	}
            }
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(packageName + " does not appear to be a valid package (Null pointer exception)");
        } catch (UnsupportedEncodingException encex) {
            throw new ClassNotFoundException(packageName + " does not appear to be a valid package (Unsupported encoding)");
        } catch (java.io.IOException ioex) {
            throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + packageName +": " + ioex.getMessage(),ioex);
        }
        return classes;
    }
}
