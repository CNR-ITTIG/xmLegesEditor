package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

public class TreeOntoClass extends DalosOntoClass {
	
	//Links to classified resources 
	//(DALOS: these are the synsets in the 'global' language)
	//private Collection resources;
	
	//Linked concepts
	private Collection concepts;
	
	private Collection semanticPaths;
	
	private Map semanticProperties;

	public TreeOntoClass(String name) {
		
		super(name);
		
		//resources = new HashSet();
		concepts = new HashSet();
		semanticPaths = new TreeSet();
		semanticProperties = new HashMap();
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

	public Map getSemanticProperties() {
		return semanticProperties;
	}

	public Collection getSemanticPaths() {
		return semanticPaths;
	}

	public void setSemanticPaths(Collection semanticPaths) {
		this.semanticPaths = semanticPaths;
	}
	
	public boolean addSemanticPaths(Object path) {
		return this.semanticPaths.add(path);
	}
	
	public void addSemanticProperty(String rel, TreeOntoClass toc) {
		
		Collection values = (Collection) semanticProperties.get(rel);
		//System.out.println("addSemProp rel: " + rel + " values: " + values + " toc: " + toc);
		if(values == null) {
			values = new TreeSet();
			semanticProperties.put(rel, values);
		}
		values.add(toc);
	}
}
