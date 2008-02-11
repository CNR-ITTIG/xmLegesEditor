package it.cnr.ittig.xmleges.editor.services.dalos.objects;

public abstract class DalosOntoClass {
	
	private String URI;
	
	public String name;

	public DalosOntoClass(String name) {
		
		this.name = name;
		this.URI = null;
	}
	
	public void setURI(String uri) {
		
		URI = uri;
	}
	
	public String getURI() {
	
		return URI;
	}

	public void setName(String str) {
		
		name = str;
	}
	
	public String getName() {
		
		return name;
	}
	
	public boolean equals(Object obj) {

		if(obj == null) {
			return false;
		}
		if(obj instanceof DalosOntoClass) {
			DalosOntoClass dalosObj = (DalosOntoClass) obj;
			
			//If there is a URI, check it
			if(this.getURI() != null || dalosObj.getURI() != null) {
				if(this.getURI().equals(dalosObj.getURI())) { 
					return true;
				}
				return false;
			}
			
			//Both have no URI, check names
			if(this.getName().trim().equals("") ||
					dalosObj.getName().trim().equals("") ) {
				return false;
			}
			if(this.getName().equals(dalosObj.getName())) { 
				return true;
			}
			
		}
		return false;
	}
	
	public int hashCode() {
		return 1;
	}

}
