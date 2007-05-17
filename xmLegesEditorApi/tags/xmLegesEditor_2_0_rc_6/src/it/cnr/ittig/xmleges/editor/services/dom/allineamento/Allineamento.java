package it.cnr.ittig.xmleges.editor.services.dom.allineamento;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per la gestione dell'allineamento del testo.
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
public interface Allineamento extends Service {

	/**
	 * Indica se &egrave; possibile allineare il testo del nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo
	 * @return <code>true</code> se &egrave; possibile allineare il testo
	 */
	public boolean canAlignText(Node node);

	/**
	 * Allinea il testo del nodo <code>pos</code>.
	 * 
	 * @param pos nodo
	 * @param allinea tipo di allineamento
	 */
	public void alignText(Node pos, String allinea);

}
