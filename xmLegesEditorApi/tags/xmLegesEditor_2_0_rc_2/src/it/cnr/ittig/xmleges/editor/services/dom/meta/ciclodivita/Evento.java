package it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita;

import it.cnr.ittig.xmleges.core.util.date.UtilDate;

/**
 * Classe per la descrizione degli eventi.
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

	/** Id dell'evento */
	String id;

	/** Data inizio evento normalizzata (AAAAMMGG) */
	String data;

	/** Fonte dell'evento (relazione) */
	Relazione fonte;
	
	/** Tipo di evento */
	String tipoEvento;
	
    /**
     * 
     * @return tipoEvento
     */
	public String getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(String tipo) {
		this.tipoEvento = tipo;
	}
/**
 * 
 * @param id: id dell'evento
 * @param data: data dell'evento
 * @param fonte: la relazione fonte dell'evento
 * @param tipo: il tipo di evento
 */
	public Evento(String id, String data, Relazione fonte, String tipo) {
		setId(id);
		setData(data);
		setFonte(fonte);
		setTipoEvento(tipo);
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
		if(tipoEvento!=null){
			retVal += ", " +tipoEvento;
		}
		if (fonte != null)
			retVal += ", " + fonte.toString();
		return retVal;
	}
	

	

}
