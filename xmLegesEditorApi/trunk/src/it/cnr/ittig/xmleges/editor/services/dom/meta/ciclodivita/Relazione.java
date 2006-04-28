package it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita;

/**
 * Classe per la descrizione delle relazioni.
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
public class Relazione {

	// FIXME:  nel caso di relazione di tipo giurisprudenza manca l'effetto (normativo|interpretativo)
	
	/** Nome del tag: originale, attiva, passiva, giurisprudenza, haallegato, allegatodi */
	String tagTipoRelazione;

	/** Id del tag */
	String id;

	/** Link della relazione */
	String link;
	
	/** Effetto della relazione */
	String effetto;

	public Relazione(String tag, String id, String link) {
		setTagTipoRelazione(tag);
		setId(id);
		setLink(link);
		setEffetto(null);
	}
	
	public Relazione(String tag, String id, String link, String effetto) {
		setTagTipoRelazione(tag);
		setId(id);
		setLink(link);
		setEffetto(effetto);
	}

	public String getTagTipoRelazione() {
		return tagTipoRelazione;
	}

	public void setTagTipoRelazione(String tag) {
		this.tagTipoRelazione = tag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public String getEffetto() {
		return effetto;
	}

	public void setEffetto(String effetto) {
		this.effetto = effetto;
	}


	public String toString() {
		return tagTipoRelazione.toUpperCase() + ", " + link;
	}
}
