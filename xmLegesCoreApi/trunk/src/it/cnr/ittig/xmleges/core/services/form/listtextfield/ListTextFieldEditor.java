package it.cnr.ittig.xmleges.core.services.form.listtextfield;

import java.awt.Component;
import java.awt.Dimension;

/**
 * Interfaccia necessaria per definire l'editor degli elementi della lista per
 * <code>ListTextField</code>.
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
public interface ListTextFieldEditor {

	/**
	 * Restituisce il componente grafico da visualizzare come editor.
	 * 
	 * @return componente grafico da visualizzare
	 */
	public Component getAsComponent();

	/**
	 * Restituisce l'oggetto da inserire nella lista.
	 * 
	 * @return oggetto da inserire nella lista, o null per non inserire nessun
	 *         oggetto.
	 */
	public Object getElement();

	/**
	 * Imposta l'oggetto selezionato nella lista che deve essere presentato nei
	 * campi di modifica.
	 * 
	 * @param object
	 */
	public void setElement(Object object);

	/**
	 * Richiede all'editor di pulire i campi
	 */
	public void clearFields();

	/**
	 * Richiede all'editor di controllare che i dati inseriti siano corretti. In
	 * caso di errore, deve essere possibile chiamare getErrorMessage per
	 * presentare il messaggio di errore all'utente.
	 * 
	 * @return true se i dati inseriti sono corretti, false se sono errati
	 */
	public boolean checkData();

	/**
	 * Richiede all'editor di pulire i campi
	 */
	public String getErrorMessage();

	/**
	 * Richiede all'editor la dimensione desiderata per la form
	 * 
	 * @return dimensione per la form, o null per non impostare la dimensione.
	 */
	public Dimension getPreferredSize();
}
