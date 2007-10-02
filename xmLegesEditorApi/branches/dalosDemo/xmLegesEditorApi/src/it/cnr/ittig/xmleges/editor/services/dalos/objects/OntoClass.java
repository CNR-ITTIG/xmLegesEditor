package it.cnr.ittig.xmleges.editor.services.dalos.objects;

public class OntoClass {
	
	public String name = null;
	
	public OntoClass(String str) {
		
		name = str;
	}
	
	public void setName(String str) {
		
		name = str;
	}
	
	public String getName() {
		
		return name;
	}
	
	public String toString() {
		
		return name;
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
