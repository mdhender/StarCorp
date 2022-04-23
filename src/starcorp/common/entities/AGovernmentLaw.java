package starcorp.common.entities;

import java.io.IOException;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import starcorp.common.types.GalacticDate;

public abstract class AGovernmentLaw implements IEntity, Comparable<AGovernmentLaw> {

	private long ID;
	private int version;
	private long colony;
	private boolean available;
	private GalacticDate issuedDate;
	private GalacticDate closedDate;

	public AGovernmentLaw() {
		super();
	}

	public long getColony() {
		return colony;
	}

	public void setColony(long colony) {
		this.colony = colony;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public GalacticDate getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(GalacticDate issuedDate) {
		this.issuedDate = issuedDate;
	}

	public void readXML(Element e) {
		this.ID = Long.parseLong(e.attributeValue("ID","0"));
		this.colony = Long.parseLong(e.attributeValue("colony","0"));
		this.available = Boolean.parseBoolean(e.attributeValue("available","false"));
		this.issuedDate = new GalacticDate(e.element("issued").element("date"));
	}

	public Element toBasicXML(Element parent) {
		Element root = parent.addElement("entity");
		root.addAttribute("ID", String.valueOf(ID));
		root.addAttribute("class", getClass().getSimpleName());
		root.addAttribute("colony", String.valueOf(colony));
		root.addAttribute("available",String.valueOf(available));
		issuedDate.toXML(root.addElement("issued"));
		return root;
	}
	public Element toFullXML(Element parent) {
		return toBasicXML(parent);
	}
	

	@Override
	public String toString() {
		return "(" + getClass().getSimpleName() + ": " + ID + ")"; 
	}

	public long getID() {
		return ID;
	}

	public void setID(long id) {
		ID = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (ID ^ (ID >>> 32));
		result = prime * result + version;
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
		final AGovernmentLaw other = (AGovernmentLaw) obj;
		if (ID != other.ID)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	public GalacticDate getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(GalacticDate closedDate) {
		this.closedDate = closedDate;
	}

	public void printXML(Writer out) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(out,format);
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("starcorp");
		writer.write(toFullXML(root));
		writer.close();
	}

	public String getDisplayName() {
		return getClass().getSimpleName() +" [" + getID() +"]";
	}

	public int compareTo(AGovernmentLaw o) {
		return (int) ((o == null ? 0 :o.ID) - this.ID);
	}

}