package it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive;

import org.w3c.dom.Node;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;

/**
 * Servizio per la visualizzazione della form per la gestione del novellando
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
 */
public interface NovellandoForm extends Service {
	/**
	 * Apre la form per segnare il Novellando
	 */
	public void openForm(FormClosedListener listener, Node meta);

	public void setMeta(Node meta, String tipoPartizione);
}
