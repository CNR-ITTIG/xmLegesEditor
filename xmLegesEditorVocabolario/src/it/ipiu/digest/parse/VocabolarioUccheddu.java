package it.ipiu.digest.parse;

import java.util.List;
import java.util.Vector;


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
public class VocabolarioUccheddu {

	private String nome;

	private List materia;

	public VocabolarioUccheddu() {
		materia = new Vector();
	}

	public List getMateria() {
		return materia;
	}


	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(System.getProperty("line.separator"));
		buffer.append("Vocabolario= ").append(nome);
		return buffer.toString();
	}
	
	/**
	 * add materia at vocabolario
	 * @param materia to add.
	 */
	public void addMateria(String materia) {
		this.materia.add(materia);
	}

}
