package it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita;

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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class Evento {

	/** Id della vigenza */
	String id;

	/** Data inizio vigenza normalizzata (AAAAMMGG) */
	String data;

	/** Fonte della vigenza (relazione) */
	Relazione fonte;
	
	String tipoEvento;
	
	String effetto;

    
	public String getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(String tipo) {
		this.tipoEvento = tipo;
	}

	public Evento(String id, String data, Relazione fonte) {
		setId(id);
		setData(data);
		setFonte(fonte);
	}
	
	public Evento(String id, String data, Relazione fonte, String effetto) {
		setId(id);
		setData(data);
		setFonte(fonte);
		setEffetto(effetto);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}


	public Relazione getFonte() {
		return fonte;
	}

	public void setFonte(Relazione fonte) {
		this.fonte = fonte;
	}

	public String toString() {
		String retVal = UtilDate.normToString(data);
		if (fonte != null)
			retVal += ", " + fonte.toString();
		return retVal;
	}
	

	public String getEffetto() {
		return effetto;
	}

	public void setEffetto(String effetto) {
		this.effetto = effetto;
	}


}
