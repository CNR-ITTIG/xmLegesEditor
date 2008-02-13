package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Synset implements Comparable {
	
	private String URI;
	
	private String LANGUAGE;
	
	private String def;
	
	private String lexicalForm; //sostituire con un vettore di forme lessicali?
	
	//Is it a concrete synset or it just contains the URI reference?
	private boolean concreteSynset = false;
	
	private boolean isLexicalPropCached = false;
	
	private boolean isSemanticPropCached = false;
	
	private boolean isSourceCached = false;
	
	private PivotOntoClass pivotClass = null;

	private Collection sources; //rispettare l'ordine alfabetico

	private Collection variants = null; //lemmi come normali stringhe..? ORDINE ALFABETICO !
	
	public Map lexicalToSynset = null;
		
	public Map semanticToSynset = null;		
	
	public Synset(String language) {
				
		LANGUAGE = language;
		
		lexicalToSynset = new HashMap();
		semanticToSynset = new HashMap();
		
		sources = new TreeSet();
		variants = new TreeSet();
		
		lexicalForm = "";
		
	}

	public void setDef(String str) {
		
		concreteSynset = true;
		def = str;
	}
	
	public String getDef() {
		
		return def;
	}
	
	public void setLexicalForm(String lex) {
		
		concreteSynset = true;
		lexicalForm = lex;
	}
	
	public String getLexicalForm() {
		
		return lexicalForm;
	}
	
	public void setPivotClass(PivotOntoClass poc) {
		
		pivotClass = poc;
	}
	
	public PivotOntoClass getPivotClass() {
		
		return pivotClass;
	}

	public void setConcreteSynset(boolean status) {
		
		concreteSynset = status;
	}
	
	public boolean isConcreteSynset() {
		
		return concreteSynset;
	}

	public void setLexicalPropCached(boolean status) {
		
		isLexicalPropCached = status;
	}
	
	public boolean isLexicalPropCached() {
		
		return isLexicalPropCached;
	}

	public void setSemanticPropCached(boolean status) {
		
		isSemanticPropCached = status;
	}
	
	public boolean isSemanticPropCached() {
		
		return isSemanticPropCached;
	}
	
	public void setSourceCached(boolean status) {
		
		isSourceCached = status;
	}
	
	public boolean isSourceCached() {
		
		return isSourceCached;
	}

	public void setLanguage(String str) {
		
		LANGUAGE = str;
	}
	
	public String getLanguage() {
		
		return LANGUAGE;
	}

	public void setURI(String uri) {
		
		URI = uri;
	}
	
	public String getURI() {
	
		return URI;
	}

	public void addSource(Source source) {

		sources.add(source);
	}
	
	public Collection getSources() {
		
		return sources;
	}

	public void addVariant(String var) {

		concreteSynset = true;
		variants.add(var);
	}
	
	public Collection getVariants() {
		
		return variants;
	}

	public String toString() {

		if(lexicalForm.trim().length() < 1) {
			return "(empty)";
		}
		return lexicalForm;
	}
	
	public boolean equals(Object obj) {

		if(obj instanceof Synset) {
			if(lexicalForm.equalsIgnoreCase(((Synset) obj).getLexicalForm())
					&& LANGUAGE.equalsIgnoreCase(((Synset) obj).getLanguage()) ) {
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		
		return 1;
	}
	
	public int compareTo(Object obj) throws ClassCastException {
		
		if(!(obj instanceof Synset)) {
			throw new ClassCastException("Object is not a valid synset!");
		}
		String objForm = ((Synset) obj).toString();
		return this.toString().compareTo(objForm);
	}

}
