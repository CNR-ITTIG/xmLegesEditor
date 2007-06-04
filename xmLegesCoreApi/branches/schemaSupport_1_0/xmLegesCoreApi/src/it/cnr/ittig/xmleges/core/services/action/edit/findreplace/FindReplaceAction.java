package it.cnr.ittig.xmleges.core.services.action.edit.findreplace;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per le azioni di find e replace sul testo.
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
 * @version 1.0
 */
public interface FindReplaceAction extends Service {

	/**
	 * Cerca il testo nel documento corrente.
	 */
	public void doFind();

	/**
	 * Cerca l'occorrenza successiva del testo precedentemente cercato.
	 */
	public void doFindNext();

	/**
	 * Sostituisce porzioni di testo.
	 */
	public void doReplace();
}
