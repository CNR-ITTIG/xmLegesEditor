package it.cnr.ittig.xmleges.core.services.form.listtextfield;

import java.util.EventListener;

/**
 * Listener per gli eventi emessi da ListTextField.
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
 * @author <a href="mailto:t.paba@onetech.it">Tommaso Paba</a>
 */
public interface ListTextFieldElementListener extends EventListener {

	/**
	 * Questo metodo viene chiamato dal ListTextField dopo la pressione di un
	 * pulsante che comporta un'operazione su un elemento, e prima di operare
	 * sull'elemento stesso. Il listener dunque riceve l'evento e deve
	 * prepararsi a ricevere una getElement. L'evento contiene un identificativo
	 * del tipo di azione che il ListTextField sta per compiere sull'elemento,
	 * in modo che il listener possa prepararsi di conseguenza.
	 * 
	 * @param evento contenente il tipo di cambiamoento effettuato
	 */
	public void elementChanged(ListTextFieldElementEvent e);
}
