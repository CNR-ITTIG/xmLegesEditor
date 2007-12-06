package it.cnr.ittig.xmleges.editor.services.autorita;

import it.cnr.ittig.services.manager.Service;

import java.util.Date;

/**
 * Servizio per l'interrogazione del Registro delle Autorit&agrave; Emittenti.
 * 
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public interface Autorita extends Service {
	/** Indica istituzioni di tipo: tutte */
	public final static int ALL = 0;

	/** Indica istituzioni di tipo: MINISTERO */
	public final static int MINISTERO = 1;

	/** Indica istituzioni di tipo: MINISTERO */
	public final static int REGIONE = 2;

	/** Indica istituzioni di tipo: PROVINCIA */
	public final static int PROVINCIA = 3;

	/** Indica istituzioni di tipo: AUTHORITY */
	public final static int AUTHORITY = 4;

	/** Indica istituzione di livello: 0 */
	public final static int LIVELLO_0 = 0;

	/** Indica istituzione di livello: 1 */
	public final static int LIVELLO_1 = 1;

	/** Indica istituzione di livello: 2 */
	public final static int LIVELLO_2 = 2;

	/**
	 * Restituisce il path del registro delle autorita' settato nella
	 * configurazione del componente
	 * 
	 * @return Stringa contenente il path
	 */
	public String getDirRae();

	/**
	 * Ricarica il registro
	 */
	public void reloadRae();

	/**
	 * Restituisce un array di Stringhe contenente il nome delle Istituzioni di
	 * tipo <code>tipoIstituzione</code> valide alla data specificata da
	 * <code>data</code>
	 * 
	 * @param data data in formato Date
	 * @param tipoIstituzione puo' essere: Autorita.ALL; Autorita.MINISTERO;
	 *        Autorita.REGIONE; Autorita.PROVINCIA; Autorita.AUTHORITY
	 * @return array di Stringhe contenente il nome delle Istituzioni
	 */
	public String[] getIstituzioniValide(Date data, int tipoIstituzione);

	/**
	 * Restituisce un array di Stringhe contenente il nome delle Istituzioni di
	 * tipo <code>tipoIstituzione</code> valide alla data specificata da
	 * <code>data</code>
	 * 
	 * @param data data normalizzata yyyyMMdd in formato String
	 * @param tipoIstituzione puo' essere: Autorita.ALL; Autorita.MINISTERO;
	 *        Autorita.REGIONE; Autorita.PROVINCIA; Autorita.AUTHORITY
	 * @return array di Stringhe contenente il nome delle Istituzioni
	 */
	public String[] getIstituzioniValide(String data, int tipoIstituzione);

	/**
	 * Restituisce un array di Istituzione contenente tutte le Istituzioni
	 * valide alla data <code>data</code> che possono emettere i provvedimenti
	 * specificati dall'espressione regolare <code>RegExp</code>
	 * 
	 * @param data data in formato Date
	 * @param RegExp espressione regolare per le autorita' emittenti i
	 *        provvedimenti
	 * @return array di Istituzioni valide che soddisfano <code>RegExp</code>
	 */
	public Istituzione[] getIstituzioniValideFromProvvedimenti(Date data, String RegExp);

	/**
	 * Restituisce un array di Istituzione contenente tutte le Istituzioni
	 * valide alla data <code>data</code> che possono emettere i provvedimenti
	 * specificati dall'espressione regolare <code>RegExp</code>
	 * 
	 * @param data data normalizzata yyyyMMdd in formato String
	 * @param RegExp espressione regolare per le autorita' emittenti i
	 *        provvedimenti
	 * @return array di Istituzioni valide che soddisfano <code>RegExp</code>
	 */
	public Istituzione[] getIstituzioniValideFromProvvedimenti(String data, String RegExp);

	/**
	 * Restituisce true se l'istituzione specificata da
	 * <code>nomeIstituzione</code> &egrave; valida alla data <code>data</code>
	 * 
	 * @param data data in formato Date
	 * @param nomeIstituzione
	 * @return true, false
	 */
	public boolean isIstituzioneValida(Date data, String nomeIstituzione);

	/**
	 * Restituisce true se l'istituzione specificata da
	 * <code>nomeIstituzione</code> &egrave; valida alla data <code>data</code>
	 * 
	 * @param data data normalizzata yyyyMMdd in formato String
	 * @param nomeIstituzione
	 * @return true, false
	 */
	public boolean isIstituzioneValida(String data, String nomeIstituzione);

	/**
	 * Restituisce la Urn dell'Istituzione dato il nome; se non &egrave; presente
	 * restituisce null.
	 * 
	 * @param nomeIstituzione nome dell'Istituzione
	 * @return urn dell'Istituzione; null se non &egrave; presente
	 */
	public String getUrnIstituzioneFromNomeIstituzione(String nomeIstituzione);

	/**
	 * Restituisce il nome dell'Istituzione data la urn; se non &egrave; presente
	 * restituisce null;
	 * 
	 * @param urnIstituzione
	 * @return nome dell'istituzione; null se non &egrave; presente
	 */
	public String getNomeIstituzioneFromUrnIstituzione(String urnIstituzione);

	/**
	 * Restituisce true se la urn dell'istituzione &egrave; presente nel registro al
	 * livello <code>livello<code>
	 * @param urn urn da cercare nel registro
	 * @param livello livello a cui cercarla; pu&ograve; essere Autorita.LIVELLO_0; Autorita.LIVELLO_1; Autorita.LIVELLO_2
	 * @return true se la urn &egrave; presente nel registro
	 */
	public boolean isUrnInRegistro(String urn, int livello);

}
