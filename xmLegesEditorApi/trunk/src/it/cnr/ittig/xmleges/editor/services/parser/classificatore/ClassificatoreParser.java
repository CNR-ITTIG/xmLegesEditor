package it.cnr.ittig.xmleges.editor.services.parser.classificatore;

import it.cnr.ittig.services.manager.Service;

import java.io.InputStream;

import org.w3c.dom.Node;

/**
 * Servizio per avviare il classificatore delle disposizioni.
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
public interface ClassificatoreParser extends Service {

	/**
	 * Classifica le disposizioni contenute nel sottoalbero indentificato da
	 * <code>node</code>.
	 * 
	 * @param node sottoalbero d&agrave; classificare
	 * @return risultato della classificazione
	 */
	public InputStream parse(Node node);
}
