/**
 * 
 */
package it.ipiu.digest.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;

/**
 * 
 * <hr>
 * Project : DigestProva<br>
 * package : it.ipiu.digest.parse<br>
 * Type Name : <b>Archivio</b><br>
 * Comment :<br>
 * 
 * <hr>
 * I+ S.r.l. 30/ott/07<br>
 * <hr>
 * 
 * @author Macchia<br>
 *         <hr>
 * 
 */
public class Archivio {
	private List vocabolari;

	public Archivio() {
		vocabolari = new Vector();
	}

	public Archivio(Vocabolario[] vocabolariOnDoc) {
		vocabolari = new Vector();
		setVocabolari(vocabolariOnDoc);
	}

	public void addVocabolario(Vocabolario vocabolario) {
		vocabolari.add(vocabolario);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < vocabolari.size(); i++) {
			buffer.append(System.getProperty("line.separator"));
			buffer.append(vocabolari.get(i));
		}
		return buffer.toString();
	}

	/**
	 * @return the number of vocabolaries.
	 */
	public int getNumVocabolari() {
		return vocabolari.size();
	}

	/**
	 * @param index
	 *            of vocabolario
	 * @return the vocabolario at index TODO to test
	 */
	public Vocabolario getVocabolario(int index) {
		return (Vocabolario) vocabolari.get(index);
	}

	/**
	 * @return the array of {@link Vocabolario} TODO to test
	 */
	public Vocabolario[] getVocabolari() {
		Vocabolario[] list = new Vocabolario[this.vocabolari.size()];
		for (int i = 0; i < this.vocabolari.size(); i++) {
			Vocabolario tmp = (Vocabolario) this.vocabolari.get(i);
			list[i] = tmp;
		}
		return list;
	}
	
	

	/**
	 * Remove one {@link Vocabolario} from specific {@link Archivio}
	 * 
	 * @param vocabolario
	 * @return
	 */
	public Vocabolario[] removeVocabolario(Vocabolario vocabolario) {
		vocabolari.remove(vocabolario);
		return getVocabolari();
	}
	
	
	/**
	 * @param vocabolari2
	 *            to set.
	 */
	public void setVocabolari(Vocabolario[] vocabolari2) {
		this.vocabolari.clear();
		CollectionUtils.addAll(this.vocabolari, vocabolari2);
	}

	/**
	 * @return an array with all name of vocabolari.
	 */
	public String[] getArrayNomiVocabolari() {
		String[] nomi = new String[this.vocabolari.size()];
		for (int i = 0; i < nomi.length; i++) {
			nomi[i] = (String) ((Vocabolario) this.vocabolari.get(i)).getNome();
		}
		return nomi;
	}

	/**
	 * @return a {@link Vector} of Vocabolari.
	 */
	public Vector getVocabolariCollection() {
		return (Vector) this.vocabolari;
	}

	/**
	 * 
	 * @param listElements
	 *            set the elements in {@link Archivio}
	 */
	public void setVocabolari(Vector listElements) {
		listElements = adaptList(listElements);
		this.vocabolari = listElements;
	}

	
	/**
	 * @param list to adapt to a list of {@link Vocabolario}
	 * @return a list adapted.
	 */
	public Vector adaptList(Vector list) {
		Vector newList = new Vector();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof String) {
				String string = (String) list.get(i);
				Vocabolario vocabolario = new Vocabolario(string);
				newList.add(vocabolario);
			} else {
				newList.add(list.get(i));
			}
		}
		return newList;	
	}
	/**
	 * set a {@link Vocabolario} with a set of materie. If vocabolario is
	 * present, it substitutes its {@link Materia}.
	 * 
	 * @param materie
	 *            array of materie to substitutes
	 * @param vocabolario
	 *            to set.
	 */
	public void setVocabolario(String[] materie, String vocabolario) {
		Vocabolario voc = (Vocabolario) CollectionUtils.find(this.getVocabolariCollection(), Vocabolario.getPredicateByName(vocabolario));
		if(voc == null) {
			voc = new Vocabolario(vocabolario);
			addVocabolario(voc);
		}
		voc.setMaterie(materie);
	}

	/**
	 * remove one vocabolario by index
	 * @param index
	 */
	public void removeVocabolario(int index) {
		this.vocabolari.remove(index);
	}

	/**
	 * @param string
	 * @return the materias from a vocabolario selected by name
	 */
	public String[] getMaterieSelectedVocabolario(String vocabolario) {
		Vocabolario voc = (Vocabolario) CollectionUtils.find(this.getVocabolariCollection(), Vocabolario.getPredicateByName(vocabolario));
		return voc != null ?  voc.getMaterie() : null;
	}
	
	/**
	 * Verifica che il documento abbia già uno o più vocabolari al suo interno e
	 * costruisce un unico vocabolario composto da quest'ultimo e da i
	 * vocabolari contenenti nel file xml di default
	 * 
	 * @param vocabolariOnDoc
	 * @param vocabolariOnDoctmp
	 * @return array of {@link Vocabolario}
	 */
	private List mergeVocabolario(Vocabolario[] vocabolariOnDoctmp) {
		List vocab2 = new Vector();
		CollectionUtils.addAll(vocab2, vocabolariOnDoctmp);

		for (int i = 0; i < vocab2.size(); i++) {

			int n = 0;

			for (int j = 0; j < this.vocabolari.size(); j++) {
				if (vocab2.get(i).toString().equals(this.vocabolari.get(j).toString())) {
					n = 1;
				}
			}
			if (n != 1) {
				this.vocabolari.add(vocab2.get(i));
			}
		}

		return this.vocabolari;
	}
	
	/**
	 * @param vocabolariOnDoc
	 * @param vocabolariOnDoctmp
	 * @return array of {@link Vocabolario}
	 */
	public static Vocabolario[] merge(Vocabolario[] vocabolariOnDoc, Vocabolario[] vocabolariOnDoctmp){
		Archivio archivio;
		if (vocabolariOnDoctmp != null) {
			archivio = new Archivio(vocabolariOnDoc);
			archivio.mergeVocabolario(vocabolariOnDoctmp);
		} else {
			archivio = new Archivio(vocabolariOnDoc);
		}

		return archivio.getVocabolari();
		
	}

	
}