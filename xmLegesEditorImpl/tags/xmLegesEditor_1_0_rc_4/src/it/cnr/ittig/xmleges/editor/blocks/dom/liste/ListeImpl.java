package it.cnr.ittig.xmleges.editor.blocks.dom.liste;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.liste.Liste;

import java.util.Collection;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.liste.Liste</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
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
 * @author <a href="mailto:cristina.mercatanti@tin.it">Cristina Mercatanti </a>
 */
public class ListeImpl implements Liste, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	Node lista;

	DocumentManager documentManager;

	DtdRulesManager dtdRulesManager;

	UtilRulesManager utilRulesManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
	}

	public int canCreaLista(Node node, String tipolista) {
		try {
			int azione;
			if (node != null) {
				Collection node_coll1 = dtdRulesManager.queryAppendable(node);
				if (node_coll1.contains(tipolista)) {
					azione = 1;
					return azione;
				} else {
					Collection node_coll2 = dtdRulesManager.queryPrependable(node);
					if (node_coll2.contains(tipolista)) {
						azione = 2;
						return azione;
					}
				}

				if (node.getParentNode() != null) {
					Collection node_coll3 = dtdRulesManager.queryInsertableAfter(node.getParentNode(), node);
					if (node_coll3.contains(tipolista)) {
						azione = 3;
						return azione;
					} else {
						Collection node_coll4 = dtdRulesManager.queryInsertableBefore(node.getParentNode(), node);
						if (node_coll4.contains(tipolista)) {
							azione = 4;
							return azione;
						}
					}
				}
			}
		} catch (DtdRulesManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return 0;
		}
		return 0;
	}

	public boolean canMuoviSu(Node node) {

		try {
			if (UtilDom.findParentByName(node, "h:li") != null) {
				Node hli = UtilDom.findParentByName(node, "h:li");
				if (hli.getParentNode() != null) {
					// Node nodoLista = hli.getParentNode(); // ma esiste elem
					// h:li senza nodo
					// lista padre?
					if (hli.getPreviousSibling() != null) {
						if (dtdRulesManager.queryCanDelete(hli.getParentNode(), hli))
							// Node precedente = elemento.getPreviousSibling();
							// if
							// (dtdRulesManager.queryCanInsertBefore(nodoLista,precedente,elemento))
							return true;

					}
				}

			}
		}

		catch (Exception ex) {
			return false;
		}
		return false;
	}

	public boolean canMuoviGiu(Node node) {

		try {
			if (UtilDom.findParentByName(node, "h:li") != null) {
				Node hli = UtilDom.findParentByName(node, "h:li");
				if (hli.getParentNode() != null) {
					// Node nodoLista = hli.getParentNode(); // ma esiste elem
					// h:li senza nodo
					// lista padre?
					if (hli.getNextSibling() != null) {
						if (dtdRulesManager.queryCanDelete(hli.getParentNode(), hli))
							// Node precedente = elemento.getPreviousSibling();
							// if
							// (dtdRulesManager.queryCanInsertBefore(nodoLista,precedente,elemento))
							return true;

					}
				}

			}
		}

		catch (Exception ex) {
			return false;
		}
		return false;
	}

	public boolean canPromuovi(Node node) {
		try {
			if (UtilDom.findParentByName(node, "h:li") != null) {
				Node hli = UtilDom.findParentByName(node, "h:li");
				if (hli.getParentNode() != null) {
					Node sottoLista = hli.getParentNode();

					if (sottoLista.getParentNode() != null) {
						if (UtilDom.findParentByName(sottoLista.getParentNode(), "h:ol") != null
								|| UtilDom.findParentByName(sottoLista.getParentNode(), "h:ul") != null) {
							if (dtdRulesManager.queryCanDelete(sottoLista, hli))
								// Node precedente =
								// elemento.getPreviousSibling();
								// if
								// (dtdRulesManager.queryCanInsertBefore(nodoLista,precedente,elemento))
								return true;
						}

					}
				}

			}
		}

		catch (Exception ex) {
			return false;
		}
		return false;
	}

	public boolean canRiduci(Node node) {
		try {
			if (UtilDom.findParentByName(node, "h:li") != null) {
				Node hli = UtilDom.findParentByName(node, "h:li");
				if (hli.getParentNode() != null) {
					// Node nodooLista = hli.getParentNode(); //ma esiste elem
					// h:li senza nodo lista padre?
					if (hli.getPreviousSibling() != null) {
						if (dtdRulesManager.queryCanDelete(hli.getParentNode(), hli))
							// Node precedente = elemento.getPreviousSibling();
							// if
							// (dtdRulesManager.queryCanInsertBefore(nodoLista,precedente,elemento))
							return true;

					}
				}

			}
		}

		catch (Exception ex) {
			return false;
		}
		return false;
	}

	public Node muoviSu(Node node) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			Node hli = UtilDom.findParentByName(node, "h:li");
			if (dtdRulesManager.queryCanInsertBefore(hli.getParentNode(), hli.getPreviousSibling(), hli)) {
				hli.getParentNode().insertBefore(hli, hli.getPreviousSibling());
				documentManager.commitEdit(tr);
				return node;
			} else {
				documentManager.rollbackEdit(tr);

				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public Node muoviGiu(Node node) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			Node hli = UtilDom.findParentByName(node, "h:li");
			if (dtdRulesManager.queryCanInsertAfter(hli.getParentNode(), hli.getNextSibling(), hli)) {
				UtilDom.insertAfter(hli, hli.getNextSibling());
				documentManager.commitEdit(tr);
				return node;

			} else {
				documentManager.rollbackEdit(tr);

				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public Node promuovi(Node node) {

		try {
			EditTransaction tr = documentManager.beginEdit();
			Node hli = UtilDom.findParentByName(node, "h:li");
			Node sottoLista = hli.getParentNode();
			Node hliSUP = UtilDom.findParentByName(sottoLista, "h:li");
			Node listaSup = hliSUP.getParentNode();
			// listaSup.appendChild(hli);
			if (dtdRulesManager.queryCanInsertAfter(listaSup, sottoLista.getParentNode(), hli)) {
				UtilDom.insertAfter(hli, sottoLista.getParentNode());
				if (!sottoLista.hasChildNodes())
					sottoLista.getParentNode().removeChild(sottoLista);
				documentManager.commitEdit(tr);
				return hli;
			} else {
				documentManager.rollbackEdit(tr);
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public Node riduci(Node node, String tipolista) {

		try {
			EditTransaction tr = documentManager.beginEdit();
			Node hli = UtilDom.findParentByName(node, "h:li");
			Node precedente = hli.getPreviousSibling();
			if (UtilDom.findDirectChild(precedente, tipolista) != null) {
				Node sottolista = UtilDom.findDirectChild(precedente, tipolista);
				if (dtdRulesManager.queryCanAppend(sottolista, hli)) {
					sottolista.appendChild(hli);
					documentManager.commitEdit(tr);
					return hli;
				} else {
					documentManager.rollbackEdit(tr);

					return null;
				}
			} else {
				lista = utilRulesManager.getNodeTemplate(tipolista);
				lista.replaceChild(hli, lista.getFirstChild());
				// lista.appendChild(hli);
				if (dtdRulesManager.queryCanAppend(precedente, lista)) {
					precedente.appendChild(lista);
					documentManager.commitEdit(tr);
					return hli;
				} else {
					documentManager.rollbackEdit(tr);

					return null;
				}
			}
		} catch (Exception ex) {
			return null;
		}

		// return null;
	}

	public Node creaLista(Node node, String tipolista) {

		lista = utilRulesManager.getNodeTemplate(tipolista);
		// lista = documentManager.getDocumentAsDom().createElement("h:ul");
		/*
		 * for (int i=1; i<=numeroElem; i++ ){ Node newelem =
		 * lista.getOwnerDocument().createElement("h:li");
		 * lista.appendChild(newelem); }
		 */
		return lista;
	}

	/*
	 * public Node creaListaNUM(Node node) { lista =
	 * utilRulesManager.getNodeTemplate("h:ol"); return lista; }
	 */

	public Node creaListaDEF(Node node) {

		lista = documentManager.getDocumentAsDom().createElement("h:dl");
		/*
		 * for (int i=1; i<=numeroElem; i++ ){ Node newelemDT =
		 * lista.getOwnerDocument().createElement("h:dt");
		 * lista.appendChild(newelemDT); Node newelemDD =
		 * lista.getOwnerDocument().createElement("h:dd");
		 * lista.appendChild(newelemDD); }
		 */
		return lista;
	}

}
