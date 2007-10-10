package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class Synset {
	
	private String URI;
	
	private String lang;
	
	private String def;
	
	private String lexicalForm; //sostituire con un vettore di forme lessicali?
	
	private boolean isLexicalPropCached = false;
	
	private boolean isSemanticPropCached = false;
	
	private boolean isSourceCached = false;

	private Collection sources; //rispettare l'ordine alfabetico

	public Map lexicalToSynset = null;
		
	public Map semanticToSynset = null;
	
	public Vector variants = null; //lemmi come normali stringhe..?
		
	public Synset() {
				
		lexicalToSynset = new HashMap();
		semanticToSynset = new HashMap();
		
		sources = new TreeSet();
		variants = new Vector();
		
		lexicalForm = "";
		
	}

	public void setDef(String str) {
		
		def = str;
	}
	
	public String getDef() {
		
		return def;
	}
	
	public void setLexicalForm(String lex) {
		
		lexicalForm = lex;
	}
	
	public String getLexicalForm() {
		
		return lexicalForm;
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
		
		lang = str;
	}
	
	public String getLanguage() {
		
		return lang;
	}

	public void setURI(String uri) {
		
		URI = uri;
	}
	
	public String getURI() {
	
		return URI;
	}
	
//	public void addSource(String s) {
//
//		boolean ins = false;
//		for(int i = 0; i < sources.size(); i++) {
//			String item = (String) sources.get(i);
//			if(item.toString().compareToIgnoreCase(s.toString()) < 0) continue;
//			if(item.toString().compareToIgnoreCase(s.toString()) > 0) {
//				sources.add(i, s);
//				ins = true;
//				break;
//			}
//		}
//		if(!ins) {
//			//Inserisci alla fine del vettore
//			sources.add(s);
//		}
//	}

	public void addSource(Source source) {

		sources.add(source);
	}
	
	public Collection getSources() {
		
		return sources;
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
					&& lang.equalsIgnoreCase(((Synset) obj).getLanguage()) ) {
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		
		return 1;
	}
}
