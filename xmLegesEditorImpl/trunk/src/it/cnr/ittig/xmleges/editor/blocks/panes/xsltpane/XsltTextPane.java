package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.util.clipboard.UtilClipboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.html.StyleSheet;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Classe per visualizzare il testo HTML ottenuto tramite i fogli di stile.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class XsltTextPane extends JTextPane {
	XsltPaneImpl xsltPaneImpl;

	Logger logger;

	Document dom;

	File xslt;

	File style;

	Hashtable xsltParam;

	/** Crea un nuova istanza di XsltTextPane */
	public XsltTextPane(XsltPaneImpl xsltPaneImpl) {
		this.xsltPaneImpl = xsltPaneImpl;
		this.logger = xsltPaneImpl.getLogger();
		setDoubleBuffered(true);
		setEnabled(false);
		setEditorKit(new XsltEditorKit(xsltPaneImpl));
		setSelectedTextColor(Color.BLACK);
		setSelectionColor(new Color(147, 147, 255));
	}

	/**
	 * Imposta il documento.
	 * 
	 * @param dom documento
	 */
	public void setDocument(Document dom) {
		this.dom = dom;
	}

	public XsltDocument getXsltDocument() {
		return (XsltDocument) getDocument();
	}

	/**
	 * Imposta il file di trasformazione XSLT per la visualizzazione.
	 * 
	 * @param xslt file di trasformazione
	 */
	public void setXslt(File xslt) {
		if (xslt != null && xslt.exists() && xslt.isFile() && xslt.canRead())
			this.xslt = xslt;
	}

	public void setStyleSheet(File file, boolean add) {
		if (file != null && file.isFile() && file.canRead())
			try {
				FileReader fr = new FileReader(file);
				StyleSheet s = getXsltDocument().getStyleSheet();
				if (!add)
					s.removeStyleSheet(s);
				s.loadRules(fr, null);
				fr.close();
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
	}

	public void setParameter(Hashtable xsltParam) {
		this.xsltParam = xsltParam;
	}

	public void update() {
		setEnabled(false);
		if (logger.isDebugEnabled())
			logger.debug("update=" + dom);
		if (dom == null) {
			setText("");
			return;
		}
		try {
			setText("loading...");
			String text = getXsltDocument().setDocument(dom, xslt, xsltParam);
			Action[] actions = getEditorKit().getActions();
			for (int i = 0; i < actions.length; i++)
				if (actions[i].getValue(Action.NAME) == DefaultEditorKit.defaultKeyTypedAction)
					getKeymap().setDefaultAction(actions[i]);

			if (logger.isDebugEnabled())
				logger.debug("text from XsltDocument:" + text);
			setText(text);
			setEnabled(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	boolean localCaret = false;

	public void setCaretPosition(int pos, boolean local) {
		boolean l = localCaret;
		localCaret = local;
		setCaretPosition(pos);
		updateSelections(local);
		localCaret = l;
	}

	XsltDocument.XsltLeafElement lastLeaf = null;

	Highlighter.HighlightPainter leafSel = new DefaultHighlighter.DefaultHighlightPainter(new Color(220, 220, 220));

	Highlighter.HighlightPainter defSel = new DefaultHighlighter.DefaultHighlightPainter(new Color(147, 147, 255));

	public void updateSelections(boolean local) {
		try {
			logger.debug("BEGIN updateSelections");
			int selStart = getSelectionStart();
			int selEnd = getSelectionEnd();
			logger.debug("sel=" + selStart + "," + selEnd);
			Highlighter hl = getHighlighter();
			hl.removeAllHighlights();
			XsltDocument.XsltLeafElement[] selLeafs = getXsltDocument().getSelectedLeafs();
			if (selLeafs != null && selLeafs.length > 0) // NODI SELEZIONATI
				for (int i = 0; i < selLeafs.length; i++) {
					int s = selLeafs[i].getStartOffset();
					int e = selLeafs[i].getEndOffset();
					hl.addHighlight(s, e, defSel);
				}
			else { // GESTIONE DELLA SELEZIONE DEL TEXT PANE
				XsltDocument.XsltLeafElement leaf = getXsltDocument().getActiveLeaf();
				if (leaf == null || leaf.getId() == null) {
					logger.debug("leaf null or without id");
					return;
				} else {
					int leafS = leaf.getStartOffset();
					int leafE = leaf.getEndOffset();
					// hl.addHighlight(leafS, leafE, leafSel);
					if (selStart >= leafS && selEnd <= leafE) {
						// SELEZIONE INTERNA AL LEAF
						if (selStart == selEnd)
							hl.addHighlight(leafS, leafE, leafSel);
						else
							hl.addHighlight(selStart, selEnd, defSel);
						Node node = getXsltDocument().getDomByLeaf(leaf);
						if (node != null && local && !(xsltPaneImpl.selectionMananger.getActiveNode() == node && leaf.isEmpty()))
							xsltPaneImpl.fireSelectionChanged(leaf.isEmpty() ? getXsltDocument().xsltMapper.getParentByGen(node) : node, selStart - leafS,
									selEnd - leafS);
					} else
						// SELEZIONE ESTERNA
						// TODO agganciare individuazione selezione nodi
						;
					// xsltPaneImpl.getXsltDocument().updateSelection(selStart,
					// selEnd);
					// hl.addHighlight(selStart, selEnd, hlSel);
					hl.addHighlight(leafS, leafE, leafSel);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
		logger.debug("UPDATE SELECTION END");
	}

	/**
	 * Usato solo per abilitare l'antialiasing del testo.
	 */
	protected void paintComponent(java.awt.Graphics g) {
		try {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		} catch (ClassCastException ex) {
		}
		super.paintComponent(g);
	}

	public boolean canCut() {
		try {
			// TODO selezione?
			return getXsltDocument().getActiveLeaf().getId() != null;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean canCopy() {
		try {
			return getXsltDocument().getActiveLeaf().getId() != null;
		} catch (Exception ex) {
			return false;
		}
	}

	public void copy() {
		if (getSelectedText() == null) // copio tutto il testo
			UtilClipboard.set(getXsltDocument().getActiveLeaf().getText());
		else
			UtilClipboard.set(getSelectedText());
	}

	public boolean canPaste() {
		try {
			return getXsltDocument().getActiveLeaf().getId() != null && UtilClipboard.hasString();
		} catch (Exception ex) {
			return false;
		}
	}

	public void paste() {
		XsltDocument.XsltLeafElement leaf = getXsltDocument().getActiveLeaf();
		int caret = getCaretPosition();
		int start = leaf.getStartOffset();
		int end = leaf.getEndOffset();
		int selStart = getSelectionStart();
		int selEnd = getSelectionEnd();

		try {
			String text = getText(start, end - start);

			StringBuffer modText = new StringBuffer();
			int newCaretOffset;
			if (selStart != selEnd) {
				modText.append(text.substring(0, selStart - start));
				modText.append(UtilClipboard.getAsString());
				newCaretOffset = modText.length();
				modText.append(text.substring(selEnd - start));
			} else {
				modText.append(text.substring(0, caret - start));
				modText.append(UtilClipboard.getAsString());
				newCaretOffset = modText.length();
				modText.append(text.substring(caret - start));
			}

			logger.debug("modtext=" + modText.toString());
			leaf.updateDom(modText.toString());

			setCaretPosition(start + newCaretOffset);
		} catch (BadLocationException ex) {
			logger.error(ex.toString(), ex);
		}
	}

	public boolean canDelete() {
		return false;
	}

	public boolean canPasteAsText() {
		return false;
	}

}
