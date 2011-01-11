package it.cnr.ittig.xmleges.editor.services.dom.ndr;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio inserire le note del redattore nell'albero DOM del documento.
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
public interface Ndr extends Service {

	
    /**
     * 
     * @param node
     * @param start
     * @param end
     * @param id
     * @param testo
     * @return nodo modificato; null se l'operazione non &egrave andata a buon
	 *         fine
     */
	public Node setNdr(Node node, int start, int end, String id, String testo);

	/**
	 * @param node
	 * @return <code>true</code> se &egrave; possibile inserire una nota nella posizione specificata
 	 */
	public boolean canSetNdr(Node node);

	/**
	 * @return array di note
	 */
	public Nota[] getNotesFromDocument();
	
	public void fixNDRFromDoc();

}
