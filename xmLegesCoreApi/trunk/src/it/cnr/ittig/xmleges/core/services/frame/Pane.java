package it.cnr.ittig.xmleges.core.services.frame;

import java.awt.Component;

/**
 * Interfaccia che deve essere usata per aggiungere un pannello nel frame.
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
 */
public interface Pane {

	/**
	 * Restituisce il nome del pannello.
	 * 
	 * @return nome del pannello
	 */
	public String getName();

	/**
	 * Restituisce il componente grafico per visualizzare il pannello.
	 * 
	 * @return componente contenente il pannello
	 */
	public Component getPaneAsComponent();

	/**
	 * Informa se il componente pu&ograve; eseguire il taglio di una porzione di
	 * testo per la successiva operazione incolla.
	 * 
	 * @return <code>true</code> se &egrave; possibile "tagliare"
	 */
	public boolean canCut();

	/**
	 * Taglia il testo e lo copia nella clipboard.
	 * 
	 * @throws PaneException se avviene un errore durante l'operatione
	 */
	public void cut() throws PaneException;

	/**
	 * Informa se il componente pu&ograve; eseguire la copia di una porzione di
	 * testo per la successiva operazione incolla.
	 * 
	 * @return <code>true</code> se &egrave; possibile "copiare"
	 */
	public boolean canCopy();

	/**
	 * Copia il testo nella clipboard.
	 * 
	 * @throws PaneException se avviene un errore durante l'operatione
	 */
	public void copy() throws PaneException;

	/**
	 * Informa se il componente pu&ograve; incollare la porzione di testo
	 * precedentemente copiato o tagliato.
	 * 
	 * @return <code>true</code> se &egrave; possibile "incollare"
	 */
	public boolean canPaste();

	/**
	 * Incolla il testo.
	 * 
	 * @throws PaneException se avviene un errore durante l'operatione
	 */
	public void paste() throws PaneException;

	/**
	 * Informa se il componente pu&ograve; incollare la porzione di testo
	 * precedentemente copiato o tagliato, trattando tale testo esclusivamente
	 * come testo piatto.
	 * 
	 * @return <code>true</code> se &egrave; possibile "incollare"
	 */
	public boolean canPasteAsText();

	/**
	 * Incolla il testo come testo piatto.
	 * 
	 * @throws PaneException se avviene un errore durante l'operatione
	 */
	public void pasteAsText() throws PaneException;

	/**
	 * Informa se il componente pu&ograve; cancellare la porzione di testo
	 * direttamente.
	 * 
	 * @return <code>true</code> se &egrave; possibile cancellare il testo
	 */
	public boolean canDelete();

	/**
	 * Cancella direttamente il testo selezionato.
	 * 
	 * @throws PaneException se avviene un errore durante l'operazione
	 */
	public void delete() throws PaneException;

	/**
	 * Informa se il pannello pu&ograve; essere stampato.
	 * 
	 * @return true se pu&ograve; essere stampato
	 */
	public boolean canPrint();

	/**
	 * Restituisce il componente che deve essere stampato se
	 * <code>canPrint</code>, restituisce <code>true</code>. Il componente
	 * restituito da questo metodo pu&ograve; essere diverso da quello
	 * restituito da <code>getPaneAsComponent</code>.
	 * 
	 * @return componente da stampare
	 */
	public Component getComponentToPrint();

	/**
	 * Informa se il pannello p&ograve; effettuare la ricerca di una stringa nel
	 * testo.
	 * 
	 * @return <code>true</code> se p&ograve; effettuare la ricerca
	 */
	public boolean canFind();

	/**
	 * Restituisce il gestore della ricerca del testo per il pannello.
	 * 
	 * @return gestore della ricerca
	 */
	public FindIterator getFindIterator();

	/**
	 * Forza l'aggornamento del pannello.
	 */
	public void reload();
}
