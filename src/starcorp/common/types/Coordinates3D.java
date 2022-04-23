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
package starcorp.common.types;

import org.dom4j.Element;

/**
 * starcorp.common.types.Coordinates3D
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class Coordinates3D implements ICoordinates{
	private int x;
	private int y;
	private int z;
	
	public Coordinates3D() {
		
	}
	
	public Coordinates3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Coordinates3D(Element e) {
		readXML(e);
	}
	
	public void readXML(Element e) {
		this.x = Integer.parseInt(e.attributeValue("x","0"));
		this.y = Integer.parseInt(e.attributeValue("y","0"));
		this.z = Integer.parseInt(e.attributeValue("z","0"));		
	}
	
	public Element toXML(Element parent) {
		parent.addAttribute("x", String.valueOf(x));
		parent.addAttribute("y", String.valueOf(y));
		parent.addAttribute("z", String.valueOf(z));
		return parent;
	}
	
	public int getDistance(ICoordinates o) {
		Coordinates3D other = (Coordinates3D) o;
		int distance = 0;
		Coordinates3D current = new Coordinates3D();
		current.x = this.x;
		current.y = this.y;
		current.z = this.z;
		while(!current.equals(other)) {
			if(current.x > other.x) {
				current.x -= 1;
			}
			else if(current.x < other.x) {
				current.x += 1;
			}
			if(current.y > other.y) {
				current.y -= 1;
			}
			else if(current.y < other.y) {
				current.y += 1;
			}
			if(current.z > other.z) {
				current.z -= 1;
			}
			else if(current.z < other.z) {
				current.z += 1;
			}
			distance++;
		}
		return distance;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Coordinates3D other = (Coordinates3D) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + x + " , " + y + " , " + z + ")";
	}
	
}
