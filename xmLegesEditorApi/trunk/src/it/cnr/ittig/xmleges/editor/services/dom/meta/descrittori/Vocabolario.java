package it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori;

public class Vocabolario {
	String nome;
	String[] materie;
	public String[] getMaterie() {
		return materie;
	}
	public void setMaterie(String[] materie) {
		this.materie = materie;
	}
	public void addMateria(String materia) {
		if (materie==null) {
			this.materie = new String[1];
			this.materie[0]= materia;
			return;
		}
		boolean presente = false;
		for (int i=0; i<materie.length; i++)
			if (materie[i].equals(materia)) {
				presente = true;
				break;
			}
		if (!presente) {
			String[] nuoveMaterie = new String[materie.length+1];
			nuoveMaterie[0]=materia;
			for (int i=0; i<materie.length; i++)
				nuoveMaterie[i+1]=materie[i];
			this.materie = nuoveMaterie;
		}
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String toString() {
		return nome;
	}
	
}
