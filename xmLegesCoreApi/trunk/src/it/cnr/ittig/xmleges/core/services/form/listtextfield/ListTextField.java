package it.cnr.ittig.xmleges.core.services.form.listtextfield;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.CommonForm;

import java.util.Vector;

/**
 * Servizio per l'inserimento di elementi in una lista utilizzando come editor
 * un pannello specificato dall'interfaccia <code>ListEditor</code>.<br>
 * La lista visualizza il testo degli oggetti usando il metodo
 * <code>Object.toString()</code>.
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

public interface ListTextField extends Service, CommonForm {

	/**
	 * Imposta il componente per la modifica degli elementi della lista.
	 * 
	 * @param listEditor editor degli elementi della lista
	 */
	public void setEditor(ListTextFieldEditor listEditor);

	/**
	 * Imposta gli elementi iniziali della lista.
	 * 
	 * @param elem elementi iniziali
	 */
	public void setListElements(Vector elem);

	/**
	 * Aggiunge elementi alla lista.
	 * 
	 * @param elem elementi da aggiungere
	 */
	public void addListElements(Vector elem);

	/**
	 * Restituisce gli elementi della lista.
	 * 
	 * @return elementi della lista
	 */
	public Vector getListElements();

	/**
	 * Aggiunge un listener alla lista dei listener registrati.
	 * 
	 * @param listener listener da aggiungere
	 */
	public void addListTextFieldElementListener(ListTextFieldElementListener listener);

	/**
	 * Rimuove un listener alla lista dei listener registrati.
	 * 
	 * @param listener listener da rimuovare
	 */
	public void removeListTextFieldElementListener(ListTextFieldElementListener listener);

	/**
	 * Imposta la propriet? moveButtons, che determina se il componente deve
	 * visualizzare i pulsanti per spostare in alto o in basso un elemento.
	 * 
	 * @param moveButtons true per mostrare i pulsanti, false per nasconderli
	 */
	public void setMoveButtons(boolean moveButtons);

	/**
	 * Restituisce l'elemento selezionato.
	 * 
	 * @return restituisce l'oggetto selezionato o <code>null</code>
	 */
	public Object getSelectedItem();
}
