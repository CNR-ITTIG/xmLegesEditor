package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.panes.xsltmapper.XsltMapper;
import it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheckWord;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheckEvent;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheckWord;
import it.cnr.ittig.xmleges.core.util.clipboard.UtilClipboard;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.xslt.UtilXslt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Classe per il pannello di testo con il testo antialias.
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
 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
 */
public final class AntiAliasedTextPane extends JTextPane implements DocumentListener, CaretListener {

	public final static String HTML_ERROR = "<center><b>ERROR</b></center>";

	public final static Highlighter.HighlightPainter LEAF_SEL = new DefaultHighlighter.DefaultHighlightPainter(new Color(220, 220, 220));

	Highlighter.HighlightPainter DEF_SEL;

	protected Document dom = null;

	protected boolean ignoreCaretEvents = false;

	protected boolean ignoreDocumentEvents = false;

	protected Logger logger;

	protected XsltPaneImpl pane = null;

	protected File xslt = null;

	protected Hashtable xsltParam = null;

	String href = null;

	//  SpellThread spellThread = new SpellThread();

	public AntiAliasedTextPane(XsltPaneImpl xsltPane) {
		super();
		pane = xsltPane;
		logger = pane.getLogger();
		setDoubleBuffered(true);
		setEnabled(false);
		setEditorKit(new XsltEditorKit(this));

		setTransferHandler(new XsltTransferHandler());
		setDragEnabled(true);
		addCaretListener(this);
		HTMLDocument doc = (HTMLDocument)getEditorKit().createDefaultDocument();
		doc.setPreservesUnknownTags(true);
		setDocument(doc);
		setSelectedTextColor(Color.black);
		getDocument().addDocumentListener(this);

		addMouseListener(new XsltMouseAdapter(xsltPane));

		setNavigationFilter(new NavigationFilter(this, xsltPane));

		Color selColor = new Color(170, 170, 250);
		DEF_SEL = new DefaultHighlighter.DefaultHighlightPainter(selColor);
		setSelectionColor(selColor);

		//pane.threadManager.execute(spellThread);
	}

	public String getEncoding() {
		return pane.documentManager.getEncoding();
	}

	public void addStyleSheet(File file) {
		if (file != null && file.isFile() && file.canRead())
			try {
				FileReader fr = new FileReader(file);
				StyleSheet s = getHTMLDocument().getStyleSheet();
				s.loadRules(fr, null);
				fr.close();
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
	}

	// ///////////////////////////////////////////////////////////////// CUT COPY PASTE
	public boolean canCopy() {
		return getSelectedText() != null;
	}

	public void copy() {
		UtilClipboard.set(getSelectedText());
	}

	public boolean canCut() {
		return getSelectedText() != null;
	}

	public void cut() {
		UtilClipboard.set(getSelectedText());
		replaceSelection("");
	}

	public boolean canDelete() {
		return getSelectedText() != null;
	}

	public void delete() {
		replaceSelection("");
	}

	public boolean canPaste() {
		return UtilClipboard.hasString();
	}

	public void paste() {
		Element currElem = getMappedSpan(getCaretPosition());
		Node modNode = getXsltMapper().getDomById(getElementId(currElem));
		if (getXsltMapper().getParentByGen(modNode) != null) {
			select(currElem.getStartOffset(), currElem.getEndOffset());
		}
		replaceSelection(UtilClipboard.getAsString());
	}

	public boolean canPasteAsText() {
		return UtilClipboard.hasNode();
	}

	public void pasteAsText() {
		replaceSelection(UtilClipboard.getAsNode().getNodeValue());
	}

		
	// ///////////////////////////////////////////////////////////////// CUT COPY PASTE

	public synchronized void caretUpdate(CaretEvent e) {
		if (ignoreCaretEvents)
			return;

		if (href != null) {
			String[] browsers = pane.getBrowsers();
			if (href.startsWith("href=#")) {
				String id = href.substring(6).trim();
				Node n = pane.getDocumentManager().getDocumentAsDom().getElementById(id);
				if (n != null) {
					pane.fireActiveNodeChanged(n);
					selectNode(new Node[] { n });
					href = null;
					return;
				}
			} else
				for (int i = 0; i < browsers.length; i++)
					try {
						String cmd = browsers[i] + " " + href.substring(5).trim();
						Runtime.getRuntime().exec(cmd);
						href = null;
						break;
					} catch (Exception ex) {
					}

		}

		int currpos = e.getMark();
		Element currelem = getMappedSpan(currpos);

		String id = getElementId(currelem);

		Node selNode = getXsltMapper().getDomById(id, true);

		removeAllHighlights();
		highlightElement(currelem);

		int selStart = Math.min(e.getDot(), e.getMark());
		int selEnd = Math.max(e.getDot(), e.getMark());

		if (e.getDot() != e.getMark()) {
			logger.debug("caretUpdate: with dot!=mark");
			// Se la selezione si estende su nodi diversi lanciamo l'evento
			// SelectedNodesChanged
			// MIRKO: 
			// L'elemento sotto il caret (o dot o mark) viene individuato
			// in modo "intelligente", quindi se ce n'� solo uno a dx o
			// a sx viene ritornato quello, mentre se ce ne sono due
			// viene in genere ritornato quello di destra (con la conseguenza
			// che non � possibile scrivere alla fine di un elemento che
			// combacia con il successivo).
			// In questo caso siamo pi� interessati a sapere se � possibile
			// che il cursore si trovi sullo stesso elemento (e stranamente
			// il pane sembra non esserlo). Quindi non ci resta che provare
			// tutte le combinazioni.
			Element selDotA = getMappedSpan(e.getDot());
			Element selDotB = getMappedSpan(e.getDot()-1);
			Element selMarkA = getMappedSpan(e.getMark());
			Element selMarkB = getMappedSpan(e.getMark()-1);
			Element selDot = null, selMark = null;
			if(selDotA == selMarkA) 
				selDot = selMark = selDotA;
			else if(selDotA == selMarkB)
				selDot = selMark = selDotA;
			else if(selDotB == selMarkA)
				selDot = selMark = selDotB;
			else 
				{ selDot = selDotA; selMark = selMarkA; }
			if(selMark == null || selDot == null) return;

			int selDotStart = selDot.getStartOffset(), 
				selDotEnd = selDot.getEndOffset();
			int selMarkStart = selMark.getStartOffset(), 
				selMarkEnd = selMark.getEndOffset();
			if(selDot != selMark) {
				int from = Math.min(selDotStart, selMarkStart);
				int to = Math.max(selDotEnd, selMarkEnd);
				Element[] enclosedElems = getEnclosedElements(from, to);
				String[] nodesId = getElementId(enclosedElems);
				Node[] selectedNodes = getXsltMapper().getDomById(nodesId, true);
				Node[] ancestorBros = UtilDom.getCommonBrothers(selectedNodes);
				select(currpos, currpos);
				selectNode(ancestorBros);
				pane.fireSelectedNodesChanged(ancestorBros);
				return;
			}
		} else {
			logger.debug("caretUpdate: with dot==mark");
		}

		int relSelStart = selStart  - currelem.getStartOffset();
		int relSelEnd = selEnd - currelem.getStartOffset();

		pane.fireSelectionChanged(selNode, relSelStart, relSelEnd);
		pane.firePaneStatusChanged();
	}

	public void changedUpdate(DocumentEvent e) {
		// non dovrebbe servire monitorare le modifiche agli attributi
		// degli Elementi HMTL
	}


	/** Default text (used to fill empty elements) for the dom node
	 * 	associated to the given element */
	public String getDefaultText(Element e) {
		try {
			Node node = getXsltMapper().getDomById(getElementId(e));
			if (node.getNodeType() == Node.COMMENT_NODE || node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)
				return getXsltMapper().getI18nNodeText(node);
			else
				return getXsltMapper().getI18nNodeText(node.getParentNode());
		} catch (Exception npe) {
			return null;
		}
	}

	/** Returns the string id of the given element (or null) */
	public String getElementId(Element e) {
		if (e == null)
			return null;
		return getIdInAttributeSet(e.getAttributes());
	}

	protected String[] getElementId(Element[] elems) {
		if (elems == null)
			return null;

		String[] retVal = new String[elems.length];

		for (int i = 0; i < elems.length; i++) {
			retVal[i] = getElementId(elems[i]);
		}

		return retVal;
	}

	protected Element[] getEnclosedElements(int from, int to) {
		return getEnclosedElements(from, to, false);
	}

	protected Element[] getEnclosedElements(int from, int to, boolean all) {
		if (from > to) {
			int a = from;
			from = to;
			to = a;
		}
		Vector retVal = new Vector();
		for (int i = from; i < to; i++) {
			Element currElem = getHTMLDocument().getCharacterElement(i);
			try {
				if (!currElem.equals(retVal.lastElement()) && (getIdInAttributeSet(currElem.getAttributes()) != null || all)) {
					retVal.add(currElem);
				}
			} catch (NoSuchElementException nsee) {
				retVal.add(currElem);
			}
		}

		Element[] array = new Element[retVal.size()];
		retVal.copyInto(array);
		return array;
	}
		
	/** Tells mapped elements */
	public static boolean isMappedElement(Element e) {
		return (e != null && isMapped(e));
	}

	/** Gets the mapped span at point (or null)*/
	public Element getMappedSpan(int dot) {
		Element e = getHTMLDocument().getCharacterElement(dot);
		if(e != null && isSpan(e) && isMapped(e))
			return e;
		else
			return null;
	}

	/** Tells mapped elements from elements without a mapped id. */
	protected static boolean isMapped(Element e) 
	{
		AttributeSet as = e.getAttributes();
		String id = null;
		if(as != null) id = getIdInAttributeSet(e.getAttributes());
		if(id != null)
			return id.startsWith("map");
		else
			return false;
	}


	/** Somewhat complicated method to identify span elements. */
	protected static boolean isSpan(Element e) {
		if(e == null) return false;
		if("content".equals(e.getName())) {
			AttributeSet as = e.getAttributes();
			if(as != null) {
				Enumeration en = as.getAttributeNames();
				while(en.hasMoreElements()) {
					Object key = en.nextElement();
					if(key.toString().equals("span")) return true; 
				}
			}
		} 
		return false;
	}	

	public static String getIdInAttributeSet(AttributeSet as) {
		String id = null;
		Enumeration en = as.getAttributeNames();
		while (en.hasMoreElements() && id == null) {
			Object k = en.nextElement();
			Object v = as.getAttribute(k);
			if ("id".equals(k.toString())) 
				id = v.toString();
			else if(v instanceof AttributeSet)
				id = getIdInAttributeSet((AttributeSet)v);
		}
		return id;
	}
	protected String getHtml(Node node) {
		logger.debug("BEGIN getHTML(Node)");
		String ret = HTML_ERROR;
		Node nodeHtml = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("node: " + node);
			if (xsltParam != null){
				//ret = UtilXslt.serializedApplyXslt(node, xslt, xsltParam);
				nodeHtml = UtilXslt.applyXslt(node, xslt, xsltParam);
			}
			else{
				//ret = UtilXslt.serializedApplyXslt(node, xslt);
				nodeHtml = UtilXslt.applyXslt(node, xslt);
			}
			if (logger.isDebugEnabled())
				//logger.debug("html:" + UtilDom.domToString(nodeHtml, true, null, false));
				logger.debug("HTML: " + ret);
			ret = UtilDom.domToString(nodeHtml, false, null, false, true);//UtilXslt.serializedApplyXslt(node, xslt, xsltParam);//
			ret = ret.substring(ret.indexOf('\n'));
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
		if (logger.isDebugEnabled() && nodeHtml != null)
			logger.debug("END getHTML(Node): " + UtilDom.domToString(nodeHtml, true));

		return ret;
	}

	protected HTMLDocument getHTMLDocument() {
		return ((HTMLDocument) getDocument());
	}

	public Element getNextTextElement(Element e) {
		String origId = getElementId(e);
		Element prevElem = null;
		for (Element currElem = e; !currElem.equals(prevElem); currElem = getHTMLDocument().getCharacterElement(currElem.getEndOffset())) {
			String id = getElementId(currElem);
			Node node = getXsltMapper().getDomById(id);
			if (id != null && !id.equals(origId) && UtilDom.isTextNode(node))
				return currElem;
			prevElem = currElem;
		}
		return null;
	}

	public XsltPaneImpl getPane() {
		return pane;
	}

	public Element getPrevTextElement(Element e) {
		String origId = getElementId(e);
		Element prevElem = null;
		for (Element currElem = e; !currElem.equals(prevElem); currElem = getHTMLDocument().getCharacterElement(currElem.getStartOffset() - 1)) {
			String id = getElementId(currElem);
			Node node = getXsltMapper().getDomById(id);
			if (id != null && !id.equals(origId) && UtilDom.isTextNode(node))
				return currElem;
			prevElem = currElem;
		}
		return null;
	}

	public XsltMapper getXsltMapper() {
		return pane.getXsltMapper();
	}

	protected void highlightElement(Element e) {
		if (e == null)
			return;
		Highlighter hl = getHighlighter();
		int hlStart = e.getStartOffset();
		int hlEnd = e.getEndOffset();
		try {
			hl.addHighlight(hlStart+1, hlEnd-1, LEAF_SEL);
		} catch (BadLocationException ble) {
			ble.printStackTrace();
		}
	}

	protected void highlightElement(Element[] e) {
		if (e == null)
			return;

		for (int i = 0; i < e.length; i++) {
			highlightElement(e[i]);
		}
	}

	public void insertUpdate(DocumentEvent e) {
		if (ignoreDocumentEvents)
			return;
		
		Element modElem = getMappedSpan(e.getOffset()); 
		if(modElem == null) return;
			
		String id = getElementId(modElem);
		logger.debug("insertUpdate: id == " + id);
		Node modNode = pane.getXsltMapper().getDomById(id);
		logger.debug("with content: " + modNode.getTextContent());

		if(modNode == null) return;

		String newText = "";
		try {
			int start = modElem.getStartOffset();
			int end = modElem.getEndOffset();
			newText = getText(start, end - start);
			if (e.getLength() > 1 && newText.equals(getXsltMapper().getI18nNodeText(modNode.getParentNode()))) {
				newText = null;
			}
		} catch (BadLocationException ble) {
			ble.printStackTrace();
		}
		pane.updateNode(modNode, newText);
	}

	/**
	 * Usato solo per abilitare l'antialiasing del testo.
	 */
	boolean antialias = false;

	protected void paintComponent(java.awt.Graphics g) {
		try {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}
		super.paintComponent(g);
	}

	protected void removeAllHighlights() {
		Highlighter hl = getHighlighter();
		hl.removeAllHighlights();
		int selStart = getSelectionStart();
		int selEnd = getSelectionEnd();
		if (selStart != selEnd && selStart != -1) {
			try {
				hl.addHighlight(selStart, selEnd, DEF_SEL);
			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}
		}
	}

	public synchronized void removeUpdate(DocumentEvent e) {
		if (ignoreDocumentEvents)
			return;

		// FIXME: Verificare che sia uno span
		Element modElem = this.getMappedSpan(e.getOffset());
		String id = getElementId(modElem);
		Node modNode = pane.getXsltMapper().getDomById(id);
		String newText = null;
		try {
			int start = modElem.getStartOffset();
			int end = modElem.getEndOffset();
			newText = getText(start, end - start);
		} catch (BadLocationException ble) {
			ble.printStackTrace();
		}

		pane.updateNode(modNode, newText);
	}

	public void selectNode(Node[] nodes) {
		removeAllHighlights();

		if (nodes == null)
			return;

		boolean setCaret = false;
		for (int j = 0; j < nodes.length; j++) {
			if (nodes[j] == null)
				continue;

			Node node = nodes[j];
			
			switch (node.getNodeType()) {
			case Node.TEXT_NODE:
			case Node.PROCESSING_INSTRUCTION_NODE:
				Element element = getHTMLDocument().getElement(getXsltMapper().getIdByDom(node));
				if (element != null) {
					highlightElement(element);
					if (!setCaret) {
						ignoreCaretEvents = true;
						setCaretAtElement(element);
						ignoreCaretEvents = false;
						setCaret = true;
					}
				}
				break;
			case Node.ELEMENT_NODE:
				Vector textNodes = new Vector();
				UtilDom.getTextChildren(getXsltMapper(), node, textNodes);
				Element currElem = null;
				Element firstElem = null;
				Element lastElem = null;
				for (int i = 0; i < textNodes.size(); i++) {
					String currId = getXsltMapper().getIdByDom((Node) textNodes.get(i));
					currElem = getHTMLDocument().getElement(currId);
					if (currElem != null) {
						highlightElement(currElem);
						if (firstElem == null)
							firstElem = currElem;
						lastElem = currElem;
					}
				}
				if (!setCaret) {
					ignoreCaretEvents = true;
					setCaretAtElement(lastElem);
					setCaretAtElement(firstElem);
					ignoreCaretEvents = false;
					setCaret = true;
				}
				break;
			}
		}
	}

	public void selectText(int start, int end) {
		if (start > end) {
			int a = start;
			start = end;
			end = a;
		}

		ignoreCaretEvents = true;
		select(start, end);
		ignoreCaretEvents = false;
	}

	protected void setCaretAtElement(Element e) {
		if (e != null) {
			setCaretPosition(e.getEndOffset());
			setCaretPosition(e.getStartOffset());
		}
	}

	public void setDom(Document document) {
		this.dom = document;
	}

	public void setParameter(Hashtable xsltParam) {
		this.xsltParam = xsltParam;
	}

	public void setStyleSheet(File file) {
		if (file != null && file.isFile() && file.canRead())
			try {
				FileReader fr = new FileReader(file);
				StyleSheet s = getHTMLDocument().getStyleSheet();
				s.removeStyleSheet(s);
				s.loadRules(fr, null);
				fr.close();
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
	}

	public void setXslt(File xslt) {
		if (xslt != null && xslt.exists() && xslt.isFile() && xslt.canRead())
			this.xslt = xslt;
	}

	public void setText(String t) {
		try {
			HTMLDocument doc = (HTMLDocument)getDocument();
			doc.remove(0, doc.getLength());
			if (t == null || t.equals("")) {
				return;
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(t.getBytes()), pane.documentManager.getEncoding()));
			//InputStreamReader r = new InputStreamReader(new ByteArrayInputStream(t.getBytes()), pane.documentManager.getEncoding());
			EditorKit kit = getEditorKit();
			kit.read(r, doc, 0);
		} catch (IOException ioe) {
			UIManager.getLookAndFeel().provideErrorFeedback(this);
		} catch (BadLocationException ble) {
			UIManager.getLookAndFeel().provideErrorFeedback(this);
		}
	}

	public void update() {
		if (logger.isDebugEnabled())
			logger.debug("BEGIN update()");
		setEnabled(false);
		ignoreDocumentEvents = true;
		ignoreCaretEvents = true;
		try {
			if (dom == null || xslt == null) {
				setText("");
			} else {
				String text = getHtml(dom);
				logger.debug(text);
				setText(text);
				setEnabled(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ignoreDocumentEvents = false;
			ignoreCaretEvents = false;
		}
	}

	public Node update(Node node) {
		String id = pane.getXsltMapper().getIdByDom(node);
		Element elem = getHTMLDocument().getElement(id);
		if (elem != null) {
			ignoreDocumentEvents = true;
			ignoreCaretEvents = true;
			try {
				getHTMLDocument().setOuterHTML(elem, getHtml(node));
				//getHTMLDocument().setOuterHTML(elem, convertEncoding(getHtml(node)));
				return node;
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			} finally {
				ignoreDocumentEvents = false;
				ignoreCaretEvents = false;
			}
		}
		return null;
	}
	
	
	// vedi   http://www.iam.ubc.ca/guides/javatut99/i18n/text/stream.html
	protected String convertEncoding(String text){
		    StringBuffer buffer = new StringBuffer();
		    try{ 	
		    	InputStreamReader r = new InputStreamReader(new ByteArrayInputStream(text.getBytes()), pane.documentManager.getEncoding());
		    	Reader in = new BufferedReader(r);
			    int ch;
			   while ((ch = in.read()) > -1) {
				   buffer.append((char)ch);
			   }
			   in.close();
			   return buffer.toString();
		    }
		    catch(Exception ex){
		    	logger.error(ex.getMessage(),ex);
		    	return text;
		    }	
	}

	
	public void viewDomSpellCheckEvent(DomSpellCheckEvent e) {
		ignoreCaretEvents = true;
		DomSpellCheckWord[] words = e.getWords();
		for (int i = 0; i < words.length; i++) {
			Node node = words[i].getNode();
			String id = getXsltMapper().getIdByDom(node);
			Element elem = getHTMLDocument().getElement(id);
			if (elem != null) {
				SpellCheckWord word = words[i].getSpellCheckWord();
				int eStart = elem.getEndOffset() + 1;
				int wStart = word.getStartOffset();
				int wEnd = word.getEndOffset();
				select(eStart + wStart, eStart + wEnd);

				AttributeSet attrs = getHTMLDocument().getCharacterElement(eStart + wStart).getAttributes();
				SimpleAttributeSet modAttrs = new SimpleAttributeSet(attrs);

				StyleConstants.setForeground(modAttrs, Color.red);
				StyleConstants.setUnderline(modAttrs, true);

				setCharacterAttributes(modAttrs, false);
			}
		}
		ignoreCaretEvents = false;
	}

	protected class SpellThread implements Runnable {
		boolean terminate = false;

		Vector elems = new Vector(100, 100);

		public void addElement(Element elem) {
			elems.addElement(elem);
		}

		public void run() {
			while (!terminate) {
				try {
					Thread.sleep(150);
					if (elems.size() == 0 || !pane.domSpellCheck.isEnabled())
						continue;
					Element elem = (Element) elems.remove(0);
					int start = elem.getStartOffset();
					int end = elem.getEndOffset();
					int caretPos = getCaretPosition();
					SpellCheckWord[] words = pane.domSpellCheck.getSpellCheck().spellCheck(getText(start, end - start));
					for (int i = 0; i < words.length; i++) {
						ignoreCaretEvents = true;
						int s = words[i].getStartOffset();
						int e = words[i].getEndOffset();
						select(start + s, start + e);

						AttributeSet attrs = getHTMLDocument().getCharacterElement(start + 1).getAttributes();
						SimpleAttributeSet modAttrs = new SimpleAttributeSet(attrs);

						StyleConstants.setForeground(modAttrs, Color.red);
						StyleConstants.setUnderline(modAttrs, true);

						setCharacterAttributes(modAttrs, false);
						ignoreCaretEvents = false;
					}
					if (words.length > 0) {
						ignoreCaretEvents = true;
						setCaretPosition(caretPos);
						ignoreCaretEvents = false;
					}

				} catch (Exception ex) {
					logger.error(ex.toString(), ex);
				}

			}

			// ignoreCaretEvents = true;
			// Element curr =
			// getHTMLDocument().getParagraphElement(getHTMLDocument().getStartPosition().getOffset());
			// Element prev = null;
			// while (!curr.equals(prev)) {
			// prev = curr;
			// int start = prev.getStartOffset();
			// int end = prev.getEndOffset();
			// try {
			// SpellCheckWord[] words =
			// pane.domSpellCheck.getSpellCheck().spellCheck(getText(start, end
			// - start));
			// for (int i = 0; i < words.length; i++) {
			// int s = words[i].getStartOffset();
			// int e = words[i].getEndOffset();
			// select(start + s, start + e);
			//
			// AttributeSet attrs = getHTMLDocument().getCharacterElement(start
			// + 1).getAttributes();
			// SimpleAttributeSet modAttrs = new SimpleAttributeSet(attrs);
			//
			// StyleConstants.setForeground(modAttrs, Color.red);
			// StyleConstants.setUnderline(modAttrs, true);
			//
			// setCharacterAttributes(modAttrs, false);
			// }
			// } catch (BadLocationException ex) {
			// }
			// curr =
			// getHTMLDocument().getParagraphElement(curr.getEndOffset());
			// }
			// ignoreCaretEvents = false;

		}
	}
}
