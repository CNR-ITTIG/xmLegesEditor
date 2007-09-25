package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane;

import java.awt.Rectangle;

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
		if (direction == SwingConstants.EAST) {
			int maxLen = textPane.getHTMLDocument().getDefaultRootElement().getEndOffset();
			for (int i = pos + 1; i < maxLen; i++) {
				Element newElem = textPane.getHTMLDocument().getCharacterElement(i - 1);
				if (newElem.getName().equals("content") && textPane.getEnclosingSpan(newElem) != null) {
					return i;
				}
			}
			return pos;
		} else if (direction == SwingConstants.WEST) {
			for (int i = pos - 1; i >= 0; i--) {
				Element newElem = textPane.getHTMLDocument().getCharacterElement(i - 1);
				if (newElem.getName().equals("content") && textPane.getEnclosingSpan(newElem) != null) {
					return i;
				}
			}
			return pos;
		} else if (direction == SwingConstants.SOUTH) {
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
		} else if (direction == SwingConstants.NORTH) {
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
		Element newElem = textPane.getHTMLDocument().getCharacterElement(dot);
		if (textPane.getEnclosingSpan(newElem) != null && !newElem.equals(textPane.getStartSpan(newElem))
				&& dot != (textPane.getStartSpan(newElem).getEndOffset()))
			super.moveDot(fb, dot, bias);
	}

	public void setDot(FilterBypass fb, int dot, Bias bias) {
		try {
			Element newElem = textPane.getHTMLDocument().getCharacterElement(dot);
			
			Object attr = newElem.getAttributes().getAttribute(HTML.Tag.A);
			if (attr != null) {
				textPane.href = attr.toString().trim();
				return;
			}
			
// OLD			
//			Enumeration en = newElem.getAttributes().getAttributeNames();
//			while (en.hasMoreElements()) {
//				Object k = en.nextElement();
//				if ("a".equalsIgnoreCase(k.toString())) {
//					String[] browsers = xsltPaneImpl.getBrowsers();
//					for (int i = 0; i < browsers.length; i++)
//						try {
//							String href = newElem.getAttributes().getAttribute(k).toString();
//							String cmd = browsers[i] + " " + href.substring(5);
//							Runtime.getRuntime().exec(cmd);
//							break;
//						} catch (Exception ex) {
//						}
//				}
//			}
			Element enclosingSpan = textPane.getEnclosingSpan(newElem);
			boolean isEnclosed = enclosingSpan != null
					&& ((newElem.getName().equals("content") && dot != enclosingSpan.getStartOffset()) || dot == enclosingSpan.getEndOffset());

			if (isEnclosed)
				super.setDot(fb, dot, bias);
			else {
				try {
					int east, west;
					east = getNextVisualPositionFrom(textPane, dot, bias, SwingConstants.EAST, new Position.Bias[1]);
					west = getNextVisualPositionFrom(textPane, dot, bias, SwingConstants.WEST, new Position.Bias[1]);

					Rectangle rectPos = textPane.modelToView(dot);
					Rectangle rectEast = textPane.modelToView(east);
					Rectangle rectWest = textPane.modelToView(west);

					double eastRectDiff = Math.abs(rectPos.getY() - rectEast.getY());
					double westRectDiff = Math.abs(rectPos.getY() - rectWest.getY());

					int eastDiff = Math.abs(dot - east);
					int westDiff = Math.abs(dot - west);
					if (east != west) {
						int newDest;
						if (eastRectDiff < westRectDiff) {
							newDest = eastDiff == 0 ? west : east;
						} else if (eastRectDiff > westRectDiff) {
							newDest = westDiff == 0 ? east : west;
						} else {
							if (eastDiff <= westDiff) {
								newDest = eastDiff == 0 ? west : east;
							} else {
								newDest = westDiff == 0 ? east : west;
							}
						}
						super.setDot(fb, newDest, bias);
					} /* else super.setDot(fb, dot, bias);*/
				}

				catch (BadLocationException ble) {
				}
			}
		} catch (Throwable tr) {

		}
	}
}
