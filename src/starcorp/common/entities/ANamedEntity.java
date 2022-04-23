package starcorp.common.entities;

import org.dom4j.Element;

public abstract class ANamedEntity extends ABaseEntity {

	protected String name;

	public void readXML(Element e) {
		super.readXML(e);
		this.name = e.attributeValue("name");
	}
	
	public ANamedEntity() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("name", name);
		return e;
	}

	@Override
	public String toString() {
		return name + " " + super.toString();
	}

	public String getDisplayName() {
		return getName() +" [" + getID() +"]";
	}

	@Override
	public int compareTo(ABaseEntity o) {
		if(name != null && o instanceof ANamedEntity) {
			ANamedEntity entity = (ANamedEntity) o;
			if(entity.name != null)
				entity.name.compareTo(name);
		}
		return super.compareTo(o);
	}

}