package it.cnr.ittig.xmleges.editor.services.dalos.kb;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.TreeOntoClass;

import java.util.Collection;


/**
 * Servizio per la gestione delle Knowledge Base DALOS
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:l.bacci@ittig.cnr.it">Lorenzo Bacci</a>
 */
public interface KbManager extends Service {
	
	


	/**
	 * 
	 * @param lang
	 */
	public void addLanguage(String lang);
	
	/**
	 * 
	 * @param syn
	 */
	public void addLexicalProperties(Synset syn); 
	
	/**
	 * 
	 * @param syn
	 */
	public void addSemanticProperties(Synset syn); 
	
	/**
	 * 
	 * @param syn
	 */
	public void addSources(Synset syn);
	
	/**
	 * 
	 * @return
	 */
	public SynsetTree getTree(String lang);
	
	/**
	 * 
	 * @return
	 */
	public Collection getSynsetsList(String lang);
	
	public Collection getSynset(TreeOntoClass toc);
	
	/**
	 * Utile per ottenere l'oggetto synset a partire dalla URI 
	 * presente in un testo marcato.
	 * 
	 * @param uri 
	 * @return l'oggetto synset relativo alla uri specificata
	 */
	public Synset getSynset(String uri);
	
	/**
	 * Utile per traduzione del termine.
	 * 
	 * @param syn oggetto di partenza
	 * @param lang lingua di destinazione
	 * @return il relativo synset nella lingua specificata
	 */
	public Synset getSynset(Synset syn, String lang);
	
	/**
	 * 
	 * @param search
	 * @param type
	 * @param lang
	 * @return
	 */
	public Collection search(String search, String type, String lang);
	
	/**
	 * 
	 * @param syn
	 * @param lang
	 * @return
	 */
	public void setTreeSelection(Synset syn);
	
	
	public boolean isLangSupported(String lang);
	
}
