package it.cnr.ittig.xmleges.editor.services.form.rinvii.interni;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;

import org.w3c.dom.Node;

/**
 * Servizio per la visualizzazione della form per recuperare il nodo per i
 * rinvii interni.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface RinviiInterniForm extends Service {
	/**
	 * Apre la form per recuperare il nodo da riferire.
	 */
	public void openForm(FormClosedListener listener, boolean allowMRif);

	/**
	 * Indica se &egrave; stato premuto il tasto ok della form.
	 * 
	 * @return <code>true</code> se tasto ok premuto
	 */
	public boolean isOk();

	/**
	 * Restituisce l'id del nodo da riferire.
	 * 
	 * @return id del nodo
	 */
	public String getId();

	/**
	 * Restituisce la lista degli id di un mrif.
	 * 
	 * @return id delle partizioni da citare
	 */
	public String[] getMRif();

	/**
	 * Restituisce la forma testuale del riferimento.
	 * 
	 * @return forma testuale
	 */
	public String getTesto();

	/**
	 * Imposta la forma testuale del riferimento iniziale.
	 * 
	 * @param testo forma testuale
	 */
	public void setTesto(String testo);

	/**
	 * Imposta il nodo attivo da cui viene chiamata la form
	 * 
	 * @param node
	 */
	public void setCallingNode(Node node);

	/**
	 * Imposta l'aggiornamento automatico della forma testuale
	 * 
	 * @param auto <code>true</code> per l'aggiornamente automatico
	 */
	public void setAutoUpdate(boolean auto);

	/**
	 * Restituisce la descrizione testuale del riferimento multiplo
	 * 
	 * @return
	 */
	public String[] getDescrizioneMRifInt();

	/**
	 * Popola la form con gli elementi di un mrif
	 * 
	 * @param id
	 */
	public void setMrif(String[] id);

}
