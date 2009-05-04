package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane;

import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions.XsltAction;
import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions.XsltDeleteNextCharAction;
import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions.XsltDeletePrevCharAction;
import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions.XsltInsertBreakAction;
import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions.XsltKeyTypedAction;
import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions.XsltPasteAction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.TextAction;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;

/**
 * Classe per impostare le azioni di modifica.
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
 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
 */
public class XsltEditorKit extends HTMLEditorKit {
	private Action[] xsltActions;

	AntiAliasedTextPane textPane;

	/** Crea una nuova istanza di XsltEditorKit */
	public XsltEditorKit(AntiAliasedTextPane textPane) {
		super();
		XsltDeleteNextCharAction xsltDeleteNextCharAction = new XsltDeleteNextCharAction();
		XsltDeletePrevCharAction xsltDeletePrevCharAction = new XsltDeletePrevCharAction();
		XsltKeyTypedAction xsltKeyTypedAction = new XsltKeyTypedAction();
		XsltInsertBreakAction xsltInsertBreakAction = new XsltInsertBreakAction();
		XsltPasteAction xsltPasteAction = new XsltPasteAction();
		xsltActions = new Action[] { xsltDeleteNextCharAction, xsltDeletePrevCharAction, xsltInsertBreakAction, xsltPasteAction, xsltKeyTypedAction };

		textPane.getKeymap().setDefaultAction(xsltKeyTypedAction);

		this.textPane = textPane;
	}

	public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {
		// TODO Auto-generated method stub
		// InputStreamReader reader = new InputStreamReader(in, "UTF-8");
		// String encoding = textPane.getEncoding();

		// StringBuffer sb = new StringBuffer();
		// //BufferedReader br = new BufferedReader(in);
		// for (int c = in.read(); c != -1; )
		// sb.append(c);
		// ByteArrayInputStream bais = new
		// ByteArrayInputStream(sb.toString().getBytes());
		// InputStreamReader r = new InputStreamReader(bais, encoding);
		super.read(in, doc, pos);
	}

	public void insertHTML(HTMLDocument doc, int offset, String html, int popDepth, int pushDepth, Tag insertTag) throws BadLocationException, IOException {
		// TODO Auto-generated method stub
		// super.insertHTML(doc, offset, html, popDepth, pushDepth, insertTag);

		Parser p = getParser();
		if (p == null) {
			throw new IOException("Can't load parser");
		}
		if (offset > doc.getLength()) {
			throw new BadLocationException("Invalid location", offset);
		}

		ParserCallback receiver = doc.getReader(offset, popDepth, pushDepth, insertTag);
		// ParserCallback receiver = doc.getReader(offset);
		Boolean ignoreCharset = (Boolean) doc.getProperty("IgnoreCharsetDirective");

		String encoding = textPane.getEncoding();
		Reader reader;
		if (encoding != null)
			reader = new InputStreamReader(new ByteArrayInputStream(html.getBytes()), encoding);
		else
			reader = new StringReader(html);
		p.parse(reader, receiver, (ignoreCharset == null) ? false : ignoreCharset.booleanValue());
		receiver.flush();

	}

	// public Document createDefaultDocument() {
	// // TODO Auto-generated method stub
	// StyleSheet styles = getStyleSheet();
	// StyleSheet ss = new StyleSheet();
	//
	// ss.addStyleSheet(styles);
	//
	// HTMLDocument doc = new EncodedHTMLDocument(ss, textPane.getEncoding());
	// doc.setParser(getParser());
	// doc.setAsynchronousLoadPriority(4);
	// doc.setTokenThreshold(100);
	// return doc;
	//
	// }

	public Action[] getActions() {
		Hashtable hash = new Hashtable();
		Action[] curr = super.getActions();
		for (int i = 0; i < curr.length; i++) {
			hash.put(curr[i].getValue(Action.NAME), curr[i]);
		}
		for (int i = 0; i < xsltActions.length; i++)
			try {
				XsltAction a = (XsltAction) xsltActions[i];
				Action oa = (Action) hash.get(a.getName());
				a.setDefaultAction(oa);
			} catch (ClassCastException ex) {
			}
		return TextAction.augmentList(super.getActions(), xsltActions);
	}

}
