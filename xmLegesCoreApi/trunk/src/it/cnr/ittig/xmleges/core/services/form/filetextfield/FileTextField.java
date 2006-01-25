package it.cnr.ittig.xmleges.core.services.form.filetextfield;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.CommonForm;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Servizio per l'inserimento di una file da tastiera o finestra di dialogo
 * standard.
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

public interface FileTextField extends Service, CommonForm {

	/**
	 * Imposta il nome del file o della directory.
	 * 
	 * @param name nome
	 */
	public void set(String name);

	/**
	 * Imposta il file o la directory.
	 * 
	 * @param file file
	 */
	public void set(File file);

	/**
	 * Imposta il filtro per la visualizzazione dei file.
	 * 
	 * @param fileFilter
	 */
	public void setFileFilter(FileFilter fileFilter);

	/**
	 * Permette di effettuare il check del file se &egrave; di sola lettura.
	 * Quando &egrave; attivo il metodo <code>getFile()</code> restituisce
	 * <code>null</code> per i file di sola lettura.
	 * 
	 * @param ro <code>true</code> se deve essere controllato se il file
	 *        &egrave; di sola lettura
	 */
	public void setCheckReadOnly(boolean ro);

	/**
	 * Indica se &egrave; abilitato la verifica di sola lettura sul file.
	 * 
	 * @return <code>true</code> se &egrave; attiva
	 */
	public boolean isCheckReadOnly();

	/**
	 * Permette di selezionare le directory.
	 * 
	 * @param directory <code>true</code> se possone e essere selezionate le
	 *        directory
	 */
	public void setSelectDirectory(boolean directory);

	/**
	 * Indica se &egrave; possibile selezionare le directory
	 * 
	 * @return <code>true</code> se &egrave; attiva
	 */
	public boolean isSelectDirectory();

	/**
	 * Restituisce il file selezionato o <code>null</code> se nessun file
	 * &egrave; stato selezionato.
	 * 
	 * @return file selezionato o <code>null</code>
	 */
	public File getFile();

	/**
	 * Aggiunge il listener che sar&agrave; avvertito quando un file &egrave;
	 * stato selezionato.
	 * 
	 * @param listener listener che sar&agrave; avvertito
	 */
	public void addFileTextFieldListener(FileTextFieldListener listener);

	/**
	 * Rimuove il listener
	 * 
	 * @param listener listener da rimuovere
	 */
	public void removeFileTextFieldListener(FileTextFieldListener listener);

	/**
	 * Rimuove tutti i listener.
	 */
	public void removeAllFileTextFieldListener();

}
