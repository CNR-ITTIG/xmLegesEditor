package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane;

import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.html.HTML;

public class NavigationFilter extends javax.swing.text.NavigationFilter {

	XsltPaneImpl xsltPaneImpl;

	AntiAliasedTextPane textPane;
	
	public NavigationFilter(AntiAliasedTextPane textPane, XsltPaneImpl pane) {
		super();
		this.textPane = textPane;
		this.xsltPaneImpl = pane;
	}

	public int getNextVisualPositionFrom(JTextComponent text, int pos, Bias bias, int direction, Bias[] biasRet) throws BadLocationException {
		if (direction == SwingConstants.EAST || direction == SwingConstants.WEST) {
			int i = pos, prev = pos;
			while((i = super.getNextVisualPositionFrom(text, i, bias, direction, biasRet)) != prev) {
				Element span = textPane.getMappedSpan(i);
				// posizione consentita se dentro ad uno span o alla sua fine.
				if(span != null && i != span.getStartOffset())// || (span == null && prevSpan != null))
					return i;
				prev = i;
			}
			return i;
		} else if (direction == SwingConstants.SOUTH || direction == SwingConstants.NORTH) {
			int newPos = pos, newEast, newWest, oldPos;
			double newPosLine, eastLine, westLine;
			do {
				oldPos = newPos;
				newPos = super.getNextVisualPositionFrom(text, newPos, bias, direction, biasRet);
				newEast = getNextVisualPositionFrom(text, newPos, bias, SwingConstants.EAST, new Position.Bias[1]);
				newWest = getNextVisualPositionFrom(text, newPos, bias, SwingConstants.WEST, new Position.Bias[1]);
				newPosLine = textPane.modelToView(newPos).getY();
				eastLine = textPane.modelToView(newEast).getY();
				westLine = textPane.modelToView(newWest).getY();
			} while (oldPos != newPos && newPosLine != eastLine && newPosLine != westLine);
			return newPos;
		}

		return super.getNextVisualPositionFrom(text, pos, bias, direction, biasRet);
	}

	public void moveDot(FilterBypass fb, int dot, Bias bias) {
		// se l'elemento si trova all'interno di uno span mappato, muoviti,
		// altrimenti ignora l'azione.
		Element span = textPane.getMappedSpan(dot);
		if(span != null && dot != span.getStartOffset())
			super.moveDot(fb, dot, bias);
	}

	public void setDot(FilterBypass fb, int dot, Bias bias) {
		//System.err.println(textPane.getPane().getName()+"  setDot - DOT = "+dot );
		try {
			Element newElem = textPane.getHTMLDocument().getCharacterElement(dot);
			
			if(newElem != null) {
				Object attr = newElem.getAttributes().getAttribute(HTML.Tag.A);
				if (attr != null) {
					textPane.href = attr.toString().trim();
					return;
				}
			}

			Element span = textPane.getMappedSpan(dot);
		
			/*
			 * 	Debugging
			System.err.println("currElement: " + (newElem !=null ? newElem.getName() : null));
			System.err.println("Count: " + newElem.getElementCount());
			AttributeSet as  = newElem.getAttributes();
			java.util.Enumeration ae = as.getAttributeNames();
			while(ae.hasMoreElements())
			{
				Object key = ae.nextElement();
				System.err.println("AsName: (" + key.getClass() + ")" + key + " = (" + as.getAttribute(key).getClass() + ")" + as.getAttribute(key));
			}
			
			System.err.println("isMapped " + textPane.isMapped(newElem) + " isSpan " + textPane.isSpan(newElem));
			System.err.println("Posizione dot: " + dot + " " + (span !=null ? span.getName() : null));
			 */
			
			if(span != null && dot != span.getStartOffset() && dot != span.getEndOffset())
				// Quando l'utente clicca dentro uno span mappato, posiziona il cursore
				super.setDot(fb, dot, bias);
			else {
				// Quando l'utente clicca fuori da uno span ricerca la piu' vicina posizione ammissibile
			
				try {
					int east, west;
					east = getNextVisualPositionFrom(textPane, dot, bias, SwingConstants.EAST, new Position.Bias[1]);
					west = getNextVisualPositionFrom(textPane, dot, bias, SwingConstants.WEST, new Position.Bias[1]);
					
					//System.err.println(textPane.getPane().getName()+"  OUTSIDE SPAN - Setting dot "+dot +" east  "+east +"  west  "+west);
					
					int newDest = 0;
					if (Math.abs(west-dot) <= Math.abs(east-dot))
						newDest = west;
					else
						newDest = east;
					
					//System.err.println(textPane.getPane().getName()+"  OUTSIDE SPAN - to newDest  "+newDest);	
					
					super.setDot(fb, newDest, bias);
					
				}
				catch (BadLocationException ble) {
					ble.printStackTrace();
				}
			}
		} catch (Throwable tr) {
			tr.printStackTrace();
		}
	}
}
