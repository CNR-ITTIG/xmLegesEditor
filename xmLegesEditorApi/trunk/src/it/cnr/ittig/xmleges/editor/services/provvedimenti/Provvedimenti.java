package it.cnr.ittig.xmleges.editor.services.provvedimenti;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la lettura da file dei tipi di provvedimento gestibili
 * 
 * @version 1.0
 * @author Lorenzo Sarti
 */

public interface Provvedimenti extends Service {

	/**
	 * @param nomeclasse nome della classe da leggere dal file di
	 *        configurazione
	 * @return classe letta
	 */
	public ClasseItem getClasseByName(String nomeclasse);

	/**
	 * @return vettore contenente tutte le classi di provvedimento
	 */
	public ClasseItem[] getAllClassi();

	/**
	 * @param nomeprovvedimento nome del provvedimento in base al quale si
	 *        effettua l'interrogazione sul file di configurazione
	 * @return provvedimento letto, se trovato.
	 */
	public ProvvedimentiItem getProvvedimentoByName(String nomeprovvedimento);

	/**
	 * @param nometag nome del tag in base al quale si effettua
	 *        l'interrogazione sul file di configurazione
	 * @return lista dei provvedimenti che soddisfano il criterio di ricerca
	 *         specificato
	 */
	public ProvvedimentiItem[] getProvvedimentiByTag(String nometag);

	/**
	 * @return restituisce tutti i provvedimenti. Attenzione: in questo modo si
	 *          perde la suddivisione in classi
	 */
	public ProvvedimentiItem[] getAllProvvedimenti();

	/**
	 * @param urnatto urn in base alla quale ricercare all'interno del file di
	 *        configurazione
	 * @return lista di provvedimenti che soddisfano il criterio di ricerca
	 *         specificato
	 */
	public ProvvedimentiItem getProvvedimentoByUrn(String urnatto);

}
