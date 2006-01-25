/*
 * Created on Dec 17, 2004 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.cnr.ittig.xmleges.editor.services.template;

/**
 * Eccezione su recupero template.
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class TemplateException extends Exception {

	/**
	 * Costruttore con messaggio.
	 * 
	 * @param message messaggio
	 */
	public TemplateException(String message) {
		super(message);
	}

	/**
	 * Costruttore con messaggio.
	 * 
	 * @param message messaggio
	 * @param cause causa
	 */
	public TemplateException(String message, Throwable cause) {
		super(message, cause);
	}

}
