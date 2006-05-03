package it.cnr.ittig.xmleges.core.blocks.util.rtf;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.util.rtf.UtilRtf;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.jfor.jfor.converter.Converter;
import org.jfor.jfor.main.*;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.fop.apps.Driver;
import org.apache.fop.messaging.MessageHandler;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.util.rtf.UtilRtf</code>.
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

public class UtilRtfImpl implements UtilRtf, Loggable, Serviceable {

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
	 * Trasforma un file XML in un file RTF utilizzando una trasformazione intermedia in
	 * XSL-FO
	 * 
	 * @param xmlUri URI del dcoumento XML
	 * @param xsltUri URI dello stylesheet XSL-FO
	 * @param rtfUri URI dove salvare la versione del documento in RTF
	 * 
	 */

	
	
	// Questa funzione non serve più
	// (eliminare questa quando tutto è ok, anche dai template)

	
	public void convertXML2RTF(String xmlUri, String xsltUri, String rtfUri) {
		
//		// Construct driver
//		Driver driver = new Driver();
//
//		// Setup logger
//		org.apache.avalon.framework.logger.Logger avalonLogger = new ConsoleLogger(ConsoleLogger.LEVEL_INFO);
//		driver.setLogger(avalonLogger);
//		MessageHandler.setScreenLogger(avalonLogger);
//
//		// Setup Renderer (output format)
//		driver.setRenderer(Driver.RENDER_PDF);
//
//		// Setup output
//		try {
//			OutputStream out = new java.io.FileOutputStream(new File(rtfUri));
//			driver.setOutputStream(out);
//
//			// Setup XSLT
//			TransformerFactory factory = TransformerFactory.newInstance();
//			Transformer transformer = factory.newTransformer(new StreamSource(new File(xsltUri)));
//
//			// Setup input for XSLT transformation
//			Source src = new StreamSource(new File(xmlUri));
//
//			// Resulting SAX events (the generated FO) must be piped through to FOP
//			Result res = new SAXResult(driver.getContentHandler());
//
//			// Start XSLT transformation and FOP processing
//			transformer.transform(src, res);
//			out.close();
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
	}

	public void convertXML2RTF(String fo, String rtfUri) {
		
		
		//PROVA 2
			String args[] = new String[2];
			args[0] = fo;
			args[1] = rtfUri;
			
		try {
            CmdLineConverter.main(args);
    	} catch (Exception e) {}
            
         //PROVA 3   
//		try {
//
//   			// Used by relative image path.
//			String userDir = System.getProperty ("user.dir");
//
//			final String xslInput = fo;
//			final String rtfOutput = rtfUri;
//
//			final InputSource input = new InputSource(xslInput);
//			final Writer output = new BufferedWriter(new FileWriter(rtfOutput));
//
//			// Used by relative image path.
//			setUserDir (new File (xslInput).getCanonicalFile().getParent ());
//			new Converter(input,output,Converter.createConverterOption ());
//			// Used by relative image path.
//			setUserDir (userDir);
//                        
//		} catch (Exception e) {
//			System.out.println (e);
//		}        
		

    	
    	
    	
    	
    	
    	//PROVA 1
			//  ESEMPIO DI funzione fo-->Rtf
		    //public void foToRtf(String dir, String fodoc, String rtfdoc){

			//metterci per prova "minimal.fo.xml"
//			
//			try {
//			 final String xslInput = "minimal.fo.xml";
//			 final String rtfOutput = rtfUri;
//
//			 Properties p = System.getProperties ();
//			 p.put ("user.dir", "c:/");
//			 System.setProperties (p);
//
//			 final InputSource input = new InputSource(xslInput);
//
//			 final Writer output = new BufferedWriter(new FileWriter(rtfOutput));
//
//			 new Converter(input,output,Converter.createConverterOption ());
//
//
//			 output.close();
//			}
//			catch (Exception e) {
//			
//			}

			
			
//			out.close();
	
	        
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}        

	}

	//funzioni che aggiungo io
	
	/** Convert from a filename to a file URL. */
	private static String fileToUrl(String filename)
	throws MalformedURLException
	{
		return toURL (new File (filename)).toString ();
	}

	/** System.setProperty function (java 1.1 compatibility) */
	private static void setUserDir (String dir)
	{
		if (dir == null)
		{
			return;
		}
		Properties p = System.getProperties ();
		p.put ("user.dir", dir);
		System.setProperties (p);
	}

	/**
	 * Converts this abstract pathname into a <code>file:</code> URL.  The
	 * exact form of the URL is system-dependent.  If it can be determined that
	 * the file denoted by this abstract pathname is a directory, then the
	 * resulting URL will end with a slash.
	 *
	 * @return a URL object representing the equivalent file URL.
	 * @throws MalformedURLException if the path cannot be parsed as a URL.
	 * @see     java.net.URL
	 * @since   1.2
	 */
	private static URL toURL (File file) throws MalformedURLException {
		String path = file.getAbsolutePath();
		if (File.separatorChar != '/') {
			path = path.replace(File.separatorChar, '/');
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.endsWith("/") && file.isDirectory()) {
			path = path + "/";
		}
		return new URL("file", "", path);
	}
	

	
	//fine funzioni che aggiungo io
	
	
	
	
	
	// Questa funzione è diventata del tutto superflua. Prende il documento, lo salva
	// in un file temp (che già ho) e richiamo la 2° funzione con gli stessi parametri
	
	//   Richiamere direttamente l'altra (e poi eliminare questa quando tutto è ok)
	
	public void convertXML2RTF(Document doc, String xsltUri, String rtfUri) {
		// Salvo il documento FO su di un file temporaneo
		try {
			File temp = UtilFile.createTemp("export-rtf.xml");
			if (saveDocument(temp))
				convertXML2RTF(temp.getAbsolutePath(), xsltUri, rtfUri);
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
