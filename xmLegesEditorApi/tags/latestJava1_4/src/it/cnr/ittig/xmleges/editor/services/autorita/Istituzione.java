/*
 * Created on Feb 16, 2005 TODO To change the template for this generated file
 * go to Window - Preferences - Java - Code Style - Code Templates
 */
package it.cnr.ittig.xmleges.editor.services.autorita;

import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author sarti TODO To change the template for this generated type comment go
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
/*
 * Istituzione.java Created on 3 giugno 2004, 14.34
 */

public class Istituzione {
	String urn;

	String nome;

	java.util.Date datainizio;

	java.util.Date datafine;

	java.util.Vector sottoistituzioni;

	/** Creates a new instance of Istituzione */
	public Istituzione() {
		urn = "";
		nome = "";
		datainizio = null;
		datafine = null;
		sottoistituzioni = new Vector();
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setDatainizio(Date datainizio) {
		this.datainizio = datainizio;
	}

	public String toString() {
		if (getNome().equals("") && (getUrn().equals("")))
			return ("");
		if (getNome().equals("")) {
			StringTokenizer st = new StringTokenizer(this.getUrn(), ".");
			String toString = "";
			while (st.hasMoreTokens())
				toString += st.nextToken() + " ";
			return (toString.trim());
		} else
			return (this.getNome());

	}

	public void Print() {
		if (this.getSottoIstituzioni().size() > 0) {
			for (int i = 0; i < getSottoIstituzioni().size(); i++) {
				((Istituzione) (getSottoIstituzioni().get(i))).Print();
			}
		}

	}

	public void setDatafine(Date datafine) {
		this.datafine = datafine;
	}

	public void addSottoIstituzione(Istituzione newist) {
		sottoistituzioni.add(newist);
	}

	public void setIstituzione(String urn, String nome, Date datainizio, Date datafine, Istituzione ist) {
		setUrn(urn);
		setNome(nome);
		setDatainizio(datainizio);
		setDatafine(datafine);
		addSottoIstituzione(ist);
	}

	public String getUrn() {
		return urn;
	}

	public String getNome() {
		if (nome.equals("")) {
			StringTokenizer st = new StringTokenizer(this.getUrn(), ".");
			String toString = "";
			while (st.hasMoreTokens())
				toString += st.nextToken() + " ";
			return (toString.trim());
		} else
			return (nome);

	}

	public Date getDatainizio() {
		return datainizio;
	}

	public Date getDatafine() {
		return datafine;
	}

	public Vector getSottoIstituzioni() {
		return sottoistituzioni;
	}

}
