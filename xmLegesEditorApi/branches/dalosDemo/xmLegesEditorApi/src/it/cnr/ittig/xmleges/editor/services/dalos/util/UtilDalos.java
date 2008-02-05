package it.cnr.ittig.xmleges.editor.services.dalos.util;

import it.cnr.ittig.services.manager.Service;


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
public interface UtilDalos extends Service {
	
	public final static String IT = "IT";

	public final static String EN = "EN";

	public final static String NL = "NL";

	public final static String ES = "ES";
	
	//public final  String[] Lang = {IT,EN,NL,ES};
	
	/**
	 * 
	 * @return
	 */
	public LangPanel getLanguageSwitchPanel();
	
	
	/**
	 * 
	 * @return
	 */
	public String getGlobalLang();
	
	

}
