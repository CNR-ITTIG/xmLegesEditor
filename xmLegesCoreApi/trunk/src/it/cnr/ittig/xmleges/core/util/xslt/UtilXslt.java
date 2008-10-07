package it.cnr.ittig.xmleges.core.util.xslt;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

import com.sun.org.apache.xalan.internal.xslt.EnvironmentCheck;

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

	private static boolean cache = true; 

	private static Hashtable transformers = new Hashtable(50);

	/**
	 * Svuota il buffer dei fogli di trasformazione.
	 */
	public static void clearXsltBuffer() {
		transformers.clear();
	}

	
	/**
	 * Restituisce un istanza del Transformer per il  file di
	 * trasformazione <code>xslt</code>.
	 * @param xslt file di trasformazione
	 * @return Transformer
	 * @throws TransformerException
	 */
	protected static Transformer getTransformerFor(File xslt) throws TransformerException{
	
		// CHECK ENVIRONMENT: 
		//boolean environmentOK = (new EnvironmentCheck()).checkEnvironment (new PrintWriter(System.out));

		Transformer tr;
		if (transformers.containsKey(xslt) && cache) {
			tr = (Transformer) transformers.get(xslt);
		} else {
			TransformerFactory factory = TransformerFactory.newInstance();
			
			factory.setAttribute("http://xml.apache.org/xalan/features/incremental",Boolean.TRUE);
			/*
			 * http://xml.apache.org/xalan-j/features.html#factoryfeature
			 * 
			 * Produce output incrementally, rather than waiting to finish parsing the input before generating any output. 
			 * By default this attribute is set to false. You can turn this attribute on to transform large documents where 
			 * the stylesheet structure is optimized to execute individual templates without having to parse the entire document. 
			 * For more information, see DTM incremental.
			 * 
			 * 
			 *  When set to true: If the parser is Xerces, we perform an incremental transform on a single thread using the 
			 *  Xerces "parse on demand" feature. If the parser is not Xerces, we run the transform in one thread and the parse in another. 
			 *  Exception: if the parser is not Xerces and the XML source is a DOMSource, setting this feature to true has no effect.
			 * 
			 * Nel nostro caso e' una DOMSource
			 * 
			 */
			
			tr = factory.newTransformer(new StreamSource(xslt));
			
			tr.setOutputProperty(OutputKeys.METHOD, "html");		  
			tr.setOutputProperty(OutputKeys.INDENT, "no");
		    tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			tr.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-15");
			tr.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD HTML 4.01 Transitional//EN");
			tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd");

			transformers.put(xslt, tr);

			//Boolean isStreamSource = (Boolean) factory.getFeature("http://javax.xml.transform.stream.StreamSource/feature");
			//Boolean isStreamResult = (Boolean) factory.getFeature("http://javax.xml.transform.stream.StreamResult/feature");
			
			//if(isStreamSource)
			//	System.err.println("Stream source supported");
			//if(isStreamResult)
			//	System.err.println("Stream result supported");

		}
		return tr; 
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
		return applyXslt(getTransformerFor(xslt), node, param);
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
		return applyXslt(getTransformerFor(xslt), node, param, encoding);
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

		if(encoding != null){
			try{
			  param.put("encoding",encoding);  
			  tr.setOutputProperty(OutputKeys.ENCODING,encoding.toUpperCase());
			}
			catch(IllegalArgumentException e){
				System.err.println(e.getMessage());
			}
		}	
		return applyXslt(tr,node,param);
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
	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	//    SERIALIZED METHODS
	////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Converte il nodo <code>node</code> con le regole del file di
	 * trasformazione <code>xslt</code>.
	 * 
	 * @param node nodo da convertire
	 * @param xslt file di trasformazione
	 * @return trasformazione serializzata
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static String serializedApplyXslt(Node node, File xslt) throws TransformerException {
		return serializedApplyXslt(node, xslt, null, null);
	}
	
	
	/**
	 * Converte il nodo <code>node</code> con le regole del file di
	 * trasformazione <code>xslt</code>.
	 * 
	 * @param node nodo da convertire
	 * @param xslt file di trasformazione
	 * @return trasformazione serializzata
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static String serializedApplyXslt(Node node, File xslt, String encoding) throws TransformerException {
		// FIXME w/a per problemi di setOutputProperties in Xalan
		Hashtable enc = null;
		if(encoding != null){
		 enc = new Hashtable(1);
		 enc.put("encoding",encoding);
		}
		return serializedApplyXslt(node, xslt, enc, encoding);
	}

	
	
	/**
	 * Converte il nodo <code>node</code> con le regole del file di
	 * trasformazione <code>xslt</code> e i suoi parametri <code>param</code>.
	 * 
	 * @param node nodo da convertire
	 * @param xslt file di trasformazione
	 * @param param parametri del file di trasformazione
	 * @return trasformazione serializzata
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static String serializedApplyXslt(Node node, File xslt, Hashtable param) throws TransformerException {
		return serializedApplyXslt(getTransformerFor(xslt), node, param);
	}
	
	
	
	/**
	 * Converte il nodo <code>node</code> con le regole del file di
	 * trasformazione <code>xslt</code> e i suoi parametri <code>param</code>.
	 * 
	 * @param node nodo da convertire
	 * @param xslt file di trasformazione
	 * @param param parametri del file di trasformazione
	 * @return trasformazione serializzata
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static String serializedApplyXslt(Node node, File xslt, Hashtable param, String encoding) throws TransformerException {
		return serializedApplyXslt(getTransformerFor(xslt), node, param, encoding);
	}
	

	/**
	 * Converte il nodo <code>node</code> tramite il trasformer
	 * <code>tr</code> applicando i parametri <code>param</code>.
	 * 
	 * @param tr trasformatore
	 * @param node nodo da convertire
	 * @param param parametri
	 * @param encoding encoding da applicare all'html
	 * @return trasformazione serializzata
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static String serializedApplyXslt(Transformer tr, Node node, Hashtable param, String encoding) throws TransformerException {

		if(encoding != null){
			try{
			  param.put("encoding",encoding);  
			  tr.setOutputProperty(OutputKeys.ENCODING,encoding.toUpperCase());
			}
			catch(IllegalArgumentException e){
				System.err.println(e.getMessage());
			}
		}	
		return serializedApplyXslt(tr,node,param);
	}
	
	/**
	 * Converte il nodo <code>node</code> tramite il trasformer
	 * <code>tr</code> applicando i parametri <code>param</code>.
	 * 
	 * @param tr trasformatore
	 * @param node nodo da convertire
	 * @param param parametri
	 * @return trasformazione serializzata
	 * @throws TransformerException se il file di trasformazione non &egrave;
	 *         applicabile
	 */
	public static String serializedApplyXslt(Transformer tr, Node node, Hashtable param) throws TransformerException {
		DOMSource source = new DOMSource(node);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Result result = new StreamResult(baos);
	    tr.clearParameters();
		setTransformerParam(tr, param);
   		tr.transform(source, result);
		
		return baos.toString();		
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////////////////////////////

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
	
	/**
	 * Rimuove l'oggetto <code>xslt</code> dal traformatore.
	 * 
	 * @param xslt l'oggetto da rimuovere
	 */
	public static void remove(File xslt) {
		transformers.remove(xslt);
	}
	
	
	
}
