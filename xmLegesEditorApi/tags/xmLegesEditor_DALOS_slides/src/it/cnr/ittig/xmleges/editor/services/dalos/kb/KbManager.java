package it.cnr.ittig.xmleges.editor.services.dalos.kb;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;

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
	public Collection getSynsets(String lang);
	
	/**
	 * 
	 * @param uri
	 * @param lang
	 * @return
	 */
	public Synset getSynset(String uri, String lang);
	
	/**
	 * 
	 * @param search
	 * @return
	 */
	public Collection search(String search);
	
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
	public boolean setTreeSelection(Synset syn, String lang);
}
