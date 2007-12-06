/**
 * 
 */
package it.cnr.ittig.xmleges.editor.blocks.form.xmleges.linker;

import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;

import java.util.Hashtable;

/**
 * 
 * <hr>
 * Project 	: xmLegesEditorImpl<br>
 * package 	: it.cnr.ittig.xmleges.editor.blocks.form.xmleges.linker<br>
 * Type Name : <b>ArchivioLocalizzazioni</b><br>
 * Comment	:<br>
 * La classe immagazzina le localizzazioni in una HashTable
 * utilizzando come chiave il nome della localizzazione
 * <hr>
 * I+ S.r.l. 03/dic/07<br>
 * <hr>
 * @author Macchia<br>
 * <hr>
 * 
 */
public class ArchivioLocalizzazioni {

	private Hashtable archivio;
	private Localizzazione localizzazione;
	private Ente ente;
	Configuration[] elencoConf;
	
	
	public ArchivioLocalizzazioni() {
		this.archivio = new Hashtable();
	}

	public Hashtable getArchivio() {
		return archivio;
	}

	/**
	 * Aggiunge una Localizzazione all'archivio. 
	 * Associa un nome(chiave) all'oggetto localizzazione (valore)
	 * @param nome
	 * @param loc
	 */
	public void addLocalizzazione (String nome, Localizzazione loc){
		this.archivio.put(nome, loc);
	}

	/**
	 * Tramite il configuration recupera le localizzazioni dal file XML rae.xml
	 * e riempie l'hashTable
	 * @param configuration
	 * @return archivio
	 * @throws ConfigurationException
	 */
	public Hashtable load(Configuration configuration) throws ConfigurationException {
		Configuration[] elencoConf = configuration.getChildren();
	
		for (int i = 0; i < elencoConf.length; i++){
			if(elencoConf[i].getName().toLowerCase().startsWith("localizzazion")){
				localizzazione = new Localizzazione(elencoConf[i].getAttribute("name"));
				localizzazione.setUrn(elencoConf[i].getAttribute("urn"));
				Configuration[] entiConf = elencoConf[i].getChildren();
				for (int k = 0; k < entiConf.length; k++) {
					ente = new Ente();
					ente.setNome(entiConf[k].getAttribute("name"));	
					ente.setUrn(entiConf[k].getAttribute("urn"));
					localizzazione.addEnti(ente);
				}
				archivio.put(elencoConf[i].getAttribute("name"), localizzazione);
			}

		}
		return archivio;
	}
}
