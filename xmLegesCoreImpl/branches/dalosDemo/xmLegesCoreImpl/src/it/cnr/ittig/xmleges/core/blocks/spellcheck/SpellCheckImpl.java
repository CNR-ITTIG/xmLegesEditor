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
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.File;
import java.util.Vector;

import com.xmlmind.spellcheck.engine.SpellChecker;
import com.xmlmind.spellcheck.engine.SpellException;




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
public class SpellCheckImpl implements SpellCheck, Loggable, Serviceable, Initializable {

	boolean libreriaCaricata;
	
	Logger logger;

	UtilMsg utilMsg;
	
	PreferenceManager preferenceManager;

	I18n i18n;
	
	private static String dictPath = UtilFile.getTempDirName()+"/it";
	
	private static String dictName = "it.dar";
	
	private SpellChecker checker=null;
	
	private String testo;

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		//threadManager = (ThreadManager) serviceManager.lookup(ThreadManager.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	public void initialize() throws Exception {
		
		// copia dictName in dictPath se non esiste già
		new File(dictPath).mkdir();
	    File dictFile = new File(dictPath + File.separator,dictName);
	    if(!dictFile.exists()){
		   UtilFile.copyFile(getClass().getResourceAsStream(dictName),dictFile);
		   UtilFile.copyFile(getClass().getResourceAsStream("language"),new File(dictPath + File.separator,"language"));
		   UtilFile.copyFile(getClass().getResourceAsStream("default"),new File(dictPath + File.separator,"default"));
	    }
		
		if (null==checker){
		  try{	
			checker = new SpellChecker(dictPath);					
			try{	//Intercetto se è scaduta la demo della libreria "xsc.jar"
			   checker.setPersonalDictionaryPath("dizionarioUtente_%L%.txt");
			   checker.setSelectedLanguage("it");	
			}
			catch(SpellException ex){
			   logger.error(ex.getMessage(),ex);
			}
		    logger.debug("---Vocabolario caricato---"+checker.getSelectedLanguage()+"---");
		    libreriaCaricata = true;
		  }
		  catch(Exception ex){
			   logger.error(ex.getMessage(),ex);
			   //utilMsg.msgInfo("spellcheck.error.library");
			   libreriaCaricata = false;
		  }
		}
	}

	
	public SpellCheckWord[] spellCheck(String text) {	
		if (text != null) {
			this.testo = text;
			return doSearch();
		}
		return null;
	}
	
	
	private SpellCheckWord[] doSearch() {
		
      Vector listaparole = new Vector();		
      if (null!=checker) {
        boolean controlla = true;
	    try{
		checker.setInput(testo);
		int err = checker.checkNext();
		int offsetparziale = 0;
		while (controlla) {			
			if (err == SpellChecker.ERR_NONE) {
			    controlla = false;
				break;
			}
			String failingWord = checker.getWord();
			int replacePos = checker.getPosition();
			int replaceSize = failingWord.length();

			switch(err) {
			case SpellChecker.ERR_DUPLICATE:
			case SpellChecker.ERR_REPLACE:
			case SpellChecker.ERR_WRONG_CAP:
			case SpellChecker.ERR_PUNCTUATION:
				offsetparziale = offsetparziale+replacePos+replaceSize+1;
				if (offsetparziale <= testo.length()) {
					checker.setInput(testo.substring(offsetparziale,testo.length()));
					err = checker.checkNext();
				}
				else controlla = false;
				break;
			case SpellChecker.ERR_UNKNOWN_WORD:
				
				//GESTIONE REGOLE:
				boolean aggiungi = false;
				//regola per l'apostrofo
				if (failingWord.indexOf("'")!=-1) {
					 
				    String[] parti = failingWord.split("'");
				    String nuovaParola = parti[0];
				    for (int j=1; j<parti.length; j++) 
				        nuovaParola.concat("' ").concat(parti[j]);
				    if (getSuggestions(nuovaParola)!=null) aggiungi=true;  
				}
				else aggiungi=true;
				
				if (aggiungi) listaparole.add(new SpellCheckWordImpl(failingWord, offsetparziale+replacePos, offsetparziale+replacePos+replaceSize));

				
				offsetparziale = offsetparziale+replacePos+replaceSize+1;

				if (offsetparziale <= testo.length()) {
					checker.setInput(testo.substring(offsetparziale,testo.length()));
					err = checker.checkNext();
				}
				else controlla = false;
				break;
			}
		}
	    }
		catch(SpellException ex){
			System.err.println(ex.getMessage());
		}
      }
      SpellCheckWord[] ret = new SpellCheckWord[listaparole.size()];
	  listaparole.copyInto(ret);
		
	  return ret;
	}
	
	public String[] getSuggestions(String word) {
		
	 if (word != null) {
	  try {
		
		checker.setInput(word);  
		
		int err = checker.checkNext();
		if (err == SpellChecker.ERR_NONE) return null;
		switch(err) {	
			   case SpellChecker.ERR_UNKNOWN_WORD:
		
		       String[] suggerimenti = checker.getSuggestions().toArray();	    
  		       return suggerimenti;
		     
		}
		
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

	public void addWord(String word, boolean temporaryDict) {
		// TODO addWord
	}
	
	public void addSuggestion(String word, String suggestion, boolean temporaryDict) {		
		
		try {		
		
		  String dictionary = null;
		  if (temporaryDict) dictionary = SpellChecker.TEMPORARY_DICT;
		  else dictionary = SpellChecker.PERSONAL_DICT;		
	      //se la parola ERRATA è corretta con una NON PRESENTE fra i suggerimenti
	      if (!isSuggestion(word, suggestion)) {
	             //Inserisce la parola errata e il suggerimento per la parola impostato
		         checker.learnSuggestion(word,suggestion,dictionary);
		         //Inserisce la parola suggerita come giusta fra quelle giuste
		         checker.learnWord(suggestion,dictionary);
		         
		         
   		         
		         //Salva il dizionario utente (QUESTO può essere MESSO ALTROVE)
		         if (!temporaryDict) checker.savePersonalDictionaries();
		  }    
		     
		      
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private boolean isSuggestion(String word, String suggestion) {
		
		  try {
			
			checker.setInput(word);  
			
			int err = checker.checkNext();
			if (err == SpellChecker.ERR_NONE) return false;
			switch(err) {	
				   case SpellChecker.ERR_UNKNOWN_WORD:
			
			       String[] suggerimenti = checker.getSuggestions().toArray();
			       for (int j=0; j<suggerimenti.length; j++)
			    	   if (suggestion.equals(suggerimenti[j])) return true;
	  		       return false;
			}
			
		  } catch(SpellException ex){}
		  return false;
	}

	public void removeWord(String word) {
		// TODO removeWord
	}

	public void modifyWord(String oldWord, String newWord) {
		// TODO modifyWord
	}

	public boolean isLoad() {
		return libreriaCaricata;
	}
}
