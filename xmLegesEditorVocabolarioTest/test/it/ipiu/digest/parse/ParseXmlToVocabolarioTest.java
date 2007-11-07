package it.ipiu.digest.parse;

import it.cnr.ittig.xmleges.editor.blocks.form.meta.descrittori.MaterieVocabolariFormImpl;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

/**
 * 
 * <hr>
 * Project 	: XmlLegesEditorVocabolarioTest<br>
 * package 	: it.ipiu.digest.parse<br>
 * Type Name : <b>ParseXmlToVocabolarioTest</b><br>
 * Comment	:<br>
 * 
 * <hr>
 * I+ S.r.l. 30/ott/07<br>
 * <hr>
 * @author Macchia<br>
 * <hr>
 * 
 */
public class ParseXmlToVocabolarioTest extends TestCase {

	String nameFile = "file:\\c:\\cygwin\\home\\Macchia\\svn\\xmLegesEditorApi\\src\\it\\cnr\\ittig\\xmleges\\editor\\services\\dom\\meta\\descrittori\\vocabolario.xml";
	/**
	 * @throws java.lang.Exception
	 */
	protected void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link it.ipiu.digest.parse.ParseXmlToVocabolario#parse(java.lang.String)}.
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?>
<vocabolari>
	<vocabolario>
		<nome> Vocabolario Primo </nome>
		<materia>Matematica </materia>
		<materia>Inglese </materia>
		<materia>Italiano </materia>	
	</vocabolario>
	<vocabolario>
		<nome> Zanichelli </nome>
		<materia>Matematica 2</materia>
		<materia>Inglese 2</materia>
		<materia>Italiano 2</materia>	
	</vocabolario>
	<vocabolario>
		<nome> Teseo </nome>
		<materia>Statuto Comunale</materia>
		<materia>Regolamento Comunale</materia>
		<materia>Delibera Consiliare</materia>	
	</vocabolario>
</vocabolari>
	 * @throws Exception 
	 */
	public void testParse() throws Exception {
		String nameFile = "file:\\c:\\cygwin\\home\\Macchia\\svn\\xmLegesEditorApi\\src\\it\\cnr\\ittig\\xmleges\\editor\\services\\dom\\meta\\descrittori\\vocabolario.xml";
		Archivio archivio = ParseXmlToVocabolario.parse(nameFile);
		assertNotNull(archivio);
		assertEquals(archivio.getNumVocabolari(), 3);
		Vocabolario vocabolario = archivio.getVocabolario(0);
		assertNotNull(vocabolario);
		assertEquals(vocabolario.getNome(),"Vocabolario Primo");
		assertNotNull(vocabolario.getListMateria());
		List listMateria = vocabolario.getListMateria();
		assertNotNull(vocabolario.getMateria(0));
		assertEquals(vocabolario.getMateria(0).getNome(), "Matematica");
	}

	
	/**
	 * test about build of {@link Materia} from String name.
	 * @throws Exception
	 */
	public void testAddMateriaString() throws Exception {
		Vocabolario vocabolario = new Vocabolario();
		vocabolario.addMateria("materia 1");
	
	}
	
	/**
	 * test the {@link Materia} setting in a {@link Vocabolario} from an array of string
	 * @throws Exception
	 */
	public void testSetMaterie() throws Exception {
		Vocabolario vocabolario = new Vocabolario();
		String[] strings = {"Materia 1","Materia 2"};
		vocabolario.setMaterie(strings);
		assertEquals(strings[0], vocabolario.getMateria(0).getNome());
		assertEquals(strings[1], vocabolario.getMateria(1).getNome());
	}
	
	/**
	 * Test the method to recover a list of {@link Materia}
	 * @throws Exception
	 */
	public void testGetMaterie() {
		Vocabolario vocabolario = new Vocabolario();
		assertNotNull(vocabolario.getMaterie());
		assertNotNull(vocabolario.getListMateria());
	}
	
	
	/**
	 * Test the effetive size of listMateria after creation of {@link Archivio}
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public void testSizeMateria() throws IOException, SAXException {
		
		Archivio archivio = ParseXmlToVocabolario.parse(nameFile);
		
		Vocabolario vocabolario = archivio.getVocabolario(0);
		List listMateria = vocabolario.getListMateria();
		assertEquals(3, listMateria.size());
		
		Vocabolario vocabolario2 = archivio.getVocabolario(1);
		List listMateria2 = vocabolario2.getListMateria();
		assertEquals(3, listMateria2.size());
		
		Vocabolario vocabolario3 = archivio.getVocabolario(2);
		List listMateria3 = vocabolario3.getListMateria();
		assertEquals(3, listMateria3.size());
	}
	
	/**
	 * Test per verificare il corretto funzionamento del metodo eliminaVocabolario 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public void testRemoveVocabolario() throws IOException, SAXException {
		
		Archivio archivio = ParseXmlToVocabolario.parse(nameFile);
		archivio.removeVocabolario(archivio.getVocabolario(0));
		assertEquals(2, archivio.getVocabolari().length);
		assertEquals("Zanichelli", archivio.getVocabolario(0).getNome());
	}
	
	/**
	 *Test the method to delete one {@link Materia} from specific {@link Vocabolario}
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public void testRemoveMateria() throws IOException, SAXException {
		Archivio archivio = ParseXmlToVocabolario.parse(nameFile);
		Vocabolario vocabolario = archivio.getVocabolario(0);
		vocabolario.removeMateria(vocabolario.getMateria(0));
		assertEquals(2, vocabolario.getListMateria().size());
		assertEquals("Inglese", vocabolario.getMateria(0).getNome());
	}
	
	/**
	 * Test dell'action performed del form delle materie
	 */
	private void testActionPerformedMaterie() {
		//TODO creare test action performed
		MaterieVocabolariFormImpl materieVocabolariFormImpl = new MaterieVocabolariFormImpl();
		
	}
}