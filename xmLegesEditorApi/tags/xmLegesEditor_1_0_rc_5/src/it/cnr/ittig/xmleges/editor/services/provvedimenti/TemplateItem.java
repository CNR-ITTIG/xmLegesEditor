/*
 * Created on Dec 15, 2004 TODO To change the template for this generated file
 * go to Window - Preferences - Java - Code Style - Code Templates
 */
package it.cnr.ittig.xmleges.editor.services.provvedimenti;

/**
 * @author Lorenzo Sarti TODO To change the template for this generated type
 *         comment go to Window - Preferences - Java - Code Style - Code
 *         Templates
 */
public class TemplateItem {
	/**
	 * Nome del file
	 */
	String filename;

	/**
	 * Tipo documento: "Articolato" o "SemiArticolato"
	 */
	String type;

	/**
	 * documento numerato o non numerato: "SI" o "NO"
	 */
	boolean numerato;

	/**
	 * tipo atto della DTD
	 */
	String tag;

	/**
	 * Indica se l'informazione numerato/non numerato e' necessaria
	 */
	boolean hasnumerato;

	public TemplateItem() {
		filename = "";
		type = "";
		numerato = true;
		tag = "";
		hasnumerato = true;

	}

	/**
	 * Imposta l'attributo hasnumerato, indicando se l'informazione sulla
	 * numerazione e' di interesse
	 */
	public void hasNumerato(boolean b) {
		this.hasnumerato = b;
	}

	/**
	 * Restituisce il nome del file generico
	 * 
	 * @return
	 */
	public String getFileName() {
		return (filename);
	}

	/**
	 * Restituisce il tipo di template: "Articolato" o "SemiArticolato"
	 */
	public String getType() {
		return (type);
	}

	/**
	 * Restituisce l'informazione relativa alla numerazione del documento
	 */
	public boolean isNumerato() {
		return (numerato);
	}

	/**
	 * Restituisce il tipo di atto della DTD
	 * 
	 * @return
	 */
	public String getTag() {
		return (tag);
	}

	/**
	 * Imposta il nome file del template
	 * 
	 * @param filename = nome del file di template
	 */
	public void setFileName(String filename) {
		this.filename = filename;
	}

	/**
	 * Imposta il tipo di documento
	 * 
	 * @param Tipo di documento: valori ammessi "Articolato" e "SemiArticolato"
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Imposta la proprieta' numerato del template
	 * 
	 * @param numerato: true = template relativo a documento numerato, false
	 *        altrimento
	 */
	public void setNumerato(boolean numerato) {
		this.numerato = numerato;
	}

	/**
	 * Imposta la proprieta' tag del template
	 * 
	 * @param tipa atto da impostare
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	public String toString() {
		if (hasnumerato) {
			if (this.isNumerato())
				return (this.type + " Numerato");
			else
				return (this.type + " Non Numerato");
		} else
			return (this.type);

	}
}
