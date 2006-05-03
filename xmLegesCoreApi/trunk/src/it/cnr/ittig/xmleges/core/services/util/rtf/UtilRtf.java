package it.cnr.ittig.xmleges.core.services.util.rtf;

import java.io.File;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Document;

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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public interface UtilRtf extends Service {

	/**
	 * @param xmlUri			DA BUTTARE
	 * @param xsltUri
	 * @param rtfUri
	 */
	public void convertXML2RTF(String xmlUri, String xsltUri, String rtfUri);
	/**
	 * @param xmlDom			DA BUTTARE
	 * @param xsltUri
	 * @param rtfUri
	 */
	public void convertXML2RTF(Document xmlDom, String xsltUri, String rtfUri);
	/**
	 * @param FO
	 * @param rtfUri
	 */
	public void convertXML2RTF(String FO, String rtfUri);
	
}
