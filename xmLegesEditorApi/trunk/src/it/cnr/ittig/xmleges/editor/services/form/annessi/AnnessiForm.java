package it.cnr.ittig.xmleges.editor.services.form.annessi;

import it.cnr.ittig.services.manager.Service;

import java.io.File;

/**
 * Servizio per ...
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
 * @author <a href="mailto:sarti@dii.unisi.it">Lorenzo Sarti </a>
 */
public interface AnnessiForm extends Service {

	public void openForm();

	/**
	 * Restituisce la denominazione dell'annesso
	 */
	public String getDenominazione();

	/**
	 * Restituisce il titolo dell'annesso
	 */
	public String getTitolo();

	/**
	 * Restituisce il preambolo dell'annesso
	 */
	public String getPreambolo();

	/**
	 * Restituisce il file selezionato
	 */
	public File getSelectedFile();

	/**
	 * Restituisce il nome del template scelto
	 */
	public String getTemplate();

	/**
	 * Indica se e' stato richiesto l'inserimento di un annesso interno
	 * specificando un template
	 */
	public boolean isInternoTemplate();

	/**
	 * Indica se e' stato richiesto l'inserimento di un annesso interno
	 * specificando un File
	 */
	public boolean isInternoFile();

	/**
	 * Indica se e' stato richiesto l'inserimento di un annesso esterno
	 */
	public boolean isEsterno();

	/**
	 * Indica se la form e' stata chiusa premendo OK o Cancel
	 */
	public boolean isOKClicked();

}
