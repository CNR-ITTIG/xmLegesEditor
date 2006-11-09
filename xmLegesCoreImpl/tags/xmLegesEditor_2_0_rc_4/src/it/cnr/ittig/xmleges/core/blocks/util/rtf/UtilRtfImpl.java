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
	

	public void convertXML2RTF(String fo, String rtfUri) {
		
//		String args[] = new String[2];
//		args[0] = fo;
//		args[1] = rtfUri;
//			
//		try {
//            CmdLineConverter.main(args);
//    	} catch (Exception e) {}    	
    	
	}

	
//	/** Convert from a filename to a file URL. */
//	private static String fileToUrl(String filename)
//	throws MalformedURLException
//	{
//		return toURL (new File (filename)).toString ();
//	}
//
//	/** System.setProperty function (java 1.1 compatibility) */
//	private static void setUserDir (String dir)
//	{
//		if (dir == null)
//		{
//			return;
//		}
//		Properties p = System.getProperties ();
//		p.put ("user.dir", dir);
//		System.setProperties (p);
//	}
//
//	/**
//	 * Converts this abstract pathname into a <code>file:</code> URL.  The
//	 * exact form of the URL is system-dependent.  If it can be determined that
//	 * the file denoted by this abstract pathname is a directory, then the
//	 * resulting URL will end with a slash.
//	 *
//	 * @return a URL object representing the equivalent file URL.
//	 * @throws MalformedURLException if the path cannot be parsed as a URL.
//	 * @see     java.net.URL
//	 * @since   1.2
//	 */
//	private static URL toURL (File file) throws MalformedURLException {
//		String path = file.getAbsolutePath();
//		if (File.separatorChar != '/') {
//			path = path.replace(File.separatorChar, '/');
//		}
//		if (!path.startsWith("/")) {
//			path = "/" + path;
//		}
//		if (!path.endsWith("/") && file.isDirectory()) {
//			path = path + "/";
//		}
//		return new URL("file", "", path);
//	}
			
}
