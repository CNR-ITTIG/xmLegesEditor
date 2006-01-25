package it.cnr.ittig.xmleges.core.services.form.date;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.CommonForm;

/**
 * Servizio per l'inserimento di una data.
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

public interface DateForm extends Service, CommonForm {

	/**
	 * Imposta la data <code>date</code>.
	 * 
	 * @param date data
	 */
	public void set(java.util.Date date);

	/**
	 * Restituisce la data come <code>java.util.Date</code>.
	 * 
	 * @return data
	 */
	public java.util.Date getAsDate();

	/**
	 * Restituisce la data come stringa formattata secondo l'attuale
	 * internazionalizzazione.
	 * 
	 * @return data formattata
	 */
	public String getAsString();

	/**
	 * Restituisce la data normalizzata, ovvero formattata come AAAAMMGG
	 * 
	 * @return data normalizzata
	 */
	public String getAsYYYYMMDD();
}
