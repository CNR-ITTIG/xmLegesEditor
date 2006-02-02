package it.cnr.ittig.xmleges.editor.services.xmleges.linker;

import it.cnr.ittig.services.manager.Service;

import java.io.InputStream;

import org.w3c.dom.Node;

/**
 * Servizio per eseguire il riconoscimento dei riferimenti esterni.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface XmLegesLinker extends Service {

	/**
	 * Imposta la regione di default se non specificata nella citazione.
	 * 
	 * @param regione regione di default
	 */
	public void setRegione(String regione);

	/**
	 * Imposta il ministero di default se non specificato nella citazione.
	 * 
	 * @param ministero ministero di default
	 */
	public void setMinistero(String ministero);

	/**
	 * Cerca i riferimenti non marcati nel testo <code>text</code>.
	 * 
	 * @param text testo da analizzare
	 * @return risultato dell'elaborazione
	 */
	public InputStream parse(String text) throws XmLegesLinkerException;

	/**
	 * Cerca i riferimenti non marcati nel sottoalbero indentificato da
	 * <code>node</code>.
	 * 
	 * @param node sottoalbero nel quale cercare i riferimenti
	 * @return risultato dell'elaborazione
	 */
	public InputStream parse(Node node) throws XmLegesLinkerException;

	/**
	 * Restituisce lo stderr dell'esecuzione del parser di struttura. Se non ci
	 * sono stati errori restituisce <code>null</code>.
	 * 
	 * @return stringa contente l'errore oppure <code>null</code>.
	 */
	public String getError();

}
