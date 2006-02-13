package it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori;

import it.cnr.ittig.xmleges.core.util.date.UtilDate;

/**
 * Classe per la descrizione delle vigenze.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class Vigenza {

	/** Id della vigenza */
	String id;

	/** Data inizio vigenza normalizzata (AAAAMMGG) */
	String inizio;

	/** Data fine vigenza normalizzata (AAAAMMGG) */
	String fine;

	/** Fonte della vigenza (relazione) */
	Relazione fonte;

	public Vigenza(String id, String inizio) {
		setId(id);
		setInizio(inizio);
	}

	public Vigenza(String id, String inizio, String fine, Relazione fonte) {
		this(id, inizio);
		setFine(fine);
		setFonte(fonte);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInizio() {
		return inizio;
	}

	public void setInizio(String inizio) {
		this.inizio = inizio;
	}

	public boolean hasFineFonte() {
		return this.fine != null && this.fonte != null;
	}

	public String getFine() {
		return fine;
	}

	public void setFine(String fine) {
		this.fine = fine;
	}

	public Relazione getFonte() {
		return fonte;
	}

	public void setFonte(Relazione fonte) {
		this.fonte = fonte;
	}

	public String toString() {
		String retVal = UtilDate.normToString(inizio);
		if (fine != null)
			retVal += " - " + UtilDate.normToString(fine);
		if (fonte != null)
			retVal += ", " + fonte.toString();
		return retVal;
	}

}
