package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Synset implements Comparable {
	
	private String URI;
	
	private String LANGUAGE;
	
	private String def;
	
	private String lexicalForm = null;
	
	//Is it a concrete synset or it just contains the URI reference?
	private boolean concreteSynset = false;
	
	private boolean isLexicalPropCached = false;
	
	private boolean isSemanticPropCached = false;
	
	private boolean isSourceCached = false;
	
	private PivotOntoClass pivotClass = null;

	private Collection sources;

	private Collection variants = null;
	
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
		
		if(!lexicalForm.equalsIgnoreCase("")) {
			System.err.println("ERROR! Trying to set again lexical form on synset " + this.URI);
			return;
		}
		concreteSynset = true;
		//FIXME Show lowercase variant for IT
		//(fix original data...)
		if(LANGUAGE.equalsIgnoreCase("IT")) {
			lex = lex.toLowerCase();
		}
		lexicalForm = lex;
		addVariant(lex);
	}
	
	public String getLexicalForm() {
		
		return lexicalForm;
	}
	
	public boolean isLinked() {
		
		if(pivotClass == null) {
			return false;
		}
		
		return true;
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

		//FIXME Show lowercase variant for IT
		//(fix original data...)
		if(LANGUAGE.equalsIgnoreCase("IT")) {
			var = var.toLowerCase();
		}
		concreteSynset = true;
		variants.add(var);
	}
	
	public Collection getVariants() {
		
		return Collections.unmodifiableCollection(variants);
	}

	public String toString() {

		if(lexicalForm.trim().length() < 1) {
			return "(empty lexical form)";
		}
		return lexicalForm;
	}
	
	//Both URI and lexicalForm must match
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Synset other = (Synset) obj;
		if (URI == null) {
			if (other.URI != null)
				return false;
		} else if (!URI.equals(other.URI))
			return false;
		if (lexicalForm == null) {
			if (other.lexicalForm != null)
				return false;
		} else if (!lexicalForm.equals(other.lexicalForm))
			return false;
		return true;
	}

	public int hashCode() {
		
		return 1;
	}
	
	public int compareTo(Object obj) throws ClassCastException {
		
		if(!(obj instanceof Synset)) {
			throw new ClassCastException("Object is not a valid synset!");
		}
		String objForm = ((Synset) obj).toString();
		return this.toString().compareToIgnoreCase(objForm);
	}

}
