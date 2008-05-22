package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

public class PivotOntoClass extends DalosOntoClass {
	
	//Links to other ontological class objects
	private Collection links;
	
	//Links to synset in different languages
	private Collection terms;
	
	//Interlingual links
	private Collection cohypoConcepts;
	private Collection eqsynConcepts;
	private Collection fuzzyConcepts;
	private Collection hyperConcepts;
	private Collection hypoConcepts;
	
	public PivotOntoClass() {
		
		this("");
	}
	
	public PivotOntoClass(String name) {
		
		super(name);
		
		links = new HashSet();
		terms = new HashSet();
		
		cohypoConcepts = new HashSet();
		eqsynConcepts = new HashSet();
		fuzzyConcepts = new HashSet();
		hyperConcepts = new HashSet();
		hypoConcepts = new HashSet();
	}

	public Collection getLinks() {
				
		return links;
	}
	
	public boolean addLink(TreeOntoClass toc) {
		
		return links.add(toc);
	}
	
	public boolean removeLink(TreeOntoClass toc) {
		
		return links.remove(toc);
	}
	
	public boolean addTerm(Synset syn) {
		
		return terms.add(syn);
	}
	
	public boolean removeTerm(Synset syn) {
		
		return terms.remove(syn);
	}
	
	public Collection getTerms(String lang) {
		/*
		 * Get lexicalizations in the specified language.
		 */
		
		Collection results = new TreeSet();
		
		for(Iterator i = terms.iterator(); i.hasNext(); ) {
			Synset item = (Synset) i.next();
			if(item.getLanguage().equalsIgnoreCase(lang)) {
				results.add(item);
			}
		}
		
		return results;
	}
	
	public Collection getCohypoConcepts() {
		return Collections.unmodifiableCollection(cohypoConcepts);
	}

	public Collection getEqsynConcepts() {
		return Collections.unmodifiableCollection(eqsynConcepts);
	}

	public Collection getFuzzyConcepts() {
		return Collections.unmodifiableCollection(fuzzyConcepts);
	}

	public Collection getHyperConcepts() {
		return Collections.unmodifiableCollection(hyperConcepts);
	}
	
	public Collection getHypoConcepts() {
		return Collections.unmodifiableCollection(hypoConcepts);
	}
	
	public boolean addCohypoConcept(PivotOntoClass l) {
		return cohypoConcepts.add(l);
	}
	
	public boolean addEqsynConcept(PivotOntoClass l) {
		return eqsynConcepts.add(l);
	}

	public boolean addFuzzyConcept(PivotOntoClass l) {
		return fuzzyConcepts.add(l);
	}

	public boolean addHyperConcept(PivotOntoClass l) {
		return hyperConcepts.add(l);
	}

	public boolean addHypoConcept(PivotOntoClass l) {
		return hypoConcepts.add(l);
	}

	public String toString() {

		String str = this.getURI();
		if(str == null) {
			str = "(NO URI!)";
		}

		return str;
	}
}
