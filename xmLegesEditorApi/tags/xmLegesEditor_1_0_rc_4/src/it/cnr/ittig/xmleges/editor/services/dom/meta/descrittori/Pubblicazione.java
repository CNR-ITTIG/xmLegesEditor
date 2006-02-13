package it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori;

import it.cnr.ittig.xmleges.core.util.date.UtilDate;

/**
 * Classe per la descrizione delle pubblicazioni.
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
public class Pubblicazione {

	/** Nome del tag: pubblicazione, ripubblicazione, rettifica, errata */
	String tag;

	/** Tipo di pubblicazione (la DTD prevede la momento valore fisso GU */
	String tipo;

	/** Numero della pubblicazione */
	String num;

	/** Data normalizzata AAAAMMGG */
	String norm;

	// TODO produttore?

	public Pubblicazione(String tag, String tipo, String num, String norm) {
		setTag(tag);
		setTipo(tipo);
		setNum(num);
		setNorm(norm);
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getNorm() {
		return norm;
	}

	public void setNorm(String norm) {
		this.norm = norm;
	}

	public String toString() {
		return tag.toUpperCase() + ", N? " + num + ", " + tipo + ", " + UtilDate.normToString(norm);
	}
}
