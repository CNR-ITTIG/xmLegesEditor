package it.cnr.ittig.xmleges.editor.services.template;

import it.cnr.ittig.services.manager.Service;

import java.io.File;
import java.util.Properties;

/**
 * Servizio per la gestione dei template per i nuovi documenti o gli allegati.
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
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public interface Template extends Service {

	/**
	 * Restituisce il Template del Documento <code>src<code> come File
	 * sostituendo al DOCTYPE il contenuto di <code>props<code> 
	 * 
	 * @param src  nome del template
	 * @param props properties da sostituire
	 */
	public File getNirTemplate(String src, Properties props) throws TemplateException;

}
