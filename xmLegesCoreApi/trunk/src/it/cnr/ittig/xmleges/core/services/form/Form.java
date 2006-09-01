package it.cnr.ittig.xmleges.core.services.form;

import it.cnr.ittig.services.manager.Service;

import java.awt.Component;
import java.io.InputStream;

import javax.swing.JDialog;

/**
 * Servizio per la costruzione di una form e visualizzazione in una finestra di
 * dialogo.
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
public interface Form extends Service {

	/**
	 * Imposta il componente padre di default per tutte le form.
	 * 
	 * @param owner componente padre di default
	 */
	public void setDefaultOwner(Component owner);

	/**
	 * Imposta il componente principale della form. L'InputStream che contiene
	 * la descrizione della form dipende dall'implementazione usata.
	 * 
	 * @param desc descrizione del componente principale
	 * @throws FormException se non &egrave; possibile impostare
	 *         <code>comp</code> come componente principale
	 */
	public void setMainComponent(InputStream desc) throws FormException;

	/**
	 * Imposta il componente principale della form.
	 * 
	 * @param comp componente principale
	 * @throws FormException se non &egrave; possibile impostare
	 *         <code>comp</code> come componente principale
	 */
	public void setMainComponent(Component comp) throws FormException;

	/**
	 * Indica se &egrave; stato impostato il componente principale della form.
	 * 
	 * @return <code>true</code> &egrave; stato impostato
	 */
	public boolean hasMainComponent();

	/**
	 * Imposta la dimensione della form.
	 * 
	 * @param dimX larghezza
	 * @param dimY altezza
	 */
	public void setSize(int width, int height);

	/**
	 * Imposta il nome della form.
	 * 
	 * @param name nome della form
	 */
	public void setName(String name);

	/**
	 * Imposta la chiave dell'help della form.
	 * 
	 * @param key chiave per l'help
	 */
	public void setHelpKey(String key);
	
	/**
	 * Imposta la chiave dell'help della form.
	 * @param key chiave per l'help
	 * @param helpFormlistener listener della chiusura dell'help
	 */
	public void setHelpKey(String key, FormClosedListener helpFormlistener);

	/**
	 * Visualizza la form come dialogo usando come oggetto padre, se
	 * specificato, quello impostato tramite <code>setDefaultOwner</code>.
	 */
	public void showDialog();

	/**
	 * Visualizza la form come dialogo usando come oggetto padre, se
	 * specificato, quello impostato tramite <code>setDefaultOwner</code>.
	 * 
	 * @param modal <code>true</code> per presentare la form modale
	 */
	public void showDialog(boolean modal);

	/**
	 * Visualizza la form come dialogo usando come oggetto padre
	 * <code>owner</code>.
	 * 
	 * @param owner padre della form
	 */
	public void showDialog(Component owner);

	/**
	 * Visualizza la form come dialogo non modale.
	 * 
	 * @param listener listener per notificare la chiusura della form
	 */
	public void showDialog(FormClosedListener listener);

	/**
	 * Visualizza la form come dialogo non modale.
	 * 
	 * @param listener listener per notificare la chiusura della form
	 * @param owner padre della form
	 */
	public void showDialog(FormClosedListener listener, Component owner);

	/**
	 * Ricostruisce la form dell'Help.
	 * 
	 * @param listener listener per notificare la chiusura della form
	 * @param owner padre della form
	 */
	public void rebuildHelp(FormClosedListener listener, Component owner);
	
	/**
	 * Indica se la form di dialogo &egrave; visibile.
	 * 
	 * @return <code>true</code> se &egrave; visibile
	 */
	public boolean isDialogVisible();

	/**
	 * Imposta la form di dialogo in wainting bloccando qualsiasi interazione e
	 * presentando il cursore di attesa.
	 * 
	 * @param waiting true per attivare la modalit&a di waiting
	 */
	public void setDialogWaiting(boolean waiting);

	/**
	 * Indica se la form di dialogo &egrave; in stato di waiting.
	 * 
	 * @return <code>true</code> se &egrave; in stato di waiting
	 */
	public boolean isDialogWaiting();

	/**
	 * Imposta la form di dialogo come ridimensionabile (default
	 * <code>true</code>).
	 * 
	 * @param resizable <code>true</code> se deve essere ridiminsionabile
	 */
	public void setDialogResizable(boolean resizable);

	/**
	 * Indica se la form di dialogo &egrave; ridimensionabile.
	 * 
	 * @return <code>true</code> se &egrave; ridimensionabile
	 */
	public boolean isDialogResizable();

	/**
	 * Restituisce la form come <code>java.awt.Component</code>.
	 * 
	 * @return componente contente la form
	 */
	public Component getAsComponent();

	/**
	 * Restituisce la form come <code>java.awt.Component</code> con le
	 * decorazioni aggiuntive (titolo, icona ecc).
	 * 
	 * @return componente contente la form
	 */
	public Component getAsComponentWithDecor();

	/**
	 * Restituisce il componente corrispondente al nome <i>compName </i>.
	 * 
	 * @param compName nome del componente
	 * @return componente
	 */
	public Component getComponentByName(String compName);

	/**
	 * Sostituisce il componente di nome <code>compName</code> con la form
	 * descritta da <code>desc</code>. L'InputStream che contiene la
	 * descrizione della form dipende dall'implementazione usata.
	 * 
	 * @param compName nome del componente
	 * @param desc descrizione del compoenente principale
	 * @throws FormException se non &egrave; possibile sostituire il componente
	 */
	public void replaceComponent(String compName, InputStream desc) throws FormException;

	/**
	 * Sostituisce il componente di nome <code>compName</code> con il
	 * componente <code>comp</code>.
	 * 
	 * @param compName nome del componente
	 * @param comp componente che deve essere inserito
	 * @throws FormException se non &egrave; possibile sostituire il componente
	 */
	public void replaceComponent(String compName, Component comp) throws FormException;

	/**
	 * Imposta i pulsanti con i testi <param>text </param>.
	 * 
	 * @param text testi dei pulsanti
	 */
	public void setCustomButtons(String[] text);

	/**
	 * Restituisce <i>true </i> se &egrave; stato premuto il tasto <i>Ok </i> o
	 * il primo tasto se impostato con <code>setCustomButtons</code>.
	 * 
	 * @return <code>true</code> se <i>Ok </i> o il primo pulsante &egrave;
	 *         stato premuto
	 */
	public boolean isOk();

	/**
	 * Restituisce <i>true </i> se &egrave; stato premuto il tasto <i>Cancel
	 * </i> o il secondo tasto se impostato con <code>setCustomButtons</code>.
	 * 
	 * @return <code>true</code> se <i>Ok </i> o il secondo pulsante &egrave;
	 *         stato premuto
	 */
	public boolean isCancel();

	/**
	 * Restituisce l'indice del pulsante premuto. L'indice del primo tasto
	 * &egrave; 1.
	 * 
	 * @return indice del pulsante premuto
	 */
	public int getPressedButton();
		
	/**
	 * Forza la chiusura della form.
	 */
	public void close();

	/**
	 * Aggiunge un <code>FormVerifier</code> che permette di verificare, prima
	 * della chiusura della form, se tutti i campi sono corretti e quindi
	 * procedere con la chiusura. Se la verifica fallisce (
	 * <code>verifier.verifyForm() == false</code>), allora verr&agrave;
	 * presentato un messaggio di errore recuperato tramite il metodo
	 * <code>verifier.getErrorMessage()</code>.<br>
	 * I vari <code>verifier</code> aggiunti sono utilizzati solo su la
	 * pressione del tasto <code>Ok</code>, <b>non </b> sull'invocazione del
	 * metodo <code>close()</code>.
	 * 
	 * @param verifier oggetto che implementa <code>FormVerifier</code> per
	 *        controllare la validit? dei campi inseriti nella form
	 */
	public void addFormVerifier(FormVerifier verifier);

	/**
	 * Rimuove il FormVerifier dalla lista dei controllori di validit&agrave; di
	 * dati della form.
	 * 
	 * @param verifier verifier da rimuovere
	 */
	public void removeFormVerifier(FormVerifier verifier);

	/**
	 * Rimuove tutti i controllori di validit&agrave; di dati della form.
	 */
	public void removeAllFormVerifier();

	/**
	 * Aggiunge il menu per la gestione del copia e incolla al componente con
	 * nome <code>name</code>.
	 * 
	 * @param name nome del componente
	 */
	public void addCutCopyPastePopupMenu(String name);

	/**
	 * Aggiunge il menu per la gestione del copia e incolla al componente
	 * <code>comp</code>.
	 * 
	 * @param comp componente
	 */
	public void addCutCopyPastePopupMenu(Component comp);

}
