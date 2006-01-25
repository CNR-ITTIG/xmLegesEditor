package it.cnr.ittig.xmleges.core.services.panes.problems;

import org.w3c.dom.Node;

/**
 * Interfaccia che deve essere usata per aggiungere un problema la pannello dei
 * problemi.
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
public interface Problem {

	public final static int FATAL_ERROR = 1;

	public final static int ERROR = 2;

	public final static int WARNING = 3;

	public final static int INFO = 4;

	/**
	 * Indica il tipo di problema. Il valore restituito deve essere
	 * <code>ERROR</code>, <code>WARNING</code> o <code>INFO</code>.
	 * 
	 * @return tipo di problema
	 */
	public int getType();

	/**
	 * Restituisce il testo del problema.
	 * 
	 * @return testo del problema
	 */
	public String getText();

	/**
	 * Indica se il problema pu&ograve; essere rimosso dal pannello direttamente
	 * dall'utente.
	 * 
	 * @return true se pu&ograve; essere rimosso dall'utente
	 */
	public boolean canRemoveByUser();

	/**
	 * Restituisce il nodo associato all'errore o <code>null</code>.
	 * 
	 * @return nodo associato all'errore o <code>null</code>
	 */
	public Node getNode();

	/**
	 * Indica se il problema pu&ograve; essere risolto automaticamente.
	 * 
	 * @return <code>true</code> se il problema pu&ograve; essere risolto
	 *         automaticamente
	 */
	public boolean canResolveProblem();

	/**
	 * Attiva la risoluzione del problema. Se il metodo restituisce
	 * <code>true</code>, il problema verr&agrave; rimosso dall'elenco.
	 * 
	 * @return true se il problema &egrave; stato risolto e deve essere rimosso
	 *         dalla lista
	 */
	public boolean resolveProblem();
}
