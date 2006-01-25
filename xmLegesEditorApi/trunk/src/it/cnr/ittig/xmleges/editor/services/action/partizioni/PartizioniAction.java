package it.cnr.ittig.xmleges.editor.services.action.partizioni;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per l'inserimento di nuove partizioni nell'articolato di un
 * documento NIR. <br>
 * L'implementazione dovr&avrage; preoccuparsi di creare, oltre al tag
 * richiesto, tutti quelli opportuni per mantenere il documento valido.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface PartizioniAction extends Service {

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;libro&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 */
	public void doNewLibro(Node node);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;libro&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 * @param int action azione di inserimento ammessa
	 */
	public void doNewLibro(Node node, int action);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;parte&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 */
	public void doNewParte(Node node);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;parte&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 * @param int action azione di inserimento ammessa
	 */
	public void doNewParte(Node node, int action);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;titolo&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 */
	public void doNewTitolo(Node node);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;titolo&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 * @param int action azione di inserimento ammessa
	 */
	public void doNewTitolo(Node node, int action);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;capo&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 */
	public void doNewCapo(Node node);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;capo&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 * @param int action azione di inserimento ammessa
	 */
	public void doNewCapo(Node node, int action);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;sezione&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 */
	public void doNewSezione(Node node);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;sezione&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 * @param int action azione di inserimento ammessa
	 */
	public void doNewSezione(Node node, int action);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;articolo&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 */
	public void doNewArticolo(Node node);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;articolo&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 * @param int action azione di inserimento ammessa
	 */
	public void doNewArticolo(Node node, int action);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;comma&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 */
	public void doNewComma(Node node);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;comma&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 * @param int action azione di inserimento ammessa
	 */
	public void doNewComma(Node node, int action);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;el&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 */
	public void doNewLettera(Node node);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;el&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 * @param int action azione di inserimento ammessa
	 */
	public void doNewLettera(Node node, int action);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;en&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 */
	public void doNewNumero(Node node);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;en&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 * @param int action azione di inserimento ammessa
	 */
	public void doNewNumero(Node node, int action);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;rubrica&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 */
	public void doNewRubrica(Node node);

	/**
	 * Inserisce un nuovo tag contenitore <code>&lt;rubrica&gt;</code>.
	 * 
	 * @param node nodo attivo sul quale attivare l'inserimento
	 * @param int action azione di inserimento ammessa
	 */
	public void doNewRubrica(Node node, int action);
}
