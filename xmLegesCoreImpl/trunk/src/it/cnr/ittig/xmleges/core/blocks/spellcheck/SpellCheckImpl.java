package it.cnr.ittig.xmleges.core.blocks.spellcheck;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheck;
import it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheckWord;
import it.cnr.ittig.xmleges.core.services.threads.ThreadManager;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Node;

import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.xmlmind.spellcheck.engine.SpellChecker;
import com.xmlmind.spellcheck.engine.SpellException;
import com.xmlmind.spellcheck.util.CharSequence;


/**
 * Implementazione del servizio per la gestione del controllo ortografico del testo.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class SpellCheckImpl implements SpellCheck, Loggable, SpellCheckListener, Serviceable, Initializable {

	Logger logger;

	PreferenceManager preferenceManager;

	I18n i18n;
	
	//Attenzione MODIFICARE - MODIFICARE - MODIFICARE
	//Attenzione MODIFICARE - MODIFICARE - MODIFICARE
	//Attenzione MODIFICARE - MODIFICARE - MODIFICARE
	//Attenzione MODIFICARE - MODIFICARE - MODIFICARE
	private static String dictPath = "c:/eclipse/workspace/xmLegesEditor/temp/it/"; 
	
	
	private static String encoding = "ISO-8859-1"; 

	private static String dictFile = "it.dic";

	private static String phonetFile = null;

	private SpellChecker spellCheck = null;

	private SpellChecker checker=null;
	
    //private SpellDictionaryHashMap dictionary = null;

	//private SpellDictionaryASpell dictionary = null;
	
	private List suggestions;

	private Vector invalidWordsVect;

	private String testo;

	ThreadManager threadManager;

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		threadManager = (ThreadManager) serviceManager.lookup(ThreadManager.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	public void initialize() throws Exception {

		// FIXME per il momento evito di caricare il dizionario all'avvio visto che prende
		// molta memoria

		// threadManager.execute(new Runnable(){
		// public void run(){
		// try{
		// dictionary = new SpellDictionaryHashMap(new
		// InputStreamReader(getClass().getResourceAsStream(dictFile)));
		// }
		// catch(IOException e){
		// logger.error(e.getMessage(),e);
		// }
		// }
		// });
	}

	public SpellCheckWord[] spellCheck(String text) {

		try {
			if (null==checker || null != checker){	
				//UtilFile.copyFileInTemp(getClass().getResourceAsStream("it_wl.dic"),"it_wl.dic");
				
				// azcheck checker basato su italian.cdi
				
				//UtilFile.copyFileInTemp(getClass().getResourceAsStream("italian.cdi"),"italian.cdi");
				
				
				//checker = new SpellChecker(UtilFile.getFileFromTemp(new File("italian.cdi")).getAbsolutePath());
				checker = new SpellChecker(dictPath);
				//checker.getDictionaryManager().setAccessPath(dictPath);
				
				//try{
				//checker.selectDictionary(UtilFile.getFileFromTemp(new File("italian.cdi")).getAbsolutePath());
				try{
					//checker.getDictionaryManager().defaultDictionaries();
					
					
//	in realtà qui risetto il vocabolario ogni volta che ci passo, ovvero per ogni nodo
//  il problema è il controllo in alto che fa entrare sempre ...... aggiustare POI					
					
					
					//FORSE INUTILE (provare poi a levare)
					//FORSE INUTILE (provare poi a levare)			
					checker.selectDictionary("it/default");
					
					checker.setSelectedLanguage("it");
				}
				catch(SpellException ex){
					System.err.println(ex.getMessage());
				}
				System.err.println("---Vocabolario caricato---"+checker.getSelectedLanguage()+"---");//.setSelectedLanguage("it");    // ?
				//}
				//catch(SpellException ex){
				//	System.err.println(ex.getMessage());
				//}
				//dictionary = new SpellDictionaryHashMap(new InputStreamReader(getClass().getResourceAsStream("it_wl.dic")),new InputStreamReader(getClass().getResourceAsStream("it_affix.dat")));
				//dictionary = new SpellDictionaryHashMap(new InputStreamReader(getClass().getResourceAsStream(i18n.getTextFor("spellcheck.dictionary"))));
			}
			} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
			
			
//		try{
//			checker.getSuggestions("abitao");
//		}
//		catch(SpellException ex){
//			System.err.println(ex.getMessage());
//		}
			
		
		
		if (text != null) {
				
			this.testo = text;
			invalidWordsVect = new Vector();
			
			return doSearch();
			
//			//spellCheck.checkSpelling(new StringWordTokenizer(text));
//
//			SpellCheckWord[] ret = new SpellCheckWord[invalidWordsVect.size()];
//			invalidWordsVect.copyInto(ret);
//			return ret;
		}
		return null;
	}
	
	
	private SpellCheckWord[] doSearch() {

// 	//Gerardo debug
//	System.err.println("FRASE sotto esame: " + testo);
//	try {
//	 StringTokenizer esamina = new StringTokenizer(testo);
//     while (esamina.hasMoreTokens()) {	
//    	 String parola = esamina.nextToken();
//    	 System.err.println("PAROLA sotto esame: " + parola);
    	 
		
		Vector listaparole = new Vector();
    	 
		for(;;)
		{
			// prepare
//			acquire next fragment from application:
			//GERARDO checker.setInput(parola);
			checker.setInput(testo);
			try{
			int err = checker.checkNext();
			
			if (err == SpellChecker.ERR_NONE) {
//				end reached: update position in source
				
				break;
			}
			String failingWord = checker.getWord();
			int replacePos = checker.getPosition();
			int replaceSize = failingWord.length();

			switch(err) {
			case SpellChecker.ERR_DUPLICATE:
				System.err.println("Parola duplicata: " + failingWord);
				break;
			case SpellChecker.ERR_REPLACE:
				System.err.println(failingWord+" <- sostituita con -> " + checker.getReplacement(failingWord));
				continue;
			case SpellChecker.ERR_WRONG_CAP:
				System.err.println("ERR.maiuscola");
				break;
			case SpellChecker.ERR_PUNCTUATION:
				System.err.println("ERR.punteggiatura");
				break;
			case SpellChecker.ERR_UNKNOWN_WORD:
				System.err.println("NON RICONOSCO: " + failingWord);
				
				String[] suggerimenti = checker.getSuggestions().toArray();
				
//				for (int j=0; j<suggerimenti.length; j++)
//				  System.err.print(suggerimenti[j]+", ");
//				  System.err.println("");
				
				
				listaparole.add(new SpellCheckWordImpl(failingWord, replacePos, replacePos+replaceSize));
   			    
				break;
			}
//			get and process user commands:
			break;
			}
			catch(SpellException ex){
				System.err.println(ex.getMessage());
			}
		}
		
		SpellCheckWord[] ret = new SpellCheckWord[listaparole.size()];
		listaparole.copyInto(ret);
		
		//return listaparole;
		return ret;
 	   
		
		
//		//finale del debug
//        }//chiude il while
//       } 
// 	   catch(ClassCastException ex){
// 	    System.err.println(ex.getMessage());
// 	   } //fine debug gerardo
 	   
 	      
	
	}
	

	public void spellingError(SpellCheckEvent event) {

		int startOffset = event.getWordContextPosition();
		int endOffset = startOffset + event.getInvalidWord().length();
		// int startOffset = testo.indexOf(event.getInvalidWord());
		// int endOffset = startOffset+event.getInvalidWord().length();
		if (event.getInvalidWord().trim().length() > 1)
			invalidWordsVect.add(new SpellCheckWordImpl(event.getInvalidWord(), startOffset, endOffset));
	}

	public String[] getSuggestions(String word) {
		
	 if (word != null) {
	  try {
		
		checker.setInput(word);  
		
		int err = checker.checkNext();
		if (err == SpellChecker.ERR_NONE) return null;
		switch(err) {	
			   case SpellChecker.ERR_UNKNOWN_WORD: 	   //potrei testare anche gli altri casi
		
				   
		       String[] suggerimenti = checker.getSuggestions().toArray();
			    
  		       return suggerimenti;
		     
		} //chiudo lo switch
		
	  } catch(SpellException ex){}
	 }		
     return null;

	}

	public String[] getDictionaries() {
		// TODO getDictionaries
		return null;
	}

	public void addDictionary(String name) {
		// TODO addDictionary
	}

	public void removeDictionary(String name) {
		// TODO removeDictionary
	}

	public void setCustomDictionary(String name) {
		// TODO setCustomDictionary
	}

	public void addWord(String word) {
		// TODO addWord
	}

	public void removeWord(String word) {
		// TODO removeWord
	}

	public void modifyWord(String oldWord, String newWord) {
		// TODO modifyWord
	}

}
