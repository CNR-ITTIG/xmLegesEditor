package it.cnr.ittig.xmleges.core.services.spellcheck.dom;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheck;

import org.w3c.dom.Node;

/**
 * Servizio per la gestione del controllo ortografico del testo di un documento
 * DOM.
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
public interface DomSpellCheck extends Service {

	/**
	 * Restituisce un oggetto di tipo SpellCheck
	 */
	public SpellCheck getSpellCheck();

	/**
	 * Esegue il controllo ortografico sul Node <code>node</code>.
	 * 
	 * @param node nodo sul quale effettuare il controllo ortografico
	 * @return elenco delle parole con errore
	 */
	public DomSpellCheckWord[] spellCheck(Node node);

	/**
	 * Esegue il controllo ortografico sul nodo testo <code>node</code>.
	 * 
	 * @param node
	 * @param start
	 * @param end
	 * @return
	 */
	public DomSpellCheckWord[] spellCheck(Node node, int start, int end);

	/**
	 * Abilita il controllo ortografico automatico su variazioni del documento.
	 * 
	 * @param enabled <code>true</code> per abilitare il controllo ortografico
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Indica se il controllo ortografico automatico &egrave; attivato.
	 * 
	 * @return <code>true</code> se &egrave; attivo
	 */
	public boolean isEnabled();

}
