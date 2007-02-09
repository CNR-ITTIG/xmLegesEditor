package it.cnr.ittig.xmleges.core.services.form;

/**
 * Eccezione emessa durante l'uso di Form.
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @see it.cnr.ittig.xmleges.core.services.form.Form
 */
public class FormException extends Exception {

	/**
	 * Crea una nuova istanza di <code>FormException</code> con il messaggio
	 * <code>msg</code>.
	 * 
	 * @param msg messaggio
	 */
	public FormException(String msg) {
		super(msg);
	}
}
