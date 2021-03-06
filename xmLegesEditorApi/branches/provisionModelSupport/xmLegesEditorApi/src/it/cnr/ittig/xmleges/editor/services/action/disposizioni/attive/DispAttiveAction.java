package it.cnr.ittig.xmleges.editor.services.action.disposizioni.attive;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per l'attivazione della gestione delle disposizioni attive.
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
 */
public interface DispAttiveAction extends Service {

	/**
	 * Gestione delle disposizioni attive
	 *
	 */
	public void doDispAttiva();


}
