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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * starcorp.common.util.ZipTools
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 30 Sep 2007
 */
public class ZipTools {

	public static void zip(File[] files, String zipFile) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		byte[] buf = new byte[1024];
		for(File file : files) {
			FileInputStream in = new FileInputStream(file);
			out.putNextEntry(new ZipEntry(file.getName()));
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
	        }
			
			out.closeEntry();
			in.close();
		}
		out.close();
	}
	
	public static void unzip(String zipFile, String outputDirectory) throws IOException {
		ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry entry;
		while((entry = in.getNextEntry()) != null) {
			File file = new File(outputDirectory,entry.getName());
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        out.close();
	        in.closeEntry();
		}
		in.close();
		
	}
}
