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


	
	/** Nome del tag: originale, attiva, passiva, giurisprudenza, haallegato, allegatodi */
	String tagTipoRelazione;

	/** Id del tag */
	String id;

	/** Link della relazione */
	String link;
	
	/** Effetto/tipo allegato della relazione */
	String effetto_tipoall;

	/**
	 * Costruttore di Relazione
	 * @param tag  tipo relazione
	 * @param id id relazione
	 * @param link link della urn
	 */
	public Relazione(String tag, String id, String link) {
		setTagTipoRelazione(tag);
		setId(id);
		setLink(link);
		setEffetto_tipoall(null);
	}
	/**
	 * Costruttore di Relazione nel caso di presenza effetto/tipoallegato
	 * @param tag
	 * @param id
	 * @param link
	 * @param effetto_tipoall
	 */
	public Relazione(String tag, String id, String link, String effetto_tipoall) {
		setTagTipoRelazione(tag);
		setId(id);
		setLink(link);
		setEffetto_tipoall(effetto_tipoall);
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
	
	public String getEffetto_tipoall() {
		return effetto_tipoall;
	}

	public void setEffetto_tipoall(String effetto_tipoall) {
		this.effetto_tipoall = effetto_tipoall;
	}


	public String toString() {
		String toString;
		toString=tagTipoRelazione.toUpperCase() + ", " + link;
		if(getTagTipoRelazione().equals("giurisprudenza")
				||getTagTipoRelazione().equals("haallegato")
				||getTagTipoRelazione().equals("allegatodi")){
			toString+=", "+getEffetto_tipoall();
		}
		
		return toString;
	}
}
