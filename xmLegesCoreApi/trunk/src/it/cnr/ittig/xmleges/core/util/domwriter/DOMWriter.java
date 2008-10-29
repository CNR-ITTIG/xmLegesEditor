package it.cnr.ittig.xmleges.core.util.domwriter;

import it.cnr.ittig.xmleges.core.util.lang.UtilLang;
import it.cnr.ittig.xmleges.core.util.xslt.UtilXslt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe per costruire il file XML da un documento DOM.
 * 
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class DOMWriter {

	static String defaultEncoding = "iso-8859-15";

	public static void setDefaultEncoding(String encoding) {
		if (encoding != null)
			defaultEncoding = encoding;
	}

	/** Print writer. */
	protected PrintWriter fOut;

	/** Canonical output. */
	protected boolean canonical;

	/** Character encoding */
	protected String charEncoding;

	/** Tab level */
	int tabLevel;

	/** Format output with tab */
	boolean format = false;

	boolean trimText = true;

	String formatTab = "\t";

	boolean xmlns = true;

	boolean expandEmptyTag = false;

	/** Default constructor. */
	public DOMWriter() {
		this(false);
	}

	public DOMWriter(boolean canonical) {
		tabLevel = -1;
		setCanonical(canonical);
		charEncoding = defaultEncoding;
		format = false;
	}

	/** Sets whether output is canonical. */
	public void setCanonical(boolean canonical) {
		this.canonical = canonical;
	}

	public boolean isCanonical() {
		return this.canonical;
	}

	public void setOutput(String fileName) throws UnsupportedEncodingException, FileNotFoundException {
		setOutput(fileName, null);
	}

	public void setOutput(String fileName, String encoding) throws UnsupportedEncodingException, FileNotFoundException {
		setOutput(new File(fileName), encoding);
	}

	public void setOutput(File file) throws UnsupportedEncodingException, FileNotFoundException {
		setOutput(new FileOutputStream(file), null);
	}

	public void setOutput(File file, String encoding) throws UnsupportedEncodingException, FileNotFoundException {
		setOutput(new FileOutputStream(file), encoding);
	}

	public void setOutput(OutputStream stream) throws UnsupportedEncodingException {
		setOutput(stream, null);
	}

	/** Sets the output stream for printing. */
	public void setOutput(OutputStream stream, String encoding) throws UnsupportedEncodingException {
		if (encoding != null)
			charEncoding = new String(encoding);
		fOut = new PrintWriter(new OutputStreamWriter(stream, charEncoding));
	}

	/** Sets the output writer. */
	public void setOutput(Writer writer) {
		fOut = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer);
	}

	public void setFormat(boolean format) {
		this.format = format;
	}

	public boolean isFormat() {
		return format;
	}

	public void setFormatTab(String formatTab) {
		this.formatTab = formatTab;
	}

	public String getFormatTab() {
		return this.formatTab;
	}

	protected void addTabs() {
		if (format)
			for (int i = 0; i < tabLevel; i++)
				fOut.print(getFormatTab());
	}

	public void setTrimText(boolean trimText) {
		this.trimText = trimText;
	}

	public boolean isTrimText() {
		return this.trimText;
	}

	public void setXmlns(boolean xmlns) {
		this.xmlns = xmlns;
	}

	public boolean isXmlns() {
		return this.xmlns;
	}

	public void setExpandEmptyTag(boolean expandEmptyTag) {
		this.expandEmptyTag = expandEmptyTag;
	}

	public boolean isExpandEmptyTag() {
		return this.expandEmptyTag;
	}

	
	
	/** Writes serialized Document */
	public void write(String serializedDoc){
		fOut.print(serializedDoc);
		fOut.flush();
	}
	
	

	
	/** Serialize and Writes Node */
	public void write(Node node){
		fOut.print(UtilXslt.serializeXML(node,format,charEncoding));
		fOut.flush();
	}
	
	
//	/** Writes the specified node, recursively. */
//	@Deprecated
//	public void write(Node node) {
//		if (node == null) {
//			return;
//		}
//		tabLevel++;
//
//		short type = node.getNodeType();
//		switch (type) {
//		case Node.DOCUMENT_NODE: {
//			Document document = (Document) node;
//			if (!isCanonical()) {
//				addTabs();
//				fOut.println("<?xml version=\"1.0\" encoding=\"" + charEncoding + "\"?>");
//				fOut.flush();
//				write(document.getDoctype());
//			}
//			write(document.getDocumentElement());
//			break;
//		}
//
//		case Node.DOCUMENT_TYPE_NODE: {
//			addTabs();
//			DocumentType doctype = (DocumentType) node;
//			fOut.print("<!DOCTYPE ");
//			fOut.print(doctype.getName());
//			String publicId = doctype.getPublicId();
//			String systemId = doctype.getSystemId();
//			if (publicId != null) {
//				fOut.print(" PUBLIC '");
//				fOut.print(publicId);
//				fOut.print("' '");
//				fOut.print(systemId);
//				fOut.print('\'');
//			} else {
//				fOut.print(" SYSTEM '");
//				fOut.print(systemId);
//				fOut.print('\'');
//			}
//			String internalSubset = doctype.getInternalSubset();
//			if (internalSubset != null) {
//				fOut.println(" [");
//				fOut.print(internalSubset);
//				fOut.print(']');
//			}
//			fOut.println('>');
//			break;
//		}
//
//		case Node.ELEMENT_NODE: {
//			addTabs();
//			fOut.print('<');
//			fOut.print(node.getNodeName());
//			Attr attrs[] = sortAttributes(node.getAttributes());
//			for (int i = 0; i < attrs.length; i++) {
//				Attr attr = attrs[i];
//
//				if (!isXmlns() && attr.getNodeName().indexOf("xmlns") != -1)
//					continue;
//
//				// Modifica eseguita da Andrea Marchetti il 30-10-2003
//				// Il problema nasce dalla routine getAttributes che
//				// recupera sia gli attributi presenti nell'elemento
//				// che quelli dichiarati di tipo default dalla DTD.
//				// Il problema nasce dal fatto che non si preoccupa se
//				// nell'elemento l'attributo di tipo default gi? compariva
//				// In questo caso lo replica.
//				if (i > 0 && attrs[i].getNodeName().compareTo(attrs[i - 1].getNodeName()) == 0)
//					continue; // salta gli eventuali attributi doppioni
//				else {
//					fOut.print(' ');
//					fOut.print(attr.getNodeName());
//					fOut.print("=\"");
//					normalizeAndPrint(attr.getNodeValue());
//					fOut.print('"');
//				}
//			}
//
//			if (!isExpandEmptyTag() && isEmpty(node))
//				fOut.println(" />");
//			else {
//
//				if (node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
//					fOut.print('>');
//					normalizeAndPrint(getText(node.getFirstChild()));
//					fOut.print("</");
//					fOut.print(node.getNodeName());
//					if (isFormat())
//						fOut.println('>');
//					else
//						fOut.print('>');
//
//				} else {
//					if (isFormat())
//						fOut.println('>');
//					else
//						fOut.print('>');
//
//					for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
//						write(child);
//
//					addTabs();
//					fOut.print("</");
//					fOut.print(node.getNodeName());
//					if (isFormat())
//						fOut.println('>');
//					else
//						fOut.print('>');
//				}
//				fOut.flush();
//			}
//
//			fOut.flush();
//
//			break;
//		}
//
//		case Node.ENTITY_REFERENCE_NODE: {
//			
//			System.out.println("***************   ENTITY FOUND");
//			
//			if (isCanonical()) {
//				for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
//					write(child);
//			} else {
//				fOut.print('&');
//				fOut.print(node.getNodeName());
//				fOut.print(';');
//				fOut.flush();
//			}
//			break;
//		}
//
//		case Node.CDATA_SECTION_NODE: {
//			// modifica inserita da Alessio Ceroni: elimina dal nodo di
//			// testo
//			// i caratteri di spaziatura iniziali e finali
//			String text = getText(node);
//
//			if (isCanonical()) {
//				if (text.length() > 0) {
//					addTabs();
//					normalizeAndPrint(text);
//					fOut.println();
//				}
//			} else {
//				fOut.print("<![CDATA[");
//				fOut.print(text);
//				if (isFormat())
//					fOut.println("]]>");
//				else
//					fOut.print("]]>");
//			}
//			fOut.flush();
//			break;
//		}
//
//		case Node.TEXT_NODE: {
//			// modifica inserita da Alessio Ceroni: elimina dal nodo di
//			// testo
//			// i caratteri di spaziatura iniziali e finali
//			if (format) {
//				String text = getText(node);
//				if (text.length() > 0) {
//					addTabs();
//					normalizeAndPrint(text);
//					fOut.println();
//				}
//			} else
//				normalizeAndPrint(node.getNodeValue());
//			fOut.flush();
//			break;
//		}
//
//		case Node.PROCESSING_INSTRUCTION_NODE: {
//			addTabs();
//			fOut.print("<?");
//			fOut.print(node.getNodeName());
//			String data = node.getNodeValue();
//			if (data != null && data.length() > 0) {
//				fOut.print(' ');
//				fOut.print(data);
//			}
//			fOut.println("?>");
//			fOut.flush();
//			break;
//		}
//
//		case Node.COMMENT_NODE: {
//			fOut.print("<!--");
//			fOut.print(node.getNodeValue());
//			fOut.println("-->");
//			fOut.flush();
//			break;
//		}
//		}
//
//		tabLevel--;
//	} // write(Node)

	/** Returns a sorted list of attributes. */
	protected Attr[] sortAttributes(NamedNodeMap attrs) {

		int len = (attrs != null) ? attrs.getLength() : 0;
		Attr array[] = new Attr[len];
		for (int i = 0; i < len; i++) {
			array[i] = (Attr) attrs.item(i);
		}
		for (int i = 0; i < len - 1; i++) {
			String name = array[i].getNodeName();
			int index = i;
			for (int j = i + 1; j < len; j++) {
				String curName = array[j].getNodeName();
				if (curName.compareTo(name) < 0) {
					name = curName;
					index = j;
				}
			}
			if (index != i) {
				Attr temp = array[i];
				array[i] = array[index];
				array[index] = temp;
			}
		}

		return array;

	} // sortAttributes(NamedNodeMap):Attr[]

	//
	// Protected methods
	//

	/** Normalizes and prints the given string. */
	protected void normalizeAndPrint(String s) {

		int len = (s != null) ? s.length() : 0;
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			normalizeAndPrint(c);
		}
	} // normalizeAndPrint(String)

	/** Normalizes and print the given character. */
	protected void normalizeAndPrint(char c) {

		switch (c) {
		case '<': {
			fOut.print("&lt;");
			break;
		}
		case '>': {
			fOut.print("&gt;");
			break;
		}
		case '&': {
			fOut.print("&amp;");
			break;
		}
		case '"': {
			fOut.print("&quot;");
			break;
		}
		case '\r':
		case '\n': {
			if (canonical) {
				fOut.print("&#");
				fOut.print(Integer.toString(c));
				fOut.print(';');
				break;
			}
			// else, default print char
		}
		default: {
			fOut.print(c);
		}
		}

	} // normalizeAndPrint(char)

	protected String getText(Node node) {
		return isTrimText() ? UtilLang.trimText(node.getNodeValue()) : node.getNodeValue();
	}

	/**
	 * Controlla se un elemento &egrave; vuoto.
	 * 
	 * @param node Nodo
	 * @return true se &egrave; vuoto
	 */
	public boolean isEmpty(Node node) {
		NodeList nl = node.getChildNodes();
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			String v = nl.item(i).getNodeValue();
			// v==null nodo non testo
			// v!=null e trim != 0 nodo testo con caratteri
			if (v == null || UtilLang.trimText(v).length() != 0)
				return false;
		}
		return true;
	}

}
