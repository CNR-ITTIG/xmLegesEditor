package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Synset {
	
	private String URI;
	
	private String lang;
	
	private String def;
	
	private String lexicalForm; //sostituire con un vettore di forme lessicali?
	
	private boolean isCached = false;

	public Map lexicalToSynset = null;
		
	public Map semanticToSynset = null;
		
	public Collection sources; //sostituire con vettore di rif. esterni?
	
	public Synset() {
		
		this("(empty)", "");
	}

	public Synset(String lexical) {
		
		this(lexical, "");
	}
	
	public Synset(String lexical, String language) {
		
		lexicalForm = lexical;
		lang = language;
		
		lexicalToSynset = new HashMap();
		semanticToSynset = new HashMap();
		
		sources = new HashSet();
	}

	public void setDef(String str) {
		
		def = str;
	}
	
	public String getDef() {
		
		return def;
	}
	
	public String getLexicalForm() {
		
		return lexicalForm;
	}

	public void setCached(boolean status) {
		
		isCached = status;
	}
	
	public boolean isCached() {
		
		return isCached;
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
	
	public String toString() {
		
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
