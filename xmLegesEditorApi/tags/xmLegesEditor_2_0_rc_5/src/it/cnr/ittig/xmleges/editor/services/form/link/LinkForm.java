package it.cnr.ittig.xmleges.editor.services.form.link;

import it.cnr.ittig.services.manager.Service;
import java.net.URL;

import org.w3c.dom.Node;

/**
 * Servizio per ...
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
 * @author <a href="mailto:tommaso.agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface LinkForm extends Service {

	/**
	 * Apre la form pe gestire i Link ad ipertesti
	 * 
	 * @param 
	 * @return <code>true</code> se la form &egrave; valida
	 */
	public boolean openForm(Node node, String testo, String url);
}
