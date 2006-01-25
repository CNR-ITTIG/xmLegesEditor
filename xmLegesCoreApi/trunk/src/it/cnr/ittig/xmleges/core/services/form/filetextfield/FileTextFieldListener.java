package it.cnr.ittig.xmleges.core.services.form.filetextfield;

import java.io.File;

/**
 * Interfaccia per essere avvertiti della selezione di un file.
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

public interface FileTextFieldListener {

	/**
	 * Metodo invocato sulla selezione di un file.
	 * 
	 * @param file file selezionato
	 */
	public void fileSelected(FileTextField src, File file);
}
