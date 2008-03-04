package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import java.util.Collection;
import java.util.HashSet;

public class TreeOntoClass extends DalosOntoClass {
	
	//Links to classified resources 
	//(DALOS: these are the synsets in the 'global' language)
	private Collection resources;
	
	//Linked concepts
	private Collection concepts;

	public TreeOntoClass(String name) {
		
		super(name);
		
		resources = new HashSet();
		concepts = new HashSet();
	}
	
	public boolean addTerm(Synset syn) {
		
		return resources.add(syn);
	}
	
	public boolean removeTerm(Synset syn) {
		
		return resources.remove(syn);
	}
	
	public Collection getResources() {
		
		return resources;
	}
	
	public boolean addConcept(PivotOntoClass poc) {
		
		return concepts.add(poc);
	}
	
	public boolean removeConcept(PivotOntoClass poc) {
		
		return concepts.remove(poc);
	}
	
	public Collection getConcepts() {
		
		return concepts;
	}
		
	public String toString() {
		
		String str = name.trim();
		if(str.equals("")) {
			str = "(no name)";
		}
		if(str.length() > 32) {
			str = str.substring(0, 30) + "..";
		}
		return str;
	}
	
}
