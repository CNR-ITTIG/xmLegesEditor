package it.cnr.ittig.xmleges.core.services.document;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Servizio per la gestione del documento. Le modifiche sono gestite in modo
 * analogo alle transazioni dei database; occorre quindi che l'oggetto che
 * desidera apportare dei cambiamenti in un nodo inoltri la richiesta tramite il
 * metodo <code>beginEdit</code> e la termini con <code>commitEdit</code>
 * oppure la annulli tramite <code>rollbackEdit</code><br>
 * Esempio:
 * 
 * <pre>
 *                ...
 *                public void myMethod() {
 *                    ...
 *                    try {
 *                        EditTransaction editId = documentManager.beginEdit();
 *                        ... // change node or subtree
 *                        documentManager.commitEdit(editId);
 *                        ...
 *                    } catch (DocumentManagerException ex) {
 *                        // manage exception
 *                    }
 *                }
 *                ...
 * </pre>
 * 
 * L'implementazione notificher&agrave; la modifica dei nodi del documento
 * tramite l'EventManager. Gli eventi sono i seguenti:
 * <ul>
 * <li>DocumentChangedEvent</li>
 * <li>NodeChangedEvent</li>
 * <li>TextNodeChangedEvent</li>
 * <li>AttributeNodeChangedEvent</li>
 * </ul>
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
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @see it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface DocumentManager extends Service {

	/**
	 * Restituisce la sorgente usata per recuperare il documento.
	 * 
	 * @return sorgente del documento
	 */
	public String getSourceName();

	/**
	 * Imposta la sorgente usata per recuperare il documento.
	 * 
	 * @param source sorgente del documento
	 */
	public void setSourceName(String source);
	
	/**
	 * 
	 * @param doc
	 * @param isNew
	 * @return
	 */
	public boolean setDoc(Document doc, boolean isNew);
	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public Document open(String filename);

	/**
	 * Imposta la sorgente del documento e legge il documento. Questo metodo
	 * salva, se presente, il nome della codifica dei caratteri della sorgente.
	 * Questa versione del metodo NON modifica lo stato del documento.
	 * L'implementazione emetter&agrave; l'evento
	 * <code>DocumentOpenedEvent</code> se la sorgente &egrave; stata aperta
	 * correttamente.
	 * 
	 * @param source sorgente
	 * @return <code>true</code> se la sorgente &egrave; stata aperta
	 *         correttamente
	 */
	public boolean openSource(String source);

	/**
	 * Imposta la sorgente del documento e legge il documento. Questo metodo
	 * salva, se presente, il nome della codifica dei caratteri della sorgente.
	 * L'implementazione emetter&agrave; l'evento
	 * <code>DocumentOpenedEvent</code> se la sorgente &egrave; stata aperta
	 * correttamente.
	 * 
	 * @param source sorgente
	 * @param isNew imposta lo stato documento nuovo a true o false
	 * @return <code>true</code> se la sorgente &egrave; stata aperta
	 *         correttamente
	 */
	public boolean openSource(String source, boolean isNew);

	/**
	 * Indica se il documento aperto contiene errori emessi dal parser XML. Tali
	 * errori possono essere recuperati tramite il metodo
	 * <code>getErrors()</code>.
	 * 
	 * @return <code>true</code> se il documento &egrave; valido
	 */
	public boolean hasErrors();

	/**
	 * Restituisce gli errori dell'apertura del documento.
	 * 
	 * @return errori dell'apertura del documento
	 */
	public String[] getErrors();
	
	/**
	 * 
	 */
	public void clearErrors();
	
	/**
	 * Chiude il documento corrente. L'implementazione emetter&agrave; l'evento
	 * <code>DocumentClosedEvent</code>.
	 */
	public void close();

	/**
	 * Restituisce il nome della codifica dei caratteri specificato al momento
	 * della lettura della sorgente oppure tramite il metodo
	 * <code>setEncoding</code>. Se la sorgente non specifica tale nome e non
	 * &egrave; stato impostatato, si assume che sia <code>UTF-8</code>,
	 * quindi il metodo restituir&agrave; la stringa <code>"UTF-8"</code>.
	 * 
	 * @return nome della codifica dei caratteri
	 */
	public String getEncoding();

	/**
	 * Restituisce il nome del DTD o XSD definito nel documento xml. Il nome
	 * restituito non comprende nessun percorso.
	 * 
	 * @return nome del DTD o XSD
	 */
	public String getGrammarName();
	
	
	/**
	 * Restituisce il nome del DTD o XSD definito in <code>doc</code> Il nome
	 * restituito non comprende nessun percorso.
	 * 
	 * @param doc
	 * @return
	 */
	public String getGrammarName(Document doc);

	/**
	 * Restituisce l'intero documento come nodo DOM.
	 * 
	 * @return documento
	 * @see org.w3c.dom.Document
	 */
	public Document getDocumentAsDom();

	/**
	 * Restituisce la radice dell'albero DOM contenente il documento.
	 * 
	 * @return radice del documento DOM
	 * @see org.w3c.dom.Node
	 */
	public Node getRootElement();

	/**
	 * Indica se il documento &egrave; vuoto.
	 * 
	 * @return <code>true</code> se vuoto
	 */
	public boolean isEmpty();

	/**
	 * Imposta lo stato del documento come non modificato. Questo metodo
	 * pu&ograve; essere usato quando ad esempio viene effettuata l'operazione
	 * di salvataggio. Usare con cautela.
	 * 
	 * @param changed
	 */
	public void setChanged(boolean changed);

	/**
	 * Indica se il documento &egrave; stato cambiato.
	 * 
	 * @return <code>true</code> se &egrave; stato cambiato
	 */
	public boolean isChanged();

	/**
	 * Indica se il documento &egrave; nuovo e necessita del salvataggio con
	 * nome.
	 * 
	 * @return <code>true</code> se &egrave; nuovo
	 */
	public boolean isNew();

	/**
	 * Imposta lo stato del documento a nuovo
	 * 
	 * @param isNew <code>true</code> se &egrave; nuovo
	 */
	public void setNew(boolean isNew);

	/**
	 * Indica se &egrave; possibile effettuare l'operazione di ripristino dello
	 * stato del documento prima dell'ultima modifica.
	 * 
	 * @return <code>true</code> se &egrave; possibile ripristinare lo stato
	 */
	public boolean canUndo();

	/**
	 * Ripristina lo stato del documento prima dell'ultima modifica.
	 */
	public void undo();

	/**
	 * Indica se &egrave; possibile ripristinare l'ultima modifica annullata dal
	 * metodo <code>undo()</code>.
	 * 
	 * @return <code>true</code> se &egrave; possibile ripristinare la
	 *         modifica
	 */
	public boolean canRedo();

	/**
	 * Ripristina la modifica annullata dal metodo <code>redo()</code>.
	 */
	public void redo();

	/**
	 * Restituisce il numero massimo di undo.
	 * 
	 * @return numero massimo di undo
	 */
	public int getUndoSize();

	/**
	 * Permette di iniziare la modifica sul nodo <code>node</code> e
	 * restituisce l'identificativo univoco della transazione.
	 * 
	 * @return identificativo univoco della transazione
	 * @throws DocumentManagerException se il documento non &egrave accessibile
	 */
	public EditTransaction beginEdit() throws DocumentManagerException;

	/**
	 * Permette di iniziare la modifica sul nodo <code>node</code> e
	 * restituisce l'identificativo univoco della transazione. <br>
	 * Il parametro <code>source</code> permette, se diverso da
	 * <code>null</code>, di emettere l'evento avente come sorgente
	 * <code>source</code> stesso anzich/&egrave; l'implementazione del
	 * servizio <code>DocumentManager</code>.
	 * 
	 * @param source sorgente per l'evento
	 * @return identificativo univoco della transazione
	 * @throws DocumentManagerException se il documento non &egrave accessibile
	 */
	public EditTransaction beginEdit(Object source) throws DocumentManagerException;

	/**
	 * Permette di terminare la fase di modifica della transazione identificata
	 * da <code>transaction</code> ottenuto dal metodo <code>beginEdit</code>.
	 * 
	 * @param transaction transazione
	 * @throws DocumentManagerException se la modifica non &agrave; applicabile
	 */
	public void commitEdit(EditTransaction transaction) throws DocumentManagerException;

	/**
	 * Annulla l'operazione di modifica iniziata con <code>beginEdit</code>.
	 * 
	 * @param transaction transazione
	 */
	public void rollbackEdit(EditTransaction transaction);

	/**
	 * Aggiunge un'azione da avviare prima dell'inizializzazione del sistema di
	 * UNDO.
	 * 
	 * @param action azione da aggiungere
	 */
	public void addBeforeInitUndoAction(DocumentBeforeInitUndoAction action);

	/**
	 * Rimuove l'azione da avviare prima dell'inizializzazione del sistema di
	 * UNDO.
	 * 
	 * @param action azione da rimuovere
	 */
	public void removeBeforeInitUndoAction(DocumentBeforeInitUndoAction action);
	

}
