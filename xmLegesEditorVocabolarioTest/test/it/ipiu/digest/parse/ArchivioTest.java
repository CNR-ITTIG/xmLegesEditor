/**
 * 
 */
package it.ipiu.digest.parse;

import java.util.Vector;

import junit.framework.TestCase;

/**
 * 
 * <hr>
 * Project 	: XmlLegesEditorVocabolarioTest<br>
 * package 	: it.ipiu.digest.parse<br>
 * Type Name : <b>ArchivioTest</b><br>
 * Comment	:<br>
 * 
 * <hr>
 * I+ S.r.l. 31/ott/07<br>
 * <hr>
 * @author Macchia<br>
 * <hr>
 * 
 */
public class ArchivioTest extends TestCase {

	private Vocabolario[] vocabolario1;
	private Vocabolario[] vocabolario2;
	private String[] nomiVocabolari = {"1Voc0", "1Voc1", "Voc2"}; 
	String[] materie = {"Mat1","Mat2", "Mat3"};

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */

	protected void setUp() throws Exception {

		vocabolario1 = new Vocabolario[4];
		vocabolario1[0] = new Vocabolario("Voc0");
		vocabolario1[1] = new Vocabolario("Voc1");
		vocabolario1[1].setMaterie(materie);
		vocabolario1[2] = new Vocabolario("Voc2");
		vocabolario1[3] = new Vocabolario("Voc3");


		vocabolario2 = new Vocabolario[3];
		vocabolario2[0] = new Vocabolario("1Voc0");
		vocabolario2[1] = new Vocabolario("1Voc1");
		vocabolario2[2] = new Vocabolario("Voc2");

		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link it.ipiu.digest.parse.Archivio#setVocabolari(it.ipiu.digest.parse.Vocabolario[])}.
	 */
	public void testSetVocabolari() {
		Archivio archivio = new Archivio();
		archivio.setVocabolari(vocabolario1);
		Vocabolario[] vocabolariTmp = archivio.getVocabolari();
		assertNotNull(vocabolariTmp);
		assertEquals(vocabolariTmp[0], vocabolario1[0]);
		assertEquals(vocabolariTmp[1], vocabolario1[1]);
		assertEquals(vocabolariTmp[2], vocabolario1[2]);
		assertEquals(vocabolariTmp[3], vocabolario1[3]);

	}



	/**
	 * Test method for {@link Archivio#getArrayNomiVocabolari()}
	 * @throws Exception
	 */
	public void testGetNomiVocabolario() throws Exception {
		Archivio archivio = new Archivio();
		archivio.setVocabolari(vocabolario1);
		String[] nomiVocabolari2 = archivio.getArrayNomiVocabolari();
		assertNotNull(nomiVocabolari2);
		assertEquals(nomiVocabolari2[0], vocabolario1[0].getNome());
		assertEquals(nomiVocabolari2[1], vocabolario1[1].getNome());
		assertEquals(nomiVocabolari2[2], vocabolario1[2].getNome());
		assertEquals(nomiVocabolari2[3], vocabolario1[3].getNome());
	}

	////////////////////////////


//	/**
//	* test su una parte di codice dell'editor (class {@link MaterieVocabolariFormImpl} )
//	* @throws Exception
//	*/
//	public void testEditorTest() throws Exception {
//	codiceEditor();
//	assertEquals(vocabolario2[0], vocabolario1[0]);
//	assertEquals(vocabolario2[1], vocabolario1[1]);
//	assertEquals(vocabolario2[2], vocabolario1[2]);
//	assertEquals(vocabolario2[3], vocabolario1[3]);

//	}
	private void codiceEditor() {
		if (vocabolario1 == null) {
			vocabolario1 = new Vocabolario[nomiVocabolari.length];
			for (int i = 0; i < nomiVocabolari.length; i++) {
				vocabolario1[i] = new Vocabolario();
				vocabolario1[i].setNome(nomiVocabolari[i]);
			}
		} else {

			for (int i = 0; i < vocabolario1.length; i++) {
				boolean found = false;
				for (int j = 0; j < nomiVocabolari.length; j++) {
					if (vocabolario1[i].getNome().equalsIgnoreCase(
							nomiVocabolari[j])) {
						found = true;
						break;
					}
				}
				if (found)
					break;
				else {
					removeVocabolario(i);
				}
			}
			for (int i = 0; i < nomiVocabolari.length; i++) {
				boolean missing = true;
				for (int j = 0; j < vocabolario1.length; j++) {
					Vocabolario vocabolario = vocabolario1[j];
					if (nomiVocabolari[i].equalsIgnoreCase(vocabolario
							.getNome())) {
						missing = false;
						break;
					}
				}
				if (missing)
					addVocabolario(nomiVocabolari[i]);

			}
		}
	}

	private void removeVocabolario(int index) {
		Vocabolario[] newVocabolario = new Vocabolario[vocabolario1.length - 1];
		for (int i = 0; i < newVocabolario.length; i++) {
			if (i < index)
				newVocabolario[i] = vocabolario1[i];
			else if (i > index)
				newVocabolario[i] = vocabolario1[i + 1];
		}
		vocabolario1 = newVocabolario;
	}

	/**
	 * @param nome
	 */
	private void addVocabolario(String nome) {
		Vocabolario[] newVocabolario = new Vocabolario[vocabolario1.length + 1];
		int i = 0;
		for (i = 0; i < newVocabolario.length - 1; i++)
			newVocabolario[i] = vocabolario1[i];
		newVocabolario[i] = new Vocabolario();
		newVocabolario[i].setNome(nome);

		vocabolario1 = newVocabolario;

	}

	/**
	 * test for {@link Archivio#setVocabolario(String[], String)}
	 * @throws Exception
	 */
	public void testSetVocabolario() throws Exception {
		Archivio archivio = new Archivio();
		archivio.setVocabolari(vocabolario1);
		String[] name = {"Mat1","Mat2", "Mat3"};
		archivio.setVocabolario(name, "Voc0");
		Vocabolario voc = archivio.getVocabolario(0);
		assertEquals("Voc0",voc.getNome());
		assertEquals(name[0], voc.getMateria(0).getNome());
		assertEquals(name[1], voc.getMateria(1).getNome());
		assertEquals(name[2], voc.getMateria(2).getNome());

		archivio.setVocabolario(name, "TreCani");

		assertEquals(5,archivio.getNumVocabolari());
		Vocabolario voc1 = archivio.getVocabolario(4);
		assertEquals("TreCani",voc1.getNome());
		assertEquals(name[0], voc1.getMateria(0).getNome());
		assertEquals(name[1], voc1.getMateria(1).getNome());
		assertEquals(name[2], voc1.getMateria(2).getNome());

	}

	/**
	 * test for {@link Archivio#getMaterieSelectedVocabolario(String)}
	 * @throws Exception
	 */
	public void testGetMaterieSelectedVocabolario() throws Exception {
		Archivio archivio = new Archivio();
		archivio.setVocabolari(vocabolario1);
		String[] materieSelectedVocabolario = archivio.getMaterieSelectedVocabolario("Voc1");
		assertEquals(materieSelectedVocabolario[0], this.materie[0]);
		assertEquals(materieSelectedVocabolario[1], this.materie[1]);
		assertEquals(materieSelectedVocabolario[2], this.materie[2]);


		String[] vocabolario = archivio.getMaterieSelectedVocabolario("oasidja");
		assertNull(vocabolario);		
	}

	/**
	 * test for {@link Archivio#getVocabolari()}
	 * @throws Exception
	 */
	public void testGetVocabolari() throws Exception {
		Archivio archivio = new Archivio();
		archivio.setVocabolari(vocabolario1);
		Vocabolario[] vocabolari = archivio.getVocabolari();
		assertEquals(vocabolari[0], archivio.getVocabolario(0));
		assertEquals(vocabolari[1], archivio.getVocabolario(1));
		assertEquals(vocabolari[2], archivio.getVocabolario(2));
	}

	/**
	 * test the method that convert from String to {@link Vocabolario} 
	 * @throws Exception
	 */
	public void testAdaptList() throws Exception {
		Archivio archivio = new Archivio();

		Vector vocabolario = new Vector();
		vocabolario.add(new Vocabolario("Voc0"));
		vocabolario.add("String0");
		vocabolario.add(new Vocabolario("Voc1"));
		vocabolario.add("String1");

		archivio.setVocabolari(vocabolario);
		assertTrue(archivio.getVocabolario(0) instanceof Vocabolario);
		assertTrue(archivio.getVocabolario(1) instanceof Vocabolario);
		assertTrue(archivio.getVocabolario(2) instanceof Vocabolario);
		assertTrue(archivio.getVocabolario(3) instanceof Vocabolario);
	}
	
	/**
	 * test the method thatmerge two Array of {@link Vocabolario}
	 */
	public void testBuildVocabolario() {
		Vocabolario[] vocabolariOnDoc = new Vocabolario[4];
		vocabolariOnDoc[0] = new Vocabolario("Vocabolario 1");
		vocabolariOnDoc[1] = new Vocabolario("Vocabolario 2");
		vocabolariOnDoc[1].addMateria("materia");
		vocabolariOnDoc[2] = new Vocabolario("Vocabolario 3");
		vocabolariOnDoc[3] = new Vocabolario("vocab 1");

		Vocabolario[] vocabolariOnDoctmp = new Vocabolario[1];
		vocabolariOnDoctmp[0] = new Vocabolario("vocab 1");

		Vocabolario[] def  = Archivio.merge(vocabolariOnDoc, vocabolariOnDoctmp);
		assertNotNull(def);
		assertEquals("Vocabolario 3", def[2].getNome());
		assertEquals(4, def.length);
		assertEquals("Vocabolario 3", def[2].getNome());
		assertEquals("materia", def[1].getMateria(0).getNome());
	}
	
}