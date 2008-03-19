package it.cnr.ittig.xmleges.editor.blocks.provvedimenti;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.ClasseItem;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.DtdItem;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.ProvvedimentiItem;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.TemplateItem;

import java.util.Vector;

/**
 * <h1>Implementazione del servizio <code>provvedimenti.Provvedimenti</code>.</h1>
 * <h1>Componente che gestisce l'interazione con il file xml ch contiene
 * l'elenco dei tipi di provvedimento gestibili da xmLegesEditor
 * <h1>Configurazione</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 * 
 * @version 1.0
 * @author Lorenzo Sarti
 */
public class ProvvedimentiImpl implements Provvedimenti, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	Configuration conf_Provvedimenti;

	private ProvvedimentiItem ManageUrn(Configuration conf, ProvvedimentiItem provvedimento) {
		try {
			provvedimento.setUrnAtto(conf.getChild("atto").getValue());
			provvedimento.setUrnCitazione(conf.getChild("citazione").getValue());
			provvedimento.setUrnAutorita(conf.getChild("autorita").getValue());
			try {
				provvedimento.setUrnValore(conf.getChild("valore").getValue());
			} catch (ConfigurationException e) {
				logger.info("Valore urn non presente");
			}
		} catch (ConfigurationException e) {
			logger.error("Errore lettura urn");
			return provvedimento;
		}
		return provvedimento;
	}

	private ProvvedimentiItem ManageTemplates(Configuration[] conf, ProvvedimentiItem provvedimento) {
		try {
			if (conf.length == 0) {
				provvedimento.hasTemplate(false);
				return provvedimento;
			} else {
				provvedimento.hasTemplate(true);
				TemplateItem[] listatemplate = new TemplateItem[conf.length];
				for (int i = 0; i < conf.length; i++) {
					TemplateItem template = new TemplateItem();
					template.setType(conf[i].getAttribute("tipo"));
					try {
						String numerato = conf[i].getAttribute("numerato");
						if (numerato.equals("SI"))
							template.setNumerato(true);
						else
							template.setNumerato(false);
					} catch (ConfigurationException e) {
						logger.info("Attributo numerato non presente");
						template.hasNumerato(false);
					}
					template.setTag(conf[i].getAttribute("tag"));
					template.setFileName(conf[i].getValue());
					listatemplate[i] = template;
				}
				provvedimento.setTemplateList(listatemplate);
				return provvedimento;
			}
		} catch (ConfigurationException e) {
			logger.error("Impossibile leggere l'elenco dei template");
			return provvedimento;

		}
	}

	private ProvvedimentiItem ManageDtds(Configuration[] conf, ProvvedimentiItem provvedimento) {
		try {
			if (conf.length == 0) {
				return provvedimento;
			} else {
				DtdItem[] listadtd = new DtdItem[conf.length];
				for (int i = 0; i < conf.length; i++) {
					DtdItem dtd = new DtdItem();
					dtd.setDtdName(conf[i].getAttribute("name"));
					dtd.setFileName(conf[i].getValue());
					listadtd[i] = dtd;
				}
				provvedimento.setDtdList(listadtd);
				return provvedimento;
			}
		} catch (ConfigurationException e) {
			logger.error("Impossibile leggere l'elenco delle dtd");
			return provvedimento;

		}
	}

	private ProvvedimentiItem CreateProvvedimento(Configuration conf) {
		ProvvedimentiItem provvedimento = new ProvvedimentiItem();
		try {
			provvedimento.setName(conf.getAttribute("name"));
			String emananti = conf.getAttribute("emananti");
			if (emananti.equals("SI"))
				provvedimento.setEmananti(true);
			else
				provvedimento.setEmananti(false);
			provvedimento = ManageUrn(conf.getChild("urn"), provvedimento);
			provvedimento = ManageTemplates(conf.getChildren("template"), provvedimento);
			provvedimento = ManageDtds(conf.getChildren("dtd"), provvedimento);
			return provvedimento;
		} catch (ConfigurationException e) {
			logger.info("Impossibile leggere provvedimento");
			return provvedimento;
		}

	}

	private int CountProvvedimenti() {
		int num_provvedimenti = 0;
		Configuration[] conf_classi = conf_Provvedimenti.getChildren();
		int num_classi = conf_classi.length;
		for (int i = 0; i < num_classi; i++) {
			num_provvedimenti += conf_classi[i].getChildren().length;
		}
		return num_provvedimenti;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti#getAllClassi()
	 */

	public ClasseItem[] getAllClassi() {
		logger.info("Creazione elenco completo classi di provvedimenti");
		Configuration[] listclass = conf_Provvedimenti.getChildren("classe");
		ClasseItem[] classilist = new ClasseItem[listclass.length];
		for (int i = 0; i < listclass.length; i++) {
			ClasseItem classe = new ClasseItem();
			Configuration[] listprovv = listclass[i].getChildren("provvedimento");
			ProvvedimentiItem[] provvedimentilist = new ProvvedimentiItem[listprovv.length];
			for (int j = 0; j < listprovv.length; j++)
				provvedimentilist[j] = CreateProvvedimento(listprovv[j]);
			try {
				classe.setName(listclass[i].getAttribute("name"));
			} catch (ConfigurationException e) {
				logger.info("Impossibile leggere la classe di provvedimenti");
				return null;
			}
			classe.setProvvedimentiList(provvedimentilist);
			classilist[i] = classe;
		}
		logger.info("Creazione completata");
		return classilist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti#getAllProvvedimenti()
	 */
	private void DisplayClassi(ClasseItem[] classi) {
		for (int i = 0; i < classi.length; i++) {
			logger.debug(classi[i].getName());
			for (int j = 0; j < classi[i].getProvvedimentiList().length; j++) {
				logger.debug(classi[i].getProvvedimentoAt(j).toString());
				if (classi[i].getProvvedimentoAt(j).getHasTemplate()) {
					for (int k = 0; k < classi[i].getProvvedimentoAt(j).getTemplateList().length; k++)
						logger.debug(classi[i].getProvvedimentoAt(j).getTemplateAt(k).toString());
				}
			}
		}
	}

	public ProvvedimentiItem[] getAllProvvedimenti() {
		logger.info("Creazione elenco completo classi di provvedimenti");
		Configuration[] listclass = conf_Provvedimenti.getChildren("classi");
		try {
			ProvvedimentiItem[] provvedimentilist = new ProvvedimentiItem[CountProvvedimenti()];

			for (int i = 0; i < listclass.length; i++) {
				Configuration[] listprovv = listclass[i].getChildren("provvedimento");
				for (int j = 0; j < listprovv.length; j++)
					provvedimentilist[j] = CreateProvvedimento(listprovv[j]);
			}
			logger.info("Creazione completata");
			return provvedimentilist;
		} catch (Exception e) {
			logger.info("Errore nella creazione della lista provvedimenti");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti#getClasseByName(java.lang.String)
	 */
	public ClasseItem getClasseByName(String nomeclasse) {
		logger.debug("Ricerca della classe di provvedimenti " + nomeclasse);
		ClasseItem classe = new ClasseItem();
		Configuration[] listaclassi = conf_Provvedimenti.getChildren("classe");
		for (int i = 0; i < listaclassi.length; i++) {
			try {
				if (listaclassi[i].getAttribute("name").equals(nomeclasse)) {
					classe.setName(nomeclasse);
					Configuration[] listprovv = listaclassi[i].getChildren("provvedimento");
					ProvvedimentiItem[] provvedimentilist = new ProvvedimentiItem[listprovv.length];
					for (int j = 0; j < listprovv.length; j++)
						provvedimentilist[j] = CreateProvvedimento(listprovv[j]);
					classe.setProvvedimentiList(provvedimentilist);
					logger.debug("Ricerca terminata con successo");
					return classe;

				}
			} catch (ConfigurationException e) {
				logger.error("Errore nella lettura della lista delle classi");
				return classe;

			}

		}
		logger.debug("Ricerca terminata senza successo");
		return classe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti#getProvvedimentoByName(java.lang.String)
	 */
	public ProvvedimentiItem getProvvedimentoByName(String nomeprovvedimento) {
		logger.debug("Ricerca del provvedimento in base al nome: " + nomeprovvedimento);
		ProvvedimentiItem provvedimento = new ProvvedimentiItem();
		Configuration[] listaclassi = conf_Provvedimenti.getChildren();
		for (int i = 0; i < listaclassi.length; i++) {
			Configuration[] listaprovvedimenti = listaclassi[i].getChildren();
			for (int j = 0; j < listaprovvedimenti.length; j++) {
				try {
					if (listaprovvedimenti[j].getAttribute("name").equalsIgnoreCase(nomeprovvedimento)) {
						provvedimento = CreateProvvedimento(listaprovvedimenti[j]);
						logger.debug("Ricerca del provvedimento terminata con successo");
						return provvedimento;
					}
				} catch (ConfigurationException e) {
					logger.error("Errore nella ricerca di provvedimenti per nome");
					// FIXME era return provvedimento; metto return null
					return null;
				}
			}
		}
		logger.debug("Ricerca del provvedimento terminata senza successo");
		return null;
	}

	public ProvvedimentiItem[] getProvvedimentiByTag(String tag) {
		logger.debug("Ricerca del provvedimento in base al tag: " + tag);
		ProvvedimentiItem provvedimento = new ProvvedimentiItem();
		Vector found = new Vector();
		Configuration[] listaclassi = conf_Provvedimenti.getChildren();
		for (int i = 0; i < listaclassi.length; i++) {
			Configuration[] listaprovvedimenti = listaclassi[i].getChildren();
			for (int j = 0; j < listaprovvedimenti.length; j++) {
				provvedimento = CreateProvvedimento(listaprovvedimenti[j]);
				if (provvedimento.getHasTemplate()) {
					TemplateItem[] templ = provvedimento.getTemplateList();
					for (int k = 0; k < templ.length; k++) {
						if (templ[k].getTag().equalsIgnoreCase(tag))
							found.add(provvedimento);
					}
				}
			}
		}
		ProvvedimentiItem[] ret = new ProvvedimentiItem[found.size()];
		found.copyInto(ret);
		return ret;
	}

	public ProvvedimentiItem getProvvedimentoByUrn(String urnatto) {
		logger.debug("Ricerca del provvedimento in base alla urn: " + urnatto);
		ProvvedimentiItem provvedimento = new ProvvedimentiItem();
		Configuration[] listaclassi = conf_Provvedimenti.getChildren();
		for (int i = 0; i < listaclassi.length; i++) {
			Configuration[] listaprovvedimenti = listaclassi[i].getChildren();
			for (int j = 0; j < listaprovvedimenti.length; j++) {
				try {
					if (null != listaprovvedimenti[j].getChild("urn").getChild("atto").getValue()
							&& listaprovvedimenti[j].getChild("urn").getChild("atto").getValue().equalsIgnoreCase(urnatto)) {
						provvedimento = CreateProvvedimento(listaprovvedimenti[j]);
						logger.debug("Ricerca del provvedimento terminata con successo");
						return provvedimento;
					}
				} catch (ConfigurationException e) {
					logger.error("Errore nella ricerca di provvedimenti per nome");
					// FIXME era return provvedimento; metto return null
					return null;
				}
			}
		}
		logger.debug("Ricerca del provvedimento terminata senza successo");
		// FIXME era return provvedimento; metto return null
		return null;
	}

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		logger.info("provvedimenti: Avvio Provvedimenti");

	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		try {
			this.conf_Provvedimenti = configuration;
		} catch (Exception e) {
			logger.error("Errore di lettura del file di configurazione \"Provvedimenti\"");
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {

	}
}
