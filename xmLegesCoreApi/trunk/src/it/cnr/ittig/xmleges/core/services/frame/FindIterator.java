package it.cnr.ittig.xmleges.core.services.frame;

/**
 * Interfaccia per realizzare la ricerca e la sostituzione di testo nei
 * pannelli. <br>
 * La ricerca &egrave; inizializzata tramite il metodo
 * <code>initFind(String)</code>. Per cercare la successiva occorrenza
 * occorre invocare il metodo next() che restituisce <code>false</code> se non
 * &egrave; stata trovata oppure &egrave; stata raggiunta la fine del documento.
 * La sostituzione dell'ultima occorrenza &egrave; effettuata dal metodo
 * <code>replace(String)</code>.<br>
 * Esempio:
 * 
 * <pre>
 *           ...
 *           if (myPane.canFind()) {
 *           	FindIterator it = myPane.getFindIterator();
 *              it.initFind(&quot;foo&quot;);
 *              while (it.next())
 *              	if (it.canReplace())
 *              		it.replace(&quot;off&quot;);
 *           }
 *           ...
 * </pre>
 * 
 * Sostituisce tutte le occorrenze di "foo" con "off".
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
 * @version 1.1
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface FindIterator {

	/**
	 * Inizializza l'operazione di ricerca del testo.
	 * 
	 * @param text testo da cercare
	 */
	public void initFind(String text);

	/**
	 * Imposta la ricerca senza considerare la differenza tra lettere maiuscole
	 * e minuscole.
	 * 
	 * @param caseSensitive <code>true</code> se deve essere considerata la
	 *        differenza
	 */
	public void setCaseSensitive(boolean caseSensitive);

	/**
	 * Cerca la successiva occorrenza del testo <code>text</code>. Questo
	 * metodo restituisce false se la stringa non &egrave; stata trovata o se
	 * 
	 * @return <code>true</code> se &egrave; stato trovato
	 */
	public boolean next();

	/**
	 * Indica se pu&ograve; effettuare la sostituzione dell'ultima occorrenza
	 * trovata con il testo <code>text</code>.
	 * 
	 * @param text testo che si desidera sostituire
	 * @return <code>true</code> se pu&ograve; effettuare la sostituzione
	 */
	public boolean canReplace(String text);

	/**
	 * Sostituisce con il testo <code>text</code> l'ultima occorrenza trovata
	 * con il metodo <code>next</code>.
	 * 
	 * @param text testo da sostituire
	 */
	public void replace(String text);

}
