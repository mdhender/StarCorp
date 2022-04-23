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

import org.dom4j.Element;

import starcorp.common.entities.IEntity;

/**
 * starcorp.common.util.Util
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 24 Sep 2007
 */
public class Util {

	public static IEntity fromXML(Element e) {
		if(e == null) {
			return null;
		}
		String className = "starcorp.common.entities." + e.attributeValue("class");
		IEntity entity;
		try {
			entity = (IEntity) Class.forName(className).newInstance();
			entity.readXML(e);
			return entity;
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

}
