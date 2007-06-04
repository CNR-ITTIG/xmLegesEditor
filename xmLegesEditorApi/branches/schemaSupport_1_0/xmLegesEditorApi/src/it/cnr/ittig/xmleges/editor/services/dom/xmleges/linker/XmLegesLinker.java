package it.cnr.ittig.xmleges.editor.services.dom.xmleges.linker;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per l'aggiornamento del documento in funzione del parser dei
 * riferimenti (XmLegesLinker). 
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public interface XmLegesLinker extends Service {

	/**
	 * Funzione per l'inserimento nel Dom del Documento dei risultati del parser
	 * dei riferimenti applicato ad un testo selezionato
	 * 
	 * @param nodeSel
	 * @param parsedText
	 * @param testoPrima
	 * @param testoDopo
	 * @return <code>null</code> se l'operazione non &egrave; andata a buon
	 *         fine; nodo modificato
	 */
	public Node setParsedText(Node nodeSel, String parsedText, String testoPrima, String testoDopo);

	/**
	 * Funzione per l'inserimento nel Dom del Documento dei risultati del parser
	 * dei riferimenti applicato all'intero documento
	 * 
	 * @param parsedText
	 * @return <code>null</code> se l'operazione non &egrave; andata a buon
	 *         fine; nodo modificato
	 */
	public Node setParsedDocument(String parsedText);

	/**
	 * @param node
	 * @param textRis
	 * @return <code>null</code> se l'operazione non &egrave; andata a buon
	 *         fine; nodo modificato
	 */
	public Node setParsedNode(Node node, String textRis);

	/**
	 * Funzione per l'inserimento nel Dom del Documento dei risultati del parser
	 * dei riferimenti applicato ad una selezione di nodi
	 * 
	 * @param nodes
	 * @param textRis
	 * @return <code>null</code> se l'operazione non &egrave; andata a buon
	 *         fine; nodo modificato
	 */
	public Node setParsedNodes(Node[] nodes, String[] textRis);

}
