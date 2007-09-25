package it.cnr.ittig.xmleges.editor.services.action.liste;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la gestione delle liste non ordinate, ordinate e puntate html.
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
public interface ListeAction extends Service {

	public void doLista(String tipolista);

	public void doCambiaLivello(boolean inc);
}
