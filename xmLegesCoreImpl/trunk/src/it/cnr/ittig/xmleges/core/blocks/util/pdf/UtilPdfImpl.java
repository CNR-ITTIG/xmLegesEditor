package it.cnr.ittig.xmleges.core.blocks.util.pdf;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.util.pdf.UtilPdf;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.fop.apps.Driver;
import org.apache.fop.messaging.MessageHandler;
import org.w3c.dom.Document;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.util.pdf.UtilPdf</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>dtd-rules-manager</li>
 * <li>document-manager</li>
 * <li>util-ui</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @see
 * @version 1.1
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */

public class UtilPdfImpl implements UtilPdf, Loggable, Serviceable {
	Logger logger;

	DocumentManager documentManager;

	UtilFile utilFile;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	/**
	 * Trasforma un file XML in un file PDF utilizzando una trasformazione intermedia in
	 * XSL-FO
	 * 
	 * @param xmlUri URI del dcoumento XML
	 * @param xsltUri URI dello stylesheet XSL-FO
	 * @param pdfUri URI dove salvare la versione del documento in PDF
	 * 
	 */
	public void convertXML2PDF(String xmlUri, String xsltUri, String pdfUri) {

		// Construct driver
		Driver driver = new Driver();

		// Setup logger
		org.apache.avalon.framework.logger.Logger avalonLogger = new ConsoleLogger(ConsoleLogger.LEVEL_INFO);
		driver.setLogger(avalonLogger);
		MessageHandler.setScreenLogger(avalonLogger);

		// Setup Renderer (output format)
		driver.setRenderer(Driver.RENDER_PDF);

		// Setup output
		try {
			OutputStream out = new java.io.FileOutputStream(new File(pdfUri));
			driver.setOutputStream(out);

			// Setup XSLT
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(new File(xsltUri)));

			// Setup input for XSLT transformation
			Source src = new StreamSource(new File(xmlUri));

			// Resulting SAX events (the generated FO) must be piped through to FOP
			Result res = new SAXResult(driver.getContentHandler());

			// Start XSLT transformation and FOP processing
			transformer.transform(src, res);
			out.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void convertXML2PDF(Document doc, String xsltUri, String pdfUri) {
		// Salvo il documento DOM su di un file temporaneo
		try {
			File temp = UtilFile.createTemp("export-pdf.xml");
			if (saveDocument(temp))
				convertXML2PDF(temp.getAbsolutePath(), xsltUri, pdfUri);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private boolean saveDocument(File file) {
		String encoding;
		String defaultEncoding = "UTF-8";
		if (documentManager.getEncoding() == null) {
			logger.warn("No encoding found. Using default:" + defaultEncoding);
			encoding = defaultEncoding;
		} else
			encoding = documentManager.getEncoding();
		DOMWriter domWriter = new DOMWriter();
		domWriter.setCanonical(false);
		domWriter.setFormat(true);
		try {
			domWriter.setOutput(file, encoding);
			domWriter.write(documentManager.getDocumentAsDom());
			return true;
		} catch (UnsupportedEncodingException ex) {
			logger.error(ex.toString(), ex);
		} catch (FileNotFoundException ex) {
			logger.error(ex.toString(), ex);
		}
		return false;
	}

}
