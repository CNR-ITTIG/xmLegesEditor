package it.cnr.ittig.xmleges.core.blocks.spellcheck.dom.form;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheck;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheckWord;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.form.SpellCheckForm;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.w3c.dom.Node;

import com.jeta.forms.components.image.ImageComponent;

/**
 * Implementazione della form controllo ortografico
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:t.paba@onetech.it">Tommaso Paba </a>
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 * 
 */
public class SpellCheckFormImpl implements SpellCheckForm, Loggable, Serviceable, Initializable, ActionListener, ListSelectionListener {

	private static final int ICON_NONE = 0;

	private static final int ICON_CORRECT = 1;

	private static final int ICON_WRONG = 2;

	Logger logger;

	Form form;

	EventManager eventManager;

	I18n i18n;

	JLabel originalWordLabel;

	JTextField wordTextField;

	JList suggestionsList;

	JButton checkButton;

	JButton ignoreButton;

	JButton ignoreAllButton;

	JButton replaceButton;

	JButton replaceAllButton;

	// TODO addButton
	// JButton addButton;

	SelectionManager selectionManager;

	DomSpellCheck domSpellCheck;

	DocumentManager documentManager;

	UtilMsg utilMsg;

	int misspelledIndex = 0;

	DomSpellCheckWord[] words = null;

	Node activeNode;

	Vector ignored = new Vector();

	Vector ignoredAll = new Vector();

	Vector inserted = new Vector();

	EditTransaction tr;

	public void enableLogging(Logger logger) {
		this.logger = logger;

	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		domSpellCheck = (DomSpellCheck) serviceManager.lookup(DomSpellCheck.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	public void initialize() throws Exception {
		form.setMainComponent(getClass().getResourceAsStream("SpellCheck.jfrm"));
		form.setName("spellcheck");

		originalWordLabel = (JLabel) form.getComponentByName("spellcheck.label.originalword");
		wordTextField = (JTextField) form.getComponentByName("spellcheck.textfield.word");
		suggestionsList = (JList) form.getComponentByName("spellcheck.list.suggestions");
		suggestionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		suggestionsList.addListSelectionListener(this);

		checkButton = (JButton) form.getComponentByName("spellcheck.button.check");
		checkButton.addActionListener(this);
		ignoreButton = (JButton) form.getComponentByName("spellcheck.button.ignore");
		ignoreButton.addActionListener(this);
		ignoreAllButton = (JButton) form.getComponentByName("spellcheck.button.ignoreall");
		ignoreAllButton.addActionListener(this);
		replaceButton = (JButton) form.getComponentByName("spellcheck.button.replace");
		replaceButton.addActionListener(this);
		replaceAllButton = (JButton) form.getComponentByName("spellcheck.button.replaceall");
		replaceAllButton.addActionListener(this);
		// TODO addButton
		// addButton =
		// (JButton)form.getComponentByName("spellcheck.button.add");
		// addButton.addActionListener(this);

		setOriginalWord("");
		setStatusIcon(ICON_NONE);
	}

	public boolean openForm() {

		int start = selectionManager.getTextSelectionStart();
		int end = selectionManager.getTextSelectionEnd();
		activeNode = selectionManager.getActiveNode();
		// String word;

		if (ignored != null)
			logger.debug("ignored size: " + ignored.size());
		else
			logger.debug("ignored vector null");

		if (ignoredAll != null)
			logger.debug("ignoredAll size: " + ignoredAll.size());
		else
			logger.debug("ignoredAll  vector null");

		// SpellCheck su testo selezionato
		if (start != end)
			words = domSpellCheck.spellCheck(activeNode, start, end);
		else
			words = domSpellCheck.spellCheck(documentManager.getRootElement());

		if (null != words && words.length > 0 && misspelledIndex != -1) {
			misspelledIndex = showWord(words);
		} else {
			utilMsg.msgInfo("spellcheck.error.spellcheckend");
			return false;
		}

		// form.setSize(680, 450);
		form.showDialog();
		ignored = new Vector();
		ignoredAll = new Vector();
		inserted = new Vector();
		misspelledIndex = 0;

		if (form.isOk()) {
			try {
				if (tr != null)
					documentManager.commitEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
			}
		} else {
			if (tr != null)
				documentManager.rollbackEdit(tr);
		}
		selectionManager.setSelectedText(this, activeNode, start, start);
		return form.isOk();
	}

	private boolean isIgnored(DomSpellCheckWord word) {

		int i = 0;
		while (i < ignored.size()) {
			if ((word.getSpellCheckWord().getWord().equals(((DomSpellCheckWord) ignored.get(i)).getSpellCheckWord().getWord()))
					&& (word.getSpellCheckWord().getStartOffset() == ((DomSpellCheckWord) ignored.get(i)).getSpellCheckWord().getStartOffset())
					&& (word.getSpellCheckWord().getEndOffset() == ((DomSpellCheckWord) ignored.get(i)).getSpellCheckWord().getEndOffset()))
				return true;
			i++;
		}
		return false;
	}

	private boolean isIgnoredAll(DomSpellCheckWord word) {

		int i = 0;
		while (i < ignoredAll.size()) {
			if ((word.getSpellCheckWord().getWord().equals(((DomSpellCheckWord) ignoredAll.get(i)).getSpellCheckWord().getWord())))
				return true;
			i++;
		}
		return false;
	}

	private boolean isInserted(DomSpellCheckWord word) {

		int i = 0;
		while (i < inserted.size()) {
			logger.debug("inserted:  " + (String) inserted.get(i));
			if ((word.getSpellCheckWord().getWord().equals((String) inserted.get(i))))
				return true;
			i++;
		}
		return false;
	}

	private int showWord(DomSpellCheckWord[] word) {
		int i = 0;
		do {
			if (!isIgnored(word[i]) && !isIgnoredAll(word[i]) && !isInserted(word[i])) {
				String[] suggestions = domSpellCheck.getSpellCheck().getSuggestions(word[i].getSpellCheckWord().getWord());
				if (null != suggestions && suggestions.length > 0) {
					logger.debug("ci sono suggestions: size > 0 ");
					logger.debug("showWord: sggestions list >0 : " + suggestions.length);
					suggestionsList.setListData(suggestions);
				} else {
					logger.debug("showWord: sggestions list null");
					suggestionsList.setListData(new Vector());
				}
				logger.debug("showWord " + word[i].getSpellCheckWord().getWord());
				originalWordLabel.setText(word[i].getSpellCheckWord().getWord());
				wordTextField.setText(word[i].getSpellCheckWord().getWord());
				selectionManager.setSelectedText(this, word[i].getNode(), word[i].getSpellCheckWord().getStartOffset(), word[i].getSpellCheckWord()
						.getEndOffset());
				return i;
			}
			i++;
		} while (i < word.length);
		return (-1);
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == checkButton) {
			logger.debug("check word");
			this.setSuggestions(domSpellCheck.getSpellCheck().getSuggestions(wordTextField.getText()));
		} else if (evt.getSource() == ignoreButton) {
			logger.debug("ignore");

			if (misspelledIndex != -1 && misspelledIndex < words.length) {
				ignored.add(words[misspelledIndex]);
				misspelledIndex = showWord(words);
				if (misspelledIndex == -1)
					utilMsg.msgInfo("spellcheck.error.spellcheckend");
			} else {
				utilMsg.msgInfo("spellcheck.error.spellcheckend");
			}
		} else if (evt.getSource() == ignoreAllButton) {
			logger.debug("ignore all");

			if (misspelledIndex != -1 && misspelledIndex < words.length) {
				ignoredAll.add(words[misspelledIndex]);
				misspelledIndex = showWord(words);
				if (misspelledIndex == -1)
					utilMsg.msgInfo("spellcheck.error.spellcheckend");
			} else {
				utilMsg.msgInfo("spellcheck.error.spellcheckend");
			}
		} else if (evt.getSource() == replaceButton) {
			logger.debug("replace on text");

			if (misspelledIndex != -1 && words.length > 0)
				replaceWord(words[misspelledIndex]);

			words = domSpellCheck.spellCheck(activeNode);
			if (misspelledIndex != -1 && words.length > 0) {
				misspelledIndex = showWord(words);
				if (misspelledIndex == -1)
					utilMsg.msgInfo("spellcheck.error.spellcheckend");
			} else {
				utilMsg.msgInfo("spellcheck.error.spellcheckend");
			}

			for (int i = 0; i < words.length; i++) {
				logger.debug("after replacement misspelled words: " + i + " " + words[i].getSpellCheckWord().toString());
			}
		} else if (evt.getSource() == replaceAllButton) {

			logger.debug("replace all");
			logger.debug("replace on text");

			if (misspelledIndex != -1 && words.length > 0)
				replaceAllWord(words[misspelledIndex]);

			words = domSpellCheck.spellCheck(activeNode);
			if (misspelledIndex != -1 && misspelledIndex < words.length) {
				misspelledIndex = showWord(words);
				if (misspelledIndex == -1)
					utilMsg.msgInfo("spellcheck.error.spellcheckend");
			} else {
				utilMsg.msgInfo("spellcheck.error.spellcheckend");
			}

			for (int i = 0; i < words.length; i++) {
				logger.debug("after replacementall misspelled words: " + i + " " + words[i].getSpellCheckWord().toString());
			}
		}
		// TODO addButton
		// else if(evt.getSource() == addButton) {
		// logger.debug("add word");
		// }
	}

	private void replaceWord(DomSpellCheckWord word) {

		// Document doc = documentManager.getDocumentAsDom();

		String oldText = word.getNode().getNodeValue();
		logger.debug("oldText:  " + oldText);
		logger.debug("node " + UtilDom.getPathName(word.getNode()));
		logger.debug("startOffset: " + word.getSpellCheckWord().getStartOffset());
		logger.debug("endOffset: " + word.getSpellCheckWord().getEndOffset());
		logger.debug("replace with " + this.getWord());

		String newText = oldText.substring(0, word.getSpellCheckWord().getStartOffset()) + this.getWord()
				+ oldText.substring(word.getSpellCheckWord().getEndOffset());
		logger.debug("newText " + newText);

		inserted.add(this.getWord());

		try {
			tr = documentManager.beginEdit();
			word.getNode().setNodeValue(newText);
			// selectionManager.setSelectedText(this,word.getNode(),word.getSpellCheckWord().getStartOffset(),word.getSpellCheckWord().getStartOffset()+this.getWord().length());
			documentManager.commitEdit(tr);
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void replaceAllWord(DomSpellCheckWord word) {

		// Document doc = documentManager.getDocumentAsDom();

		String oldText = word.getNode().getNodeValue();
		logger.debug("oldText:  " + oldText);
		logger.debug("node " + UtilDom.getPathName(word.getNode()));
		logger.debug("startOffset: " + word.getSpellCheckWord().getStartOffset());
		logger.debug("endOffset: " + word.getSpellCheckWord().getEndOffset());
		logger.debug("replace with " + this.getWord());

		String oldWord = oldText.substring(word.getSpellCheckWord().getStartOffset(), word.getSpellCheckWord().getEndOffset());

		int startReplace;
		int endReplace;
		String newText;

		while (!oldWord.equals(this.getWord()) && (startReplace = oldText.indexOf(oldWord)) != -1) {
			endReplace = startReplace + oldWord.length();
			oldText = oldText.substring(0, startReplace) + this.getWord() + oldText.substring(endReplace);
			logger.debug("replacing all: " + oldText);
		}

		newText = oldText;
		logger.debug("newText " + newText);

		inserted.add(this.getWord());

		try {
			tr = documentManager.beginEdit();
			word.getNode().setNodeValue(newText);
			// selectionManager.setSelectedText(this,word.getNode(),word.getSpellCheckWord().getStartOffset(),word.getSpellCheckWord().getStartOffset()+this.getWord().length());
			documentManager.commitEdit(tr);
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	public String getWord() {
		return wordTextField.getText();
	}

	public void setOriginalWord(String originalWord) {
		originalWordLabel.setText(originalWord);
	}

	public void setSuggestions(String[] suggestions) {
		suggestionsList.setListData(suggestions);
	}

	public void setWord(String word) {
		wordTextField.setText(word);
	}

	public void setStatusIcon(int status) {

		ImageComponent icon = (ImageComponent) form.getComponentByName("spellcheck.image.status");

		switch (status) {
		case (ICON_CORRECT):
			icon.setIcon(i18n.getIconFor("spellcheck.icon.correctword"));
			break;
		case (ICON_WRONG):
			icon.setIcon(i18n.getIconFor("spellcheck.icon.wrongword"));
			break;
		default:
			icon.setIcon(new ImageIcon());
			break;
		}
	}

	public void valueChanged(ListSelectionEvent arg0) {
		wordTextField.setText((String) suggestionsList.getSelectedValue());
	}
}
