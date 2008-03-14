package it.cnr.ittig.xmleges.core.util.xml;

import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Classe contenete metodi statici di uso generico per la conversione in DOM di
 * file o stringhe.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>, Alessio
 *         Ceroni
 */
public class UtilXml {

	/**
	 * Legge la DTD dal file usando il meccanismo di callback di DeclHandler. I
	 * nomi delle entita' vengono risolti usando il DtdResolver.
	 * 
	 * @param file file XML da cui estrarre la DTD
	 * @param dtdReader oggetto che riceve le dichiarazioni della DTD
	 */
	public static void readDTD(File file, DeclHandler dtdReader) {
		try {
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxFactory.newSAXParser();

			saxParser.setProperty("http://xml.org/sax/properties/declaration-handler", dtdReader);
			saxParser.parse(file, new DefaultHandler() {
				private DtdResolver resolver = new DtdResolver();

				public InputSource resolveEntity(String publicId, String systemId) {
					return resolver.resolveEntity(publicId, systemId);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Legge il file XML specificato da <code>file</code> restituendo il DOM
	 * corrispondente.
	 * 
	 * @param file percorso del file contenente il documento XML
	 * @return DOM del file
	 */
	public static Document readXML(String file) {
		return readXML(new File(file), false, null);
	}

	/**
	 * Legge il file XML specificato da <code>file</code> restituendo il DOM
	 * corrispondente. Il file &egrave; validato se <code>validate</code>
	 * &egrave; <i>true </i>.
	 * 
	 * @param file percorso del file contenente il documento XML
	 * @param validate indica se il parser deve validare il documento
	 * @return DOM del file
	 */
	public static Document readXML(String file, boolean validate) {
		return readXML(new File(file), validate, null);
	}

	/**
	 * Legge il file XML specificato da <code>file</code> restituendo il DOM
	 * corrispondente. Il file &egrave; validato se <code>validate</code>
	 * &egrave; <i>true </i>. I messaggi di errore vengono gestiti
	 * dall'ErrorHandler.
	 * 
	 * @param file percorso del file contenente il documento XML
	 * @param validate indica se il parser deve validare il documento
	 * @param eh ErrorHandler per la gestione degli errori
	 * @return DOM del file
	 */
	public static Document readXML(String file, boolean validate, ErrorHandler eh) {
		return readXML(new File(file), validate, eh);
	}

	/**
	 * Legge il file XML specificato da <code>file</code> restituendo il DOM
	 * corrispondente.
	 * 
	 * @param file file contenente il documento XML
	 * @return DOM del file
	 */
	public static Document readXML(File file) {
		return readXML(file, false, null);
	}

	/**
	 * Legge il file XML specificato da <code>file</code> restituendo il DOM
	 * corrispondente. Il file &egrave; validato se <code>validate</code>
	 * &egrave; <i>true </i>.
	 * 
	 * @param file file contenente il documento XML
	 * @param validate indica se il parser deve validare il documento
	 * @return DOM del file
	 */
	public static Document readXML(File file, boolean validate) {
		return readXML(file, validate, null);
	}

	/**
	 * Legge il file XML specificato da <code>file</code> restituendo il DOM
	 * corrispondente. Il file &egrave; validato se <code>validate</code>
	 * &egrave; <i>true </i>. I messaggi di errore vengono gestiti
	 * dall'ErrorHandler.
	 * 
	 * @param file file contenente il documento XML
	 * @param validate indica se il parser deve validare il documento
	 * @param eh ErrorHandler per la gestione degli errori
	 * @return DOM del file
	 */
	public static Document readXML(File file, boolean validate, ErrorHandler eh) {
		return (readXML(file, validate, eh, true));
	}

	/**
	 * Legge il file XML specificato da <code>file</code> restituendo il DOM
	 * corrispondente. Il file &egrave; validato se <code>validate</code>
	 * &egrave; <i>true </i>. I messaggi di errore vengono gestiti
	 * dall'ErrorHandler.
	 * 
	 * @param file file contenente il documento XML
	 * @param validate indica se il parser deve validare il documento
	 * @param eh ErrorHandler per la gestione degli errori
	 * @param nameSpaceAware
	 * @return DOM del file
	 */
	public static Document readXML(File file, boolean validate, ErrorHandler eh, boolean nameSpaceAware) {
		try {
			// return readXML(new FileInputStream(file));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			dbf.setNamespaceAware(nameSpaceAware);
			if (validate) {
				dbf.setValidating(true);
				dbf.setIgnoringElementContentWhitespace(true);
			}
			dbf.setIgnoringComments(false);

			DocumentBuilder domBuilder = dbf.newDocumentBuilder();
			domBuilder.setEntityResolver(new DtdResolver());
			if (eh != null)
				domBuilder.setErrorHandler(eh);

			return (domBuilder.parse(file));

		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Legge il file XML specificato dall'InputStream <code>is</code>
	 * restituendo il DOM corrispondente.
	 * 
	 * @param is stream contenente il documento XML
	 * @return DOM del file
	 */
	public static Document readXML(InputStream is) {
		return readXML(is, false);
	}

	/**
	 * Legge, attivando la validazione con DTD, il file XML specificato
	 * dall'InputStream <code>is</code> restituendo il DOM corrispondente.
	 * 
	 * @param is stream contenente il documento XML
	 * @param validate
	 * @return DOM del file
	 */
	public static Document readXML(InputStream is, boolean validate) {
		return (readXML(is, validate, null, true));
	}

	/**
	 * Legge, attivando la validazione con DTD, il file XML specificato
	 * dall'InputStream <code>is</code> restituendo il DOM corrispondente.
	 * 
	 * @param is stream contenente il documento XML
	 * @param validate
	 * @param nameSpaceAware
	 * @return DOM del file
	 */
	public static Document readXML(InputStream is, boolean validate, boolean nameSpaceAware) {
		return (readXML(is, validate, null, nameSpaceAware));
	}

	/**
	 * Legge, attivando la validazione con DTD, il file XML specificato
	 * dall'InputStream <code>is</code> restituendo il DOM corrispondente.
	 * 
	 * @param is stream contenente il documento XML
	 * @param validate
	 * @param eh
	 * @param nameSpaceAware
	 * @return DOM del file
	 */
	public static Document readXML(InputStream is, boolean validate, ErrorHandler eh, boolean nameSpaceAware) {
		try {
			File temp = File.createTempFile("tmp", "tmp");
			UtilFile.stringToFile(UtilFile.inputStreamToString(is), temp);
			Document doc = readXML(temp, validate, eh, nameSpaceAware);
			temp.delete();
			return doc;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Versione del metodo che elimina dal file l'attributo xmlns che d&agrave; problemi prima di parsare l'XML.
	 * 
	 * @param is stream contenente il documento XML
	 * @param validate
	 * @param eh
	 * @param nameSpaceAware
	 * @return DOM del file
	 */
	public static Document readAndFixXML(InputStream is, boolean validate, ErrorHandler eh, boolean nameSpaceAware) {
		try {
			String removeThis = "xmlns=\"http://www.normeinrete.it/nir/1.0\"";
			String removeThat = "xmlns=\"http://www.normeinrete.it/disegnilegge/1.0\"";
			File temp = File.createTempFile("tmp", "tmp");
			String filteredText = UtilFile.inputStreamToString(is).replaceAll(removeThis, "");
			filteredText = filteredText.replaceAll(removeThat, "");
			UtilFile.stringToFile(filteredText, temp);
			Document doc = readXML(temp, validate, eh, nameSpaceAware);
			temp.delete();
			return doc;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Converte la stringa <code>text</code> contenente codice XML nel
	 * corrispondente albero DOM.
	 * 
	 * @param text testo XML da convertire in DOM
	 * @return DOM del testo XML
	 */
	public static Document textToXML(String text) {
		return (textToXML(text, true));
	}

	/**
	 * Converte la stringa <code>text</code> contenente codice XML nel
	 * corrispondente albero DOM.
	 * 
	 * @param text testo XML da convertire in DOM
	 * @param nameSpaceAware
	 * @return DOM del testo XML
	 */
	public static Document textToXML(String text, boolean nameSpaceAware) {
		try {
			return readXML(new ByteArrayInputStream(text.getBytes()), false, nameSpaceAware);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Converte la stringa <code>text</code> contenente codice XML nel
	 * corrispondente albero DOM. Il documento restituito ha come nodo radice
	 * <code>rootName</code>.
	 * 
	 * @param text testo XML da convertire in DOM
	 * @param rootName nome del nodo radice
	 * @return DOM del testo XML
	 */
	public static Document textToXML(String text, String rootName) {
		StringBuffer sb = new StringBuffer("<" + rootName + ">");
		sb.append(text);
		sb.append("</" + rootName + ">");
		return textToXML(sb.toString());
	}

	/**
	 * Converte la stringa <code>text</code> contenente codice XML nel
	 * corrispondente albero DOM importandolo nel documento passato (<code>
	 * document</code>).
	 * Il nodo restituito appartiene a <code>document
	 * </code> ma non e'
	 * inserito in nessun posto.
	 * 
	 * @param text testo XML da convertire in DOM
	 * @param document DOM nel quale importare il testo convertito in DOM
	 * @return DOM del testo XML
	 */
	public static Node textToXML(String text, Document document) {
		try {
			Node node = textToXML(text);
			return document.importNode(node.getFirstChild(), true);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Converte la stringa <code>text</code> contenente codice XML nel
	 * corrispondente albero DOM. Il DOM restituito ha come nodo radice <code>
	 * rootName</code>,
	 * appartiene a <code>document</code> ma non &egrave; inserito in nessun
	 * posto.
	 * 
	 * @param text testo XML da convertire in DOM
	 * @param rootName nome del nodo radice
	 * @param document DOM nel quale importare il testo convertito in DOM
	 * @return DOM del testo XML
	 */
	public static Node textToXML(String text, String rootName, Document document) {
		try {
			Node node = textToXML(text, rootName);
			return document.importNode(node.getFirstChild(), true);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * @param node
	 */
	public static void concatenaTextNode(Node node) {
		NodeList nl = node.getChildNodes();
		Node p = nl.item(0);
		for (int i = 1; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeType() == Node.TEXT_NODE && p.getNodeType() == Node.TEXT_NODE) {
				p.setNodeValue(p.getNodeValue() + nl.item(i).getNodeValue());
				node.removeChild(nl.item(i));
				i--;
			} else
				p = nl.item(i);
		}
	}

}
