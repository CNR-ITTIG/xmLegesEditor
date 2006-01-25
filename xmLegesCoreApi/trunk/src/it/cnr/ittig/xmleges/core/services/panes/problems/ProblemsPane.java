package it.cnr.ittig.xmleges.core.services.panes.problems;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.frame.Pane;

/**
 * Servizio per la visualizzazione del pannello dei problemi.
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
public interface ProblemsPane extends Service, Pane {

	/**
	 * Aggiunge il problema <code>problem</code> al pannello.
	 * 
	 * @param problem problema da aggiungere al pannello
	 */
	public void addProblem(Problem problem);

	/**
	 * Rimuove il problema <code>problem</code> dal pannello.
	 * 
	 * @param problem problema da rimuovere dal pannello
	 */
	public void removeProblem(Problem problem);
}
