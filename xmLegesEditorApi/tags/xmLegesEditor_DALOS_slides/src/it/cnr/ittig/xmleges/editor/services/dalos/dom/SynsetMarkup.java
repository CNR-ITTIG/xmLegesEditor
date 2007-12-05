package it.cnr.ittig.xmleges.editor.services.dalos.dom;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

import org.w3c.dom.Node;

/**
 * Servizio per l'assegnazione di 
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface SynsetMarkup extends Service {
	
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public boolean canSetSynset(Node node);
	
	

	/**
	 * 
	 * @param node
	 * @param start
	 * @param end
	 * @param synset
	 * @return
	 */
	public Node setSynset(Node node, int start, int end, Synset synset);
	
	
	/**
	 * 
	 * @param node
	 * @param start
	 * @param end
	 * @param synset
	 * @param variant
	 * @return
	 */
	public Node setSynset(Node node, int start, int end, Synset synset, String variant);
	

}
