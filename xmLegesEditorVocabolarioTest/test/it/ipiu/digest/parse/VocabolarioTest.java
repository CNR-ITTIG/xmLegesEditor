/**
 * 
 */
package it.ipiu.digest.parse;

import junit.framework.TestCase;

/**
 * 
 * <hr>
 * Project 	: XmlLegesEditorVocabolarioTest<br>
 * package 	: it.ipiu.digest.parse<br>
 * Type Name : <b>VocabolarioTest</b><br>
 * Comment	:<br>
 * 
 * <hr>
 * I+ S.r.l. 05/nov/07<br>
 * <hr>
 * @author Macchia<br>
 * <hr>
 * 
 */
public class VocabolarioTest extends TestCase {

	private Vocabolario vocabolario;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		vocabolario = new Vocabolario();
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Test method for {@link it.ipiu.digest.parse.Vocabolario#setMaterie(String[])}
	 */
	public void testSetMateria() {
		
		String[] list = new String[2];
		list[0]= "materia1";
		list[1]= "materia2";
		vocabolario.setMaterie(list);
		assertEquals("materia1", vocabolario.getMateria(0).toString());
	}
}
