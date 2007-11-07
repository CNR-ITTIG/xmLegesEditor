/**
 * 
 */
package it.ipiu.digest.parse;

import java.io.IOException;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * 
 * <hr>
 * Project 	: xmLegesEditorImpl<br>
 * package 	: it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori<br>
 * Type Name : <b>ParseXmlToVocabolario</b><br>
 * Comment	:<br>
 * Attraverso il digest recupera i parametri dal file Xml e crea l'oggetto Vocabolario
 * <hr>
 * I+ S.r.l. 30/ott/07<br>
 * <hr>
 * @author Macchia<br>
 * <hr>
 * 
 */
public class ParseXmlToVocabolario {

	public static Archivio parse(String filename) throws IOException, SAXException{
		Digester digester = new Digester();
		
		//creo Oggetto Vocabolario
		digester.addObjectCreate("vocabolari", Archivio.class);
		digester.addObjectCreate("vocabolari/vocabolario", Vocabolario.class);
		digester.addBeanPropertySetter("vocabolari/vocabolario/nome", "nome");
		
		digester.addObjectCreate("vocabolari/vocabolario/materia", Materia.class);
		digester.addBeanPropertySetter("vocabolari/vocabolario/materia", "nome");
		
		digester.addSetNext("vocabolari/vocabolario/materia", "addMateria");
		digester.addSetNext("vocabolari/vocabolario", "addVocabolario");
		
		return (Archivio)digester.parse(filename);
	}
}
