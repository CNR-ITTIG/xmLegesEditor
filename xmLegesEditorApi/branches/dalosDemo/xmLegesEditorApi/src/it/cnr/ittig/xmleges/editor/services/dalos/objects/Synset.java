package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

public class Synset {
	
	private String URI;
	
	private String lang;
	
	private String def;
	
	private String lexicalForm; //sostituire con un vettore di forme lessicali?
	
	private boolean isPropCached = false;
	
	private boolean isSourceCached = false;

	private Vector sources; //rispettare l'ordine alfabetico

	public Map lexicalToSynset = null;
		
	public Map semanticToSynset = null;
	
	public Vector variants = null; //lemmi come normali stringhe..?
		
	public Synset() {
				
		lexicalToSynset = new HashMap();
		semanticToSynset = new HashMap();
		
		sources = new Vector();
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

	public void setPropCached(boolean status) {
		
		isPropCached = status;
	}
	
	public boolean isPropCached() {
		
		return isPropCached;
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
	
	public boolean addSource(String s) {

		boolean ins = false;
		for(int i = 0; i < sources.size(); i++) {
			String item = (String) sources.get(i);
			if(item.toString().compareToIgnoreCase(s.toString()) < 0) continue;
			if(item.toString().compareToIgnoreCase(s.toString()) > 0) {
				sources.add(i, s);
				ins = true;
				break;
			}
		}
		if(!ins) {
			//Inserisci alla fine del vettore
			sources.add(s);
		}
		return true;
	}

	public Vector getSources() {
		
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
