package it.cnr.ittig.xmleges.editor.blocks.xmleges.marker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class DdlRegionaliMarker {
	/*
	 * Aggiusta l'xml risultante dalla marcatura di un DDL/PDL Piemonte effettuta tramite xmLegesMarker.
	 * 
	 * IMPORTANTE: Utilizzare xmLegesMarker specificando tipo documento: LEGGE.
	 */

	private String inputFileName = "";
	private String outputFileName = "";

	private String encoding = "UTF-8";
	
	/**
	 * 
	 * @return true if output file was written
	 */
	public boolean processXML() {
		
		if(inputFileName.equals("")) {
			System.out.println("Error - Specify original xml file as input.");
			return false;
		}
		
		Document dom = null;
		
   		try {
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		//dbf.setValidating(false);
    		DocumentBuilder domBuilder = dbf.newDocumentBuilder();
    		dom = domBuilder.parse(new File(inputFileName));
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

   		return fixXML(dom);
	}
		
	private boolean fixXML(Document dom) {	

		Element nir = dom.getDocumentElement();
		
		Element art = (Element) nir.getElementsByTagName("Legge").item(0); 
		dom.renameNode(art, null, "DocArticolato");
		
		NodeList metas = art.getElementsByTagName("meta");
		Element meta = (Element) metas.item(0);
		
		//Verifica e completa i meta DESCRITTORI di default
		Element descr = (Element) meta.getElementsByTagName("descrittori").item(0);

		if(!descr.hasChildNodes()) {

			Element pubb = dom.createElement("pubblicazione");
			pubb.setAttribute("tipo", "GU");
			pubb.setAttribute("name", "");
			pubb.setAttribute("norm", "");
			descr.appendChild(pubb);
			
			Element vigore = dom.createElement("entratainvigore");
			vigore.setAttribute("norm", "");
			descr.appendChild(vigore);
			
			Element redazione = dom.createElement("redazione");
			redazione.setAttribute("nome", "");
			redazione.setAttribute("id", "red1");
			redazione.setAttribute("norm", "");
			descr.appendChild(redazione);
			
			Element urn = dom.createElement("urn");
			urn.setAttribute("valore", "");
			descr.appendChild(urn);
			
		}
		
		Element prop = dom.createElement("proprietario"); 
		meta.appendChild(prop);
		Element crpMeta = dom.createElement("crp:meta");
		crpMeta.setAttribute("xmlns:crp", "http://www.consiglioregionale.piemonte.it/atti/1.0");
		prop.appendChild(crpMeta);
		Element pres = dom.createElement("crp:presentatori");
		crpMeta.appendChild(pres);
		//Redazione
		Element redaz = (Element) meta.getElementsByTagName("redazione").item(0);
		redaz.setAttribute("id", "CRP");
		redaz.setAttribute("nome", "CRPiemonte");
		
		//TODO Calcolare la URN in qualche modo? Oppure se ne occupa xmLeges?
		Element urn = (Element) meta.getElementsByTagName("urn").item(0);
		urn.setAttribute("valore", "urn:nir:regione.piemonte;");
		
		NodeList intestaziones = art.getElementsByTagName("intestazione");
		Element intestazione = (Element) intestaziones.item(0);
		
		String headerText = "";
		String footerText = "";
		boolean preArticolato = true;
		NodeList nlist = art.getChildNodes();
		for(int i = 0; i < nlist.getLength(); i++) {
			Node item = nlist.item(i);
			//System.out.println("Type: " + item.getNodeType() + " name: " + item.getNodeName() + " val: " + item.getNodeValue());
			if(item.getNodeType() == Node.TEXT_NODE) {
				continue;
			}
			if(item.getNodeName().indexOf("articolato") > -1) {
				//Recupera il testo dal primo nodo error sotto Articolato
				NodeList artNodes = item.getChildNodes();
				for(int k = 0; k < artNodes.getLength(); k++) {
					Node artItem = artNodes.item(k);
					if(artItem.getNodeType() == Node.TEXT_NODE) {
						continue;
					}
					
					//controlla se processing instruction e aggiungi il testo in header/footer
					if(artItem.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)  { 
						String piText = artItem.getNodeValue() + " "; 
						System.out.println("PI NODE FOUND IN ARTICOLATO - text: " + piText);
						headerText += piText + " ";
						
						//Elimina il nodo error
						item.removeChild(artItem);
					}
					
					break;
				}
				/*
				Node firstNode = item.getFirstChild();
				System.out.println("type " + firstNode.getNodeType() + " val " + firstNode.getNodeValue() + " nam " + firstNode.getNodeName());
				Element firstNodeArt = (Element) firstNode.getNextSibling(); //item.getFirstChild();
				if(firstNodeArt.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)  { 
					String piText = item.getNodeValue() + " "; 
					System.out.println("PI NODE FOUND - text: " + piText);
					headerText += piText + " ";
				}
				//Elimina il nodo error
				item.removeChild(firstNodeArt);
				*/
				preArticolato = false;
				continue;
			}
			
			//controlla se processing instruction e aggiungi il testo in header/footer
			if(item.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)  { 
				String piText = item.getNodeValue() + " "; 
				System.out.println("PI NODE FOUND - text: " + piText);
				if(preArticolato) {
					headerText += piText + " ";
				} else {
					footerText += piText + " ";
				}
			}
		}
		
		System.out.println("### HEADER: " + headerText);
		System.out.println("### FOOTER: " + footerText);
		
		
		
		//Parsa header 
		DdlRegionaliHeader header = new DdlRegionaliHeader(dom, art, intestazione);
		header.parse(headerText);
		

		//Rimuovi le processing instructions (SOLO QUELLE NELL'HEADER PER ORA)
		for(int i = 0; i < nlist.getLength(); i++) {
			Node item = nlist.item(i);
			if(item.getNodeName().indexOf("articolato") > -1) {
				break;
			}
			if(item.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)  {
				art.removeChild(item);
			}
		}		
		
		//Salva
		if(!outputFileName.equals("")) {
			//writeXmlFile(dom, outputFileName);
			//saveFile(dom, outputFileName);
			
	  		String enc = encoding;
	 		if(encoding.equals("iso")) enc = "ISO-8859-1";
	 		if(encoding.equals("utf")) enc = "UTF-8";
	 		if(encoding.equals("win")) enc = "Windows-1252";
	 		
			try {
				String serial = getSerialization(dom, enc);
				PrintWriter pw = new PrintWriter(outputFileName);
				pw.write(serial);
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
		
		return false;
	}
	
	// This method writes a DOM document to a file //CON IL TRANSFORMER SI PERDE IL DOCTYPE! //TODO Usare un XML Serializer..
    private void writeXmlFile(Document doc, String filename) {
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);
    
            // Prepare the output file
            File file = new File(filename);
            Result result = new StreamResult(file);
    
            System.out.println("Serializing DOM to " + file.getAbsolutePath() + "...");
            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
        } catch (TransformerException e) {
        }
    }
    
    private void saveFile() {
    	
    	
    }
    
   private String getSerialization(Document doc, String enc) {
    	
    	String str = "error";
    	
    	try {
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			
			DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
			
			LSSerializer lsSerializer = impl.createLSSerializer();
			
			
			//lsSerializer.getDomConfig().setParameter("format-pretty-print", true);
			 
			LSOutput lsOutput = impl.createLSOutput();
			
			if(enc.equals("")) {
				enc = "UTF-8";
			}
			lsOutput.setEncoding(enc);
			
			Writer stringWriter = new StringWriter();
			
			lsOutput.setCharacterStream(stringWriter);
			
			lsSerializer.write(doc, lsOutput);
			
			//str = writer.writeToString(doc);
			str = stringWriter.toString();
			
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return str;
    }
   
   /*
  	private boolean saveFile(Document dom, String fileName) {
		
  		String enc = encoding;
 		if(encoding.equals("iso")) enc = "ISO-8859-1";
 		if(encoding.equals("utf")) enc = "UTF-8";
 		if(encoding.equals("win")) enc = "Windows-1252";
 		  		
  		
  		File file = new File(fileName);
  		System.out.println("Saving DOM to " + file.getAbsolutePath() + " with enc: " + encoding + "...");
  		
		DOMWriter domWriter = new DOMWriter();
		domWriter.setCanonical(false);
		domWriter.setFormat(true);
			
			try {
				domWriter.setOutput(file, enc);
				domWriter.write(dom);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		return true;
	}

    */

	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
