/**
 * 
 */
package it.ipiu.digest.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Predicate;

/**
 * 
 * <hr>
 * Project : XmlLegesEditorVocabolario<br>
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
public class Vocabolario {

	private String nome;

	private List materia;

	public Vocabolario() {
		this.materia = new ArrayList();
	}

	public Vocabolario(String string) {
		this.materia = new ArrayList();
		this.nome = string;
	}

	public List getListMateria() {
		return this.materia;
	}

	// public void setMateria(Materia[] materia) {
	// this.materia = materia;
	// }

	public void addMateria(Materia materia) {
		this.materia.add(materia);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * return the name of {@link Vocabolario}
	 */
	public String toString() {
		return this.getNome();
	}

	/**
	 * @param index
	 *            of materia
	 * @return the materia at index
	 */
	public Materia getMateria(int index) {
		return (Materia) this.materia.get(index);
	}

	/**
	 * @param string
	 *            to add a {@link Materia}
	 */
	public void addMateria(String string) {
		Materia materia2 = new Materia();
		materia2.setNome(string);
		this.materia.add(materia2);
	}

	/**
	 * @return a list of name of set of {@link Materia}
	 */
	public String[] getMaterie() {
		String[] list = new String[this.materia.size()];
		for (int i = 0; i < this.materia.size(); i++) {
			Materia tmpMateria = (Materia) this.materia.get(i);
			list[i] = tmpMateria.getNome();
		}
		return list;
	}

	/**
	 * add a set of materia from an array of strings
	 * 
	 * @param strings
	 *            array to set
	 */
	public void setMaterie(String[] strings) {
		this.materia.clear();
		strings = adaptList(strings);
		for (int i = 0; i < strings.length; i++) {
			String string = strings[i];
			addMateria(string);
		}
	}
	
	/**
	 * @param list to adapt to a list of {@link Materia}
	 * @return a list adapted.
	 */
	public String[] adaptList(String[] list) {
		String[] newList = new String[list.length];
		for (int i = 0; i < list.length; i++) {
			
			if (list[i] instanceof String) {
				String string = (String) list[i];
				
				newList[i]= string;
			} else {
				newList[i]= list[i];
			}
		}
		return newList;	
	}

	/**
	 * Remove one {@link Materia} from specific {@link Vocabolario}
	 * 
	 * @param materia
	 * @return list of {@link Materia}
	 */
	public String[] removeMateria(Materia materia) {
		this.materia.remove(materia);

		String[] list = new String[this.materia.size()];
		for (int i = 0; i < this.materia.size(); i++) {
			Materia tmpMateria = (Materia) this.materia.get(i);
			list[i] = tmpMateria.getNome();
		}
		return list;

	}

	/**
	 * return an instance of {@link Predicate}. This instance implements the
	 * method {@link Predicate#evaluate(Object)} on nomeVocabolario.<br />
	 * Example:<br />
	 * <code>predicate.equalse(vocabolario) = true iff
	 * nomeVocabolario.equals(vocabolario.getNome())</code>
	 * 
	 * @param nomeVocabolario
	 * @return
	 */
	public static Predicate getPredicateByName(final String nomeVocabolario) {
		Predicate predicate = new Predicate() {

			public boolean evaluate(Object arg0) {
				if (arg0 instanceof Vocabolario) {
					Vocabolario tmpVocabolario = (Vocabolario) arg0;
					return nomeVocabolario.equals(tmpVocabolario.getNome());
				}
				return false;
			}
		};
		return predicate;
	}

}
