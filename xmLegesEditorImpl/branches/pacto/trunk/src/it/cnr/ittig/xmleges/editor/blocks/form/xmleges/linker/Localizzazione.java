/**
 * 
 */
package it.cnr.ittig.xmleges.editor.blocks.form.xmleges.linker;

import java.util.Vector;

/**
 * 
 * <hr>
 * Project : xmLegesEditorImpl<br>
 * package : it.cnr.ittig.xmleges.editor.blocks.form.xmleges.linker<br>
 * Type Name : <b>Localizzazione</b><br>
 * Comment :<br>
 * 
 * <hr>
 * I+ S.r.l. 30/nov/07<br>
 * <hr>
 * 
 * @author Macchia<br>
 *         <hr>
 * 
 */
public class Localizzazione {

	private String nome;

	private String urn;

	private Vector enti;

	public Localizzazione(String name) {
		this.nome = name;
		enti = new Vector();
	}

	public Vector getEnti() {
		return enti;
	}

	public void setEnti(Vector enti) {
		this.enti = enti;
	}

	public String getNome() {
		return this.nome;
	}

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}

	/**
	 * Aggiunge un Ente alla Localizzazione
	 * 
	 * @param ente
	 */
	public void addEnti(Ente ente) {
		this.enti.add(ente);
	}
}
