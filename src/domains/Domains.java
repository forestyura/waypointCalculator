package domains;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import domains.Fields.Field;
import domains.Machinery.Machine;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Domains {
	
	@XmlElement(name="field")
	@XmlElementWrapper
	public List<Field> fields;
	
	@XmlElement(name="machine")
	@XmlElementWrapper
	public List<Machine> machines;
	
	public Domains() {
		super();
	}

	public Domains(List<Field> fields, List<Machine> machines) {
		super();
		this.fields = fields;
		this.machines = machines;
	}
	
}
