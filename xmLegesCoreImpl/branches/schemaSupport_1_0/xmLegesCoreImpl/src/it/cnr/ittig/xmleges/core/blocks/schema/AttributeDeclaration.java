package it.cnr.ittig.xmleges.core.blocks.schema;

import java.io.Serializable;

/**
 * @author agnoloni
 * 
 */
public class AttributeDeclaration implements Serializable {

	public String type;

	public String valueDefault;

	public String value;

	public AttributeDeclaration(String _type, String _valueDefault, String _value) {
		type = _type;
		valueDefault = _valueDefault;
		value = _value;
	}
	
	public String toString(){
		return "type="+(type.equals("")?null:type)+" default="+valueDefault+" value="+value;
	}
	
}
