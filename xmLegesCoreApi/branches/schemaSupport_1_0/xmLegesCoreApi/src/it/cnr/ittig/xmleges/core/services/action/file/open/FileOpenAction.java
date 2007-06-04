package it.cnr.ittig.xmleges.core.services.action.file.open;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per l'azione di apertura di un documento.
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
public interface FileOpenAction extends Service {

	/**
	 * Apre un documento visualizzando eventualmente una finestra di dialogo.
	 * 
	 * @return <code>true</code> se il documento &egrave; stato aperto
	 *         correttamente
	 */
	public boolean doOpen();

	/**
	 * Apre il documento <code>source</code>.
	 * 
	 * @param source sorgente da aprire
	 * @return <code>true</code> se il documento &egrave; stato aperto
	 *         correttamente
	 */
	public boolean doOpen(String source);

	/**
	 * Apre il documento <code>source</code>.
	 * 
	 * @param source sorgente da aprire
	 * @param addLast se <code>true</code> aggiunge il documento specificato
	 *        alla lista degli ultimi aperti.
	 * @return <code>true</code> se il documento &egrave; stato aperto
	 *         correttamente
	 */
	public boolean doOpen(String source, boolean addLast);

	/**
	 * Aggiunge il documento specificato alla lista degli ultimi aperti.
	 * 
	 * @param source sorgente da aggiungere
	 */
	public void addLast(String source);
}
