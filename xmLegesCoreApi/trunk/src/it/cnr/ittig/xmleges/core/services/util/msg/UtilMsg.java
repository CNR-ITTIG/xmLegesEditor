package it.cnr.ittig.xmleges.core.services.util.msg;

import it.cnr.ittig.services.manager.Service;

import java.awt.Component;

/**
 * Servizio per i messaggi di errore, informazione e richiesta yes/no.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface UtilMsg extends Service {

	/**
	 * Imposta il componente padre di default per tutti i messaggi.
	 * 
	 * @param owner componente padre di default
	 */
	public void setDefaultOwner(Component owner);

	/**
	 * Visualizza una finestra di dialogo di tipo error.
	 * 
	 * @param msg testo del messaggio
	 */
	public void msgError(String msg);

	/**
	 * Visualizza una finestra di dialogo di tipo error.
	 * 
	 * @param msg testo del messaggio
	 * @param title titolo della finestra
	 */
	public void msgError(String msg, String title);

	/**
	 * Visualizza una finestra di dialogo di tipo error.
	 * 
	 * @param c component al quale agganciarsi
	 * @param msg testo del messaggio
	 */
	public void msgError(Component c, String msg);

	/**
	 * Visualizza una finestra di dialogo di tipo error.
	 * 
	 * @param c component al quale agganciarsi
	 * @param msg testo del messaggio
	 * @param title titolo della finestra
	 */
	public void msgError(Component c, String msg, String title);

	/**
	 * Visualizza una finestra di dialogo di tipo info.
	 * 
	 * @param msg testo del messaggio
	 */
	public void msgInfo(String msg);

	/**
	 * Visualizza una finestra di dialogo di tipo info.
	 * 
	 * @param msg testo del messaggio
	 * @param title titolo della finestra
	 */
	public void msgInfo(String msg, String title);

	/**
	 * Visualizza una finestra di dialogo di tipo info.
	 * 
	 * @param c component al quale agganciarsi
	 * @param msg testo del messaggio
	 */
	public void msgInfo(Component c, String msg);

	/**
	 * Visualizza una finestra di dialogo di tipo info.
	 * 
	 * @param c component al quale agganciarsi
	 * @param msg testo del messaggio
	 * @param title titolo della finestra
	 */
	public void msgInfo(Component c, String msg, String title);

	/**
	 * Visualizza una finestra di dialogo di tipo yes/no.
	 * 
	 * @param msg testo del messaggio
	 * @return true se premuto il tasto yes
	 */
	public boolean msgYesNo(String msg);

	/**
	 * Visualizza una finestra di dialogo di tipo yes/no.
	 * 
	 * @param msg testo del messaggio
	 * @param title titolo della finestra
	 * @return true se premuto il tasto yes
	 */
	public boolean msgYesNo(String msg, String title);

	/**
	 * Visualizza una finestra di dialogo di tipo yes/no.
	 * 
	 * @param c component al quale agganciarsi
	 * @param msg testo del messaggio
	 * @return true se premuto il tasto yes
	 */
	public boolean msgYesNo(Component c, String msg);

	/**
	 * Visualizza una finestra di dialogo di tipo yes/no.
	 * 
	 * @param c component al quale agganciarsi
	 * @param msg testo del messaggio
	 * @param title titolo della finestra
	 * @return true se premuto il tasto yes
	 */
	public boolean msgYesNo(Component c, String msg, String title);

	/**
	 * Visualizza una finestra di dialogo di tipo yes/no/cancel.
	 * 
	 * @param msg testo del messaggio
	 * @return 0 se premuto il tasto no, 1 tasto yes, 2 tasto cancel
	 */
	public int msgYesNoCancel(String msg);

	/**
	 * Visualizza una finestra di dialogo di tipo yes/no/cancel.
	 * 
	 * @param msg testo del messaggio
	 * @param title titolo della finestra
	 * @return 0 se premuto il tasto no, 1 tasto yes, 2 tasto cancel
	 */
	public int msgYesNoCancel(String msg, String title);

	/**
	 * Visualizza una finestra di dialogo di tipo yes/no/cancel.
	 * 
	 * @param c component al quale agganciarsi
	 * @param msg testo del messaggio
	 * @return 0 se premuto il tasto no, 1 tasto yes, 2 tasto cancel
	 */
	public int msgYesNoCancel(Component c, String msg);

	/**
	 * Visualizza una finestra di dialogo di tipo yes/no/cancel.
	 * 
	 * @param c component al quale agganciarsi
	 * @param msg testo del messaggio
	 * @param title titolo della finestra
	 * @return 0 se premuto il tasto no, 1 tasto yes, 2 tasto cancel
	 */
	public int msgYesNoCancel(Component c, String msg, String title);

	/**
	 * Visualizza una finestra di dialogo di tipo warning con pulsanti yes/no.
	 * 
	 * @param msg testo del messaggio
	 * @return true se premuto il tasto yes
	 */
	public boolean msgWarning(String msg);

	/**
	 * Visualizza una finestra di dialogo di tipo warning con pulsanti yes/no.
	 * 
	 * @param msg testo del messaggio
	 * @param title titolo della finestra
	 * @return true se premuto il tasto yes
	 */
	public boolean msgWarning(String msg, String title);

	/**
	 * Visualizza una finestra di dialogo di tipo warning con pulsanti yes/no.
	 * 
	 * @param c component al quale agganciarsi
	 * @param msg testo del messaggio
	 * @return true se premuto il tasto yes
	 */
	public boolean msgWarning(Component c, String msg);

	/**
	 * Visualizza una finestra di dialogo di tipo warning con pulsanti yes/no.
	 * 
	 * @param c component al quale agganciarsi
	 * @param msg testo del messaggio
	 * @param title titolo della finestra
	 * @return true se premuto il tasto yes
	 */
	public boolean msgWarning(Component c, String msg, String title);

	/**
	 * Restituisce un'istanza di una finestra splash utile per visualizzare
	 * informazioni durante l'esecuzione di altre procedure.
	 * 
	 * @return la splash da aprire
	 */
	public Splash getSplash();

}
