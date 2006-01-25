/*
 * Created on Dec 15, 2004
 */
package it.cnr.ittig.xmleges.editor.services.provvedimenti;

/**
 * @author Tommaso Agnoloni
 */
public class DtdItem {

	/**
	 * Nome della Dtd
	 */
	String dtdname;

	/**
	 * Nome del file .dtd
	 */
	String filename;

	public DtdItem() {
		filename = "";
		dtdname = "";
	}

	/**
	 * Restituisce il nome della Dtd
	 */
	public String getDtdName() {
		return (dtdname);
	}

	/**
	 * Restituisce il nome del file .dtd
	 * 
	 * @return
	 */
	public String getFileName() {
		return (filename);
	}

	/**
	 * Imposta il nome della dtd
	 * 
	 * @param Nome della dtd
	 */
	public void setDtdName(String dtdname) {
		this.dtdname = dtdname;
	}

	/**
	 * Imposta il nome file della dtd
	 * 
	 * @param filename = nome del file dtd
	 */
	public void setFileName(String filename) {
		this.filename = filename;
	}

	public String toString() {
		return (this.dtdname);
	}
}
