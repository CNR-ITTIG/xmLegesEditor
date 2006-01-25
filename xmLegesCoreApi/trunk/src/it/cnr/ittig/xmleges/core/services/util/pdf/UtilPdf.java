package it.cnr.ittig.xmleges.core.services.util.pdf;

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
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public interface UtilPdf extends Service {

	/**
	 * @param xmlUri
	 * @param xsltUri
	 * @param pdfUri
	 */
	public void convertXML2PDF(String xmlUri, String xsltUri, String pdfUri);

	/**
	 * @param xmlDom
	 * @param xsltUri
	 * @param pdfUri
	 */
	public void convertXML2PDF(Document xmlDom, String xsltUri, String pdfUri);

}
