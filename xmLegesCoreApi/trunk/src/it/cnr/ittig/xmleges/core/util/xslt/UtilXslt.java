package it.cnr.ittig.xmleges.core.util.xslt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

/**
 * Classe di utilit&agrave; per l'applicazione dei fogli di stile a un documento
 * <code>org.w3c.dom.Document</code>.
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
 * @see org.w3c.dom.Document
 * @see javax.xml.transform.Transformer
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class UtilXslt {

	private static boolean cache = false; // default true

	private static Hashtable transformers = new Hashtable(50);

	/**
	 * Svuota il buffer dei fogli di trasformazione.
	 */
	public static void clearXsltBuffer() {
		transformers.clear();
	}

	/**
	 * Converte il nodo <code>node</code> con le regole di trasformazione in
	 * <code>xslt</code>. Attenzione xslt contiene le regole di
	 * trasformazione non il path del file (per questo usare
	 * <code>transformDom(Node node, File xslt)</code>).
	 * 
	 * @param node nodo da convertire
	 * @param xslt regole di trasformazione
	 * @return trasformazione
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static Node applyXslt(Node node, String xslt) throws TransformerException {
		return applyXslt(node, xslt, null);
	}

	/**
	 * Converte il nodo <code>node</code> con le regole del file di
	 * trasformazione <code>xslt</code>.
	 * 
	 * @param node nodo da convertire
	 * @param xslt file di trasformazione
	 * @return trasformazione
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static Node applyXslt(Node node, File xslt) throws TransformerException {
		return applyXslt(node, xslt, null, null);
	}
	
	/**
	 * Converte il nodo <code>node</code> con le regole del file di
	 * trasformazione <code>xslt</code>.
	 * 
	 * @param node nodo da convertire
	 * @param xslt file di trasformazione
	 * @return trasformazione
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static Node applyXslt(Node node, File xslt, String encoding) throws TransformerException {
		// FIXME w/a per problemi di setOutputProperties in Xalan
		Hashtable enc = null;
		if(encoding != null){
		 enc = new Hashtable(1);
		 enc.put("encoding",encoding);
		}
		return applyXslt(node, xslt, enc, encoding);
		//return applyXslt(node, xslt, null, encoding);
	}

	/**
	 * Converte il nodo <code>node</code> con le regole del file di
	 * trasformazione <code>xslt</code> e i suoi parametri <code>param</code>.
	 * 
	 * @param node nodo da convertire
	 * @param xslt file di trasformazione
	 * @param param parametri del file di trasformazione
	 * @return trasformazione
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static Node applyXslt(Node node, String xslt, Hashtable param) throws TransformerException {
		Transformer tr;
		if (transformers.containsKey(xslt) && cache) {
			tr = (Transformer) transformers.get(xslt);
		} else {
			InputStream is = new ByteArrayInputStream(xslt.getBytes());
			TransformerFactory factory = TransformerFactory.newInstance();
			tr = factory.newTransformer(new StreamSource(is));
			transformers.put(xslt, tr);
		}
		return applyXslt(tr, node, param);
	}

	/**
	 * Converte il nodo <code>node</code> con le regole del file di
	 * trasformazione <code>xslt</code> e i suoi parametri <code>param</code>.
	 * 
	 * @param node nodo da convertire
	 * @param xslt file di trasformazione
	 * @param param parametri del file di trasformazione
	 * @return trasformazione
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static Node applyXslt(Node node, File xslt, Hashtable param) throws TransformerException {
		Transformer tr;
		if (transformers.containsKey(xslt) && cache) {
			tr = (Transformer) transformers.get(xslt);
		} else {
			TransformerFactory factory = TransformerFactory.newInstance();
			tr = factory.newTransformer(new StreamSource(xslt));
			transformers.put(xslt, tr);
		}
		return applyXslt(tr, node, param);
	}
	
	
	/**
	 * Converte il nodo <code>node</code> con le regole del file di
	 * trasformazione <code>xslt</code> e i suoi parametri <code>param</code>.
	 * 
	 * @param node nodo da convertire
	 * @param xslt file di trasformazione
	 * @param param parametri del file di trasformazione
	 * @return trasformazione
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static Node applyXslt(Node node, File xslt, Hashtable param, String encoding) throws TransformerException {
		Transformer tr;
		if (transformers.containsKey(xslt) && cache) {
			tr = (Transformer) transformers.get(xslt);
		} else {
			TransformerFactory factory = TransformerFactory.newInstance();
			tr = factory.newTransformer(new StreamSource(xslt));
			transformers.put(xslt, tr);
		}
		return applyXslt(tr, node, param, encoding);
	}
	

	/**
	 * Converte il nodo <code>node</code> tramite il trasformer
	 * <code>tr</code> applicando i parametri <code>param</code>.
	 * 
	 * @param tr trasformatore
	 * @param node nodo da convertire
	 * @param param parametri
	 * @return trasformazione
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static Node applyXslt(Transformer tr, Node node, Hashtable param) throws TransformerException {
		DOMSource source = new DOMSource(node);
		DOMResult result = new DOMResult();
		tr.clearParameters();
		setTransformerParam(tr, param);
		tr.transform(source, result);
		return result.getNode();
	}
	
	
	/**
	 * Converte il nodo <code>node</code> tramite il trasformer
	 * <code>tr</code> applicando i parametri <code>param</code>.
	 * 
	 * @param tr trasformatore
	 * @param node nodo da convertire
	 * @param param parametri
	 * @param encoding encoding da applicare all'html
	 * @return trasformazione
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static Node applyXslt(Transformer tr, Node node, Hashtable param, String encoding) throws TransformerException {
		DOMSource source = new DOMSource(node);
		DOMResult result = new DOMResult();
		
		if(encoding != null){
			try{
			  param.put("encoding",encoding);
			  tr.setOutputProperty(OutputKeys.METHOD, "html");		  
			  tr.setOutputProperty(OutputKeys.ENCODING,encoding.toUpperCase());
			}
			catch(IllegalArgumentException e){
				System.err.println(e.getMessage());
			}
		}	
		tr.clearParameters();
		setTransformerParam(tr, param);
		
		tr.transform(source, result);
		return result.getNode();
	}
	

	/**
	 * Applica i parametri <code>param</code> al traformatore <code>tr</code>.
	 * 
	 * @param tr trasformatore
	 * @param param parametri
	 */
	public static void setTransformerParam(Transformer tr, Hashtable param) {
		if (param != null) {
			Enumeration en = param.keys();
			while (en.hasMoreElements()) {
				String name = en.nextElement().toString();
				tr.setParameter(name, param.get(name));
			}
		}
	}

}
