package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class PivotOntoClass extends DalosOntoClass {
	
	//Links to other ontological class objects
	private Collection links;
	
	//Links to synset in different languages
	private Collection terms;
	
	public PivotOntoClass() {
		
		super("");
	}
	
	public PivotOntoClass(String name) {
		
		super(name);
		
		links = new HashSet();
		terms = new HashSet();
	}

	public boolean addClass(DalosOntoClass dc) {
		
		return links.add(dc);
	}
	
	public boolean removeClass(DalosOntoClass dc) {
		
		return links.remove(dc);
	}
	
	public boolean addTerm(Synset syn) {
		
		return terms.add(syn);
	}
	
	public boolean removeTerm(Synset syn) {
		
		return terms.remove(syn);
	}
	
	public Synset getTerm(String lang) {
		
		for(Iterator i = terms.iterator(); i.hasNext(); ) {
			Synset item = (Synset) i.next();
			if(item.getLanguage().equalsIgnoreCase(lang)) {
				return item;
			}
		}
		
		return null;
	}
	
	public String toString() {

		String str = this.getURI();
		if(str == null) {
			str = "(NO URI!)";
		}

		return str;
	}
}
