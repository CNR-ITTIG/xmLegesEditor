package it.cnr.ittig.xmleges.editor.services.action.revisioni;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la gestione delle azioni per l'iter dei disegni di legge.
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface RevisioniAction extends Service {

	/**
	 * gestione dell'azione per l'inserimento di modifiche
	 */
	public void doInserisci();

	/**
	 * gestione dell'azione per la soppressione di parti del documento
	 */
	public void doSopprimi();

	/**
	 * gestione dell'azione per la visualizzazione del testo a fronte sul
	 * browser
	 */
	public void doTestoaFronte();

	/**
	 * gestione dell'azione per l'estrazione del testo approvato e il passaggio
	 * ad un nuovo documento
	 */
	public void doPassaggio();

}
