package starcorp.common.entities;

import java.io.IOException;
import java.io.Writer;

import org.dom4j.Element;

public interface IEntity {

	public abstract void readXML(Element e);

	public abstract Element toBasicXML(Element parent);

	public abstract Element toFullXML(Element parent);

	public abstract long getID();

	public abstract void setID(long id);

	public abstract void printXML(Writer out) throws IOException;

	public abstract int getVersion();

	public abstract void setVersion(int version);

	public abstract String getDisplayName();
}