package it.cnr.ittig.xmleges.editor.services.dom.disposizioni;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;

import org.w3c.dom.Node;

/**
 * Servizio per l'assegnazione di un intervallo di vigenza ad una porzione di
 * testo o ad una partizione e la creazione delle disposizioni nei metadati. 
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
public interface Disposizioni extends Service {
		
	//Disposizioni ATTIVE
	/**
	 * Funzione per l'aggiornamento dei metadati di disposizione ATTIVE (esclusa novella/novellando)
	 * 
	 * @return la DSP che sto aggiungendo
	 */
	public Node setDOMDispAttive(Node metaDaModificare, String idMod, int operazioneIniziale, String completa, boolean condizione, String decorrenza, String idevento, String norma, String partizione, String[] delimitatori);
	
	/**
	 * Funzione per l'aggiornamento dei metadati di disposizione ATTIVE (Novella)
	 * 
	 * @return la DSP che sto aggiungendo
	 */
	public void setDOMNovellaDispAttive(Node meta, String virgolettaContenuto, String tipo, String posizione, String virgolettaA, String virgolettaB);
	
	/**
	 * Funzione per l'aggiornamento dei metadati di disposizione ATTIVE (Novellando)
	 * 
	 * @return la DSP che sto aggiungendo
	 */
	public void setDOMNovellandoDispAttive(Node meta, boolean parole, String tipoPartizione, String tipo, String ruoloA, String virgolettaA, String ruoloB, String virgolettaB);
	
	
	//Disposizioni PASSIVE
	/**
	 * Funzione per l'aggiornamento dei metadati di disposizione PASSIVE e
	 * per l'inserimento dei metadati proprietari (creazione della nota)
	 * 
	 * @return </code>True</code> operazione correttamente eseguita
	 */
	public boolean setDOMDisposizioni(String pos, String Norma, String partizione, String Novellando, String Novella, String preNota, String autoNota, String postNota, boolean implicita, Evento eventoOriginale, Evento eventoVigore);
	
	/**
	 * Funzione per l'inserimento di una nuova partizione
	 * 
	 * @param node nodo fratello della nuova partizione da creare
	 * @param prima se la nuova partizione va inserita come fratello precedente
	 * @return </code>Node</code> la nuova partizione inserita
	 */
	public Node makePartition(Node node, boolean prima, VigenzaEntity vigenza);
	
	/**
	 * Funzione per l'inserimento di un nuovo span
	 * 
	 * @param node nodo in cui inserire lo span
	 * @param posizione (se positivo) la posizione in cui va inserito lo span nel nodo (altrimenti) inserito come fratello destro
	 * @param vigenza la vigenza che assumer� lo span
	 * @param testo dello span
	 * @return </code>Node</code> la nuova partizione inserita
	 */
	public Node makeSpan(Node node, int posizione, VigenzaEntity vigenza, String testo);
	
	/**
	 * Funzione per l'inserimento della nota ndr (per la vigenza)
	*/
	public void makeNotaVigenza(Node node);
		
	/**
	 * Funzione Dom per l'assegnazione di un intervallo di vigenza ad una
	 * porzione di testo
	 * 
	 * @param node nodo sul cui testo si applica la vigenza
	 * @param selectedText testo selezionato
	 * @param start inizio selezione del testo
	 * @param end fine selezione del testo
	 * @param vigenza oggetto contenente la vigenza
	 * @return </code>true</code> se la vigenza e' stata inserita
	 *         correttamente
	 */
	public Node setVigenza(Node node, String selectedText, int start, int end, VigenzaEntity vigenza);
	
	/**
	 * Funzione Dom per la lettura da documento della vigenza del nodo selezionato
	 * @param node nodo selezionato
	 * @param start inizio selezione
	 * @param end fine selezione
	 * @return
	 */
	public VigenzaEntity getVigenza(Node node, int start, int end);
		
	/**
	 * Funzione per verificare se il documento ha almeno una vigenza
	 * @return 
	 */
	public boolean isVigente();
	/**
	 * Funzione che cambia il tipo di documento in funzione delle vigenze presenti
	 *
	 */
	public void setTipoDocVigenza();
	
	/**
	 * Undo ripristinando una vecchia vigenza
	 */
	public void doUndo(String id, String iniziovigore, String finevigore, String status);	
	
	/**
	 * Undo senza vigenza, ripristino testo opzionale
	 */
	public void doUndo(String id, boolean cancellaTesto);	

	/**
	 * Elimina vigenza
	 */
	public Node doErase(String idNovellando, String idNovella, Node disposizione, Node novellando);
	
	/**
	 * Modifica vigenza
	 */
	public void doChange(String norma, String pos, Node disposizione, String autonota, boolean implicita, Node novellando, String status, String idEvento, String idNovella);
}
