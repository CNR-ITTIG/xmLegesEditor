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
public class ClasseItem {

	/**
	 * Nome della classe di provvedimenti
	 */
	private String name;

	/**
	 * Lista dei provvedimenti appartenenti a ciascuna classe
	 */
	private ProvvedimentiItem[] provvedimentilist;

	public ClasseItem() {
		name = "";
		provvedimentilist = null;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the provvedimentilist.
	 */
	public ProvvedimentiItem[] getProvvedimentiList() {
		return provvedimentilist;
	}

	/**
	 * Restituisce il provvedimento in posizione index
	 * 
	 * @param index posizione richiesta
	 * @return provvedimento
	 */
	public ProvvedimentiItem getProvvedimentoAt(int index) {
		try {
			return provvedimentilist[index];
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param provvedimentilist The provvedimentilist to set.
	 */
	public void setProvvedimentiList(ProvvedimentiItem[] provvedimentilist) {
		this.provvedimentilist = provvedimentilist;
	}

	public void setProvvedimentiListAt(ProvvedimentiItem provvedimento, int index) {
		try {
			this.provvedimentilist[index] = provvedimento;
		} catch (Exception e) {
			// System.out.println(e.toString());

		}
	}

	public String toString() {
		return (this.name);
	}

}
