package starcorp.common.entities;

import org.dom4j.Element;

import starcorp.common.types.CoordinatesPolar;

public class StarSystemEntity extends ANamedEntity {
	protected long system;
	protected CoordinatesPolar location;
	protected boolean asteroid;
	protected boolean gasfield;
	
	public StarSystemEntity() {
		super();
	}

	public CoordinatesPolar getLocation() {
		return location;
	}
	public void setLocation(CoordinatesPolar location) {
		this.location = location;
	}

	@Override
	public void readXML(Element e) {
		super.readXML(e);
		this.system = Long.parseLong(e.attributeValue("system","0"));
		this.location = new CoordinatesPolar(e);
		if("true".equals(e.attributeValue("asteroid")))
			asteroid = true;
		if("true".equals(e.attributeValue("gasfield")))
			gasfield = true;
	}

	@Override
	public Element toBasicXML(Element parent) {
		Element e =super.toBasicXML(parent);
		e.addAttribute("system", String.valueOf(system));
		if(isAsteroid()) e.addAttribute("asteroid", "true");
		if(isGasfield()) e.addAttribute("gasfield", "true");
		if(location != null)
			location.toXML(e);
		return e;
	}

	@Override
	public String toString() {
		return super.toString() + " @ system " + system + " " + location;
	}

	public boolean isAsteroid() {
		return asteroid;
	}

	public void setAsteroid(boolean asteroid) {
		this.asteroid = asteroid;
	}

	public boolean isGasfield() {
		return gasfield;
	}

	public void setGasfield(boolean gasfield) {
		this.gasfield = gasfield;
	}

	public long getSystem() {
		return system;
	}

	public void setSystem(long system) {
		this.system = system;
	}
	
}