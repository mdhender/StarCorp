package starcorp.common.types;

import org.dom4j.Element;

public interface ICoordinates {

	public abstract void readXML(Element e);

	public abstract Element toXML(Element parent);

	public abstract int getDistance(ICoordinates o);

}