package it.cnr.ittig.xmleges.editor.services.form.tabelle;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la visualizzazione della form per l'inserimento dei parametri
 * per la creazione di una tabella.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface TabelleForm extends Service {

	/**
	 * Apre la form per l'inserimento dei parametri per la creazione di una
	 * nuova tabella.
	 * 
	 * @return <code>true</code> se l'operazione &egrave; terminata con
	 *         successo
	 */
	public boolean openForm();

	/**
	 * Indica se la tabella deve avere il titolo.
	 * 
	 * @return <code>true</code> per avere il titolo
	 */
	public boolean hasCaption();

	/**
	 * Indica se la tabella deve avere l'intestazione.
	 * 
	 * @return <code>true</code> per avere il l'intestazione
	 */
	public boolean hasHead();

	/**
	 * Indica se la tabella deve avere il pi&egrave; di tabella.
	 * 
	 * @return <code>true</code> per avere il il pi&egrave; di tabella
	 */
	public boolean hasFoot();

	/**
	 * Numero di righe che deve avere la tabella.
	 * 
	 * @return numero di righe
	 */
	public int getRows();

	/**
	 * Numero di colonne che deve avere la tabella.
	 * 
	 * @return numero di colonne
	 */
	public int getCols();
}
