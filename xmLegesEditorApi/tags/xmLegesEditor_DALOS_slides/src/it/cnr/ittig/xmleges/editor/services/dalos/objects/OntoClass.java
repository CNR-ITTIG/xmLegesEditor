package it.cnr.ittig.xmleges.editor.services.dalos.objects;

public class OntoClass {
	
	public String name = null;
	
	public OntoClass(String str) {
		
		if(str.length() > 24) {
			str = str.substring(0, 24);
		}
		name = str;
	}
	
	public void setName(String str) {
		
		name = str;
	}
	
	public String getName() {
		
		return name;
	}
	
	public String toString() {
		
		String str = name;
		if(str.length() > 32) {
			str = str.substring(0, 30) + "..";
		}
		return str;
	}
	
	public boolean equals(Object obj) {

		if(obj == null) {
			return false;
		}
		if(obj instanceof OntoClass) {
			if(this.getName().equals(((OntoClass) obj).getName())) { 
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Lascia tutto nella mani di equals() ! 
	 */
	public int hashCode() {
		return 1;
	}

}
