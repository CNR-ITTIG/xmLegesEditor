package it.cnr.ittig.xmleges.core.blocks.action.connect;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.action.file.open.FileOpenAction;
import it.cnr.ittig.xmleges.core.services.action.file.save.FileSaveAction;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.connect.ConnectAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.methods.LockMethod;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import sun.net.TelnetInputStream;
import sun.net.ftp.FtpClient;


/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.connect.ConnectAction</code>.</h1>
 * 
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
 */

public class ConnectActionImpl implements ConnectAction, EventManagerListener, Loggable, Serviceable, Initializable, Startable {

	HttpURL httpURL;

	WebdavResource webdavResource = null;
	
	WebdavResource[] list;
	
	Logger logger;

	PreferenceManager preferenceManager;
	
	ActionManager actionManager;

	UtilMsg utilMsg;

	DocumentManager documentManager;
	
	EventManager eventManager;
	
	FtpOpenAction ftpOpenAction;

	FtpSaveAction ftpSaveAction;

	WebdavOpenAction webdavOpenAction;
	
	WebdavSaveAction webdavSaveAction;
	
	AzioneConnetti connectAction;
	
	LockUnlockAction lockUnlockAction;
	
	FileOpenAction fileOpenAction;
	
	FileSaveAction fileSaveAction;
	
	String type;
	
	Frame frame;
	
	Form form;
	
	FtpClient ftpClient = new FtpClient();
	
	JLabel typehost;
	JLabel etiUser;
	JLabel etiPassword;
	JLabel etiDir;
	JTextField host;
	JTextField user;
	JTextField password;
	JTextField file;
	JLabel currDir;
	JList filedir;
	JButton connect;
	JButton lockUnlock;
	String lastFtpHost = null;
	String lastFtpUser = null;
	String lastFtpPass = null;
	String lastDavHost = null;
	String lastDavUser = null;
	String lastDavPass = null;
	String dirFtp;
	
	DefaultListModel listModel = new DefaultListModel();
	
	MouseAdapter mouseAdapter = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e) && filedir.getSelectedIndex()!=-1) {
				
				String temp = ((String) listModel.getElementAt(filedir.getSelectedIndex()));
				String pre = temp.substring(0, 6);
				temp = temp.substring(6,temp.length());
				
				if (type.equals("ftp")) {
					
					//aggiornare come fatto per DAV!!!!!!!!!!!
					
					if (cwdFTP(temp)) {	//test se è una directory
						file.setText("");
						readDirFTP("");
					} else	{
						file.setText(temp);
						file.setEnabled(true);
					}	
				}
				if (type.equals("webdav")) {		
					if (pre.trim().equals("DIR:")) {	//test se è una directory
						lockUnlock.setVisible(false);
						//file.setText("");
						readDirDAV(temp);
					} else {	
						
						//NON LO ATTIVO... cerca Strategia beinformed qui nel codice 
						//lockUnlock.setVisible(true);
						
						if (pre.trim().equals("Lock!"))
							lockUnlock.setText("Unlock");
						else
							lockUnlock.setText("Lock");
						file.setText(temp);
						file.setEnabled(true);
					}	
				}
			}
		}
	};
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		frame = (Frame) serviceManager.lookup(Frame.class);
		form = (Form) serviceManager.lookup(Form.class);
		fileOpenAction = (FileOpenAction) serviceManager.lookup(FileOpenAction.class); 
		fileSaveAction = (FileSaveAction) serviceManager.lookup(FileSaveAction.class);
	}


	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		ftpOpenAction = new FtpOpenAction();
		actionManager.registerAction("connect.ftp.open", ftpOpenAction);
		ftpSaveAction = new FtpSaveAction();
		actionManager.registerAction("connect.ftp.save", ftpSaveAction);
		webdavOpenAction = new WebdavOpenAction();
		actionManager.registerAction("connect.webdav.open", webdavOpenAction);
		webdavSaveAction = new WebdavSaveAction();
		actionManager.registerAction("connect.webdav.save", webdavSaveAction);	
		connectAction = new AzioneConnetti();
		actionManager.registerAction("editor.form.connetti.connect", connectAction);
		lockUnlockAction = new LockUnlockAction();
		actionManager.registerAction("editor.form.connetti.lockUnlock", lockUnlockAction);		
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		
		
		form.setSize(450, 430);
		form.setName("file.connect.form");
		form.setMainComponent(getClass().getResourceAsStream("Connetti.jfrm"));
		
		form.setHelpKey("help.contents.form.connect");
		
		typehost = (JLabel) form.getComponentByName("editor.form.connetti.typehost");
		etiUser = (JLabel) form.getComponentByName("editor.form.connetti.etiUser");
		etiPassword = (JLabel) form.getComponentByName("editor.form.connetti.etiPassword");
		etiDir = (JLabel) form.getComponentByName("editor.form.connetti.etiDir");
		host = (JTextField) form.getComponentByName("editor.form.connetti.host");
		user = (JTextField) form.getComponentByName("editor.form.connetti.user");
		password = (JTextField) form.getComponentByName("editor.form.connetti.password");
		file = (JTextField) form.getComponentByName("editor.form.connetti.file");
		currDir = (JLabel) form.getComponentByName("editor.form.connetti.dir");
		filedir = (JList) form.getComponentByName("editor.form.connetti.filedir");
		
		filedir.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		filedir.setEnabled(true);
		filedir.setModel(listModel);
		
		connect = (JButton) form.getComponentByName("editor.form.connetti.connect");
		connect.addActionListener(connectAction);
		lockUnlock = (JButton) form.getComponentByName("editor.form.connetti.lockUnlock");
		lockUnlock.setVisible(false);
		lockUnlock.addActionListener(lockUnlockAction);

		Properties p = preferenceManager.getPreferenceAsProperties(this.getClass().getName());
		try {
			lastFtpHost = p.getProperty("lastftphost");
			lastFtpUser = p.getProperty("lastftpuser");
			lastFtpPass = p.getProperty("lastftppass");
			lastDavHost = p.getProperty("lastdavhost");
			lastDavUser = p.getProperty("lastdavuser");
			lastDavPass = p.getProperty("lastdavpass");
		} catch (Exception ex) {
		}
		manageEvent(null);
	}

	// /////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
	}

	public void stop() throws Exception {

		logger.debug("saving last host/user/pass...");
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		try {
			p.setProperty("lastftphost", lastFtpHost);
			p.setProperty("lastftpuser", lastFtpUser);
			p.setProperty("lastftppass", lastFtpPass);
			p.setProperty("lastdavhost", lastDavHost);
			p.setProperty("lastdavuser", lastDavUser);
			p.setProperty("lastdavpass", lastDavPass);
		} catch (Exception ex) {
		}
		preferenceManager.setPreference(getClass().getName(), p);
		logger.debug("saving OK");
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		ftpOpenAction.setEnabled(true);
		ftpSaveAction.setEnabled(!documentManager.isEmpty());
		webdavOpenAction.setEnabled(true);
		webdavSaveAction.setEnabled(!documentManager.isEmpty());
	}

	protected class AzioneConnetti extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (type.equals("ftp"))
				if (openFTP(host.getText(), 21, user.getText(), password.getText())) {
					if (!readDirFTP(""))
						utilMsg.msgError("connect.ftp.dirftp.error");
					else {
						lastFtpHost=host.getText();
						lastFtpUser=user.getText();
						lastFtpPass=password.getText();
					}
				} else  utilMsg.msgError("connect.ftp.open.error");
			if (type.equals("webdav"))
				if (openDAV(host.getText(), user.getText(), password.getText())) {
					if (!readDirDAV(""))
						utilMsg.msgError("connect.webdav.dirdav.error");
					else {
						lastDavHost=host.getText();
						lastDavUser=user.getText();
						lastDavPass=password.getText();
					}
				} else  utilMsg.msgError("connect.dav.open.error");
		}
	}	
	
	protected class LockUnlockAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			try {
				
				//NOTA: non imposto nessun parametro sul LOCK tipo:
				//				LockMethod.DEPTH_INFINITY
				//				LockMethod.SCOPE_EXCLUSIVE
				// neanche il proprietario.... tutto in default !!!
				
				WebdavResource temp = list[filedir.getSelectedIndex()-1];	
				if (temp.isLocked()) {
					logger.debug("Unlock: " + temp.getDisplayName());
					if (!temp.unlockMethod())
						utilMsg.msgError("connect.dav.unlock.error");
				}
				else {
					logger.debug("Lock: " + temp.getDisplayName());
					if (!temp.lockMethod())
						utilMsg.msgError("connect.dav.lock.error");
				}
				readDirDAV("");
			} catch (HttpException we) {
            	//utilMsg.msgError("Error " + we.getStatusCode() + ": " + we.getMessage());
				utilMsg.msgError("Error " + we.getReasonCode() + ": " + we.getMessage());
            } catch (Exception we) {
            	utilMsg.msgError("Error: " + we.getMessage());
            }
		}
	}
	
	private void initField(String connectionType) {
		type = connectionType;
		etiDir.setVisible(false);
		if (type.equals("ftp")) {			
			host.setText(lastFtpHost);
			user.setText(lastFtpUser);
			password.setText(lastFtpPass);
			dirFtp="/";
		}
		if (type.equals("webdav")) {
			host.setText(lastDavHost);
			user.setText(lastDavUser);
			password.setText(lastDavPass);
		}
		typehost.setText("Host " + connectionType);
		currDir.setText("*** non connesso ***");
		file.setText("");
		file.setEnabled(false);
		filedir.removeMouseListener(mouseAdapter);
		
		listModel.removeAllElements();
		lockUnlock.setVisible(false);
	}
	
	protected class FtpOpenAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			
			initField("ftp");
			form.showDialog();
			if (form.isOk())
				if (!readFTP(file.getText(),""))
					utilMsg.msgError("connect.ftp.read.error");
			closeFTP();
		}
	}

	protected class FtpSaveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			
			initField("ftp");
			form.showDialog();
			if (form.isOk())
				if (!writeFTP(file.getText(),""))
					utilMsg.msgError("connect.ftp.wite.error");
			closeFTP();
		}
	}
	
	protected class WebdavOpenAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			initField("webdav");
			form.showDialog();
			if (form.isOk())
				if (!readDAV(file.getText(),""))
					utilMsg.msgError("connect.webdav.read.error");
			closeDAV();
		}
	}
	
	protected class WebdavSaveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {

			initField("webdav");
			form.showDialog();
			if (form.isOk())
				if (!writeDAV(file.getText(),""))
					utilMsg.msgError("connect.webdav.wite.error");
			closeFTP();
		}
	}
	
	
	
	//Funzioni WebDAV	
	private boolean openDAV(String url, String user, String password) {
				
		if (url.substring(url.length()-1, url.length()).equals("/"))
			url=url.substring(0, url.length()-1);
		currDir.setText(">>> Mi connetto ...");
        try {        	
        	if (url.startsWith("https"))
        		httpURL = new HttpsURL(url);
        	else
        		httpURL = new HttpURL(url);
            if (webdavResource == null) {
                webdavResource = new WebdavResource(httpURL);
                webdavResource.setDebug(0);
            } else {
                webdavResource.close();
                webdavResource.setHttpURL(url);
            }

        } catch (HttpException we) {
        	//if (we.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
        	if (we.getReasonCode() == HttpStatus.SC_UNAUTHORIZED) {
        		try {
                    try {
                        if (webdavResource != null)
                            webdavResource.close();
                    } catch (IOException e) {
                    } finally {
                        httpURL = null;
                        webdavResource = null;
                    }
                	if (url.startsWith("https"))
                		httpURL = new HttpsURL(url);
                	else
                		httpURL = new HttpURL(url);
                    //httpURL.setUserInfo(user, password);
                    httpURL.setUserinfo(user, password);
                    if (webdavResource != null) {
                        webdavResource.setHttpURL(url);                        
                    } else {
                        webdavResource = new WebdavResource(httpURL);
                        webdavResource.setDebug(0);
                    }
       
                } catch (HttpException e) {
                	//utilMsg.msgError("Error " + e.StatusCode() + ": " + e.getMessage());
                	utilMsg.msgError("Error " + e.getReasonCode() + ": " + e.getMessage());
                    httpURL = null;
                    currDir.setText("*** non connesso ***");
        		    return false;
                } catch (IOException e) {
                	utilMsg.msgError("Error: " + e.getMessage());
                    httpURL = null;
                    currDir.setText("*** non connesso ***");
        		    return false;
                }
        	}	
        	else {
        		//utilMsg.msgError("Error " + we.getStatusCode() + ": " + we.getMessage());
        		utilMsg.msgError("Error " + we.getReasonCode() + ": " + we.getMessage());
        		httpURL = null;
        		currDir.setText("*** non connesso ***");
    		    return false;
            }		
        } catch (Exception e) {
        	utilMsg.msgError("Error: " + e.getMessage());
            httpURL = null;
            currDir.setText("*** non connesso ***");
		    return false;
        }			
        
        etiDir.setVisible(true);
        currDir.setText(webdavResource.getPath());
        filedir.addMouseListener(mouseAdapter);
        file.setEnabled(true);
	    return true;
		
	}
	
	private boolean closeDAV(){	
		try {
			if (webdavResource != null)
				webdavResource.close();
		} catch (IOException e) {
		} finally {
			httpURL = null;
        	webdavResource = null;
		}
		return true;
	}
			
	private boolean writeDAV(String fileName, String dir){	
		try {
			if (fileName.equals(""))
				return false;
			if (!cwdDAV(dir))
				return false;
			
			
			File file=new File("temp/tempDAV.xml");
			String encoding;
			if (documentManager.getEncoding() == null) {
				logger.warn("No encoding found. Using default: UTF-8");
				encoding = "UTF-8";
			} else
				encoding = documentManager.getEncoding();
			DOMWriter domWriter = new DOMWriter();
			domWriter.setCanonical(false);
			domWriter.setFormat(true);
			try {
				domWriter.setOutput(file, encoding);
				domWriter.write(documentManager.getDocumentAsDom());
				documentManager.setChanged(false);
				documentManager.setSourceName(file.getAbsolutePath());
				documentManager.setNew(false);
			} catch (UnsupportedEncodingException ex) {
				utilMsg.msgError("action.file.save.error.encoding");
				logger.error(ex.toString(), ex);
			} catch (FileNotFoundException ex) {
				utilMsg.msgError("action.file.save.error.file");
				logger.error(ex.toString(), ex);
			}			
			UtilFile.copyFile("temp/tempDAV.xml", "temp/tempDAV.zip");
			File file2= UtilFile.getFileFromTemp("tempDAV.zip");
			
			
			
			logger.debug("Write: " + httpURL.getScheme()+"//"+httpURL.getHost()+":"+httpURL.getPort()+webdavResource.getPath()+"/"+fileName);			
			
			//STRATEGIA beinformed
			webdavResource.unlockMethod(webdavResource.getPath()+"/"+fileName, password.getText());
						
			//if (!webdavResource.putMethod(webdavResource.getPath()+"/"+fileName, file))
			if (!webdavResource.putMethod(webdavResource.getPath()+"/"+fileName, file2))
				utilMsg.msgError(webdavResource.getStatusCode() + ": " + webdavResource.getStatusMessage());
				
			
			
						
        } catch (HttpException we) {
        	//utilMsg.msgError("Error " + we.getStatusCode() + ": " + we.getMessage());
        	utilMsg.msgError("Error " + we.getReasonCode() + ": " + we.getMessage());
			return false;							
		} catch (IOException e) {
			utilMsg.msgError("Error: " + e.getMessage());
			return false;
		}
		return true;
	}

	private boolean readDirDAV(String dir){	
		try {
			if (!cwdDAV(dir))
				return false;
			listModel.removeAllElements();
			list = webdavResource.listWebdavResources();

			//scommentare per le info su Date Size ..
//			Date data = new Date();
			listModel.addElement("DIR:  ..");
			String pre;
            for (int i = 0; i < list.length; i++) {            	
            	if (list[i].isLocked())
            		pre = "Lock! ";
            	else if (list[i].isCollection())
            		pre = "DIR:  ";
            	else pre = "File: ";
//            	data = new Date(list[i].getGetLastModified());
//              listModel.addElement(pre + list[i].getDisplayName() + " " + list[i].getGetContentType() + " " + list[i].getGetContentLength() + " " + data.toLocaleString()); 
                listModel.addElement(pre + list[i].getDisplayName());
            }
            
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private boolean readDAV(String fileName, String dir){	
		try {
			if (fileName.equals(""))
				return false;
			if (!cwdDAV(dir))
				return false;			
			File file=new File("temp/"+fileName);
			
			logger.debug("Read: " + httpURL.getScheme()+"//"+httpURL.getHost()+":"+httpURL.getPort()+webdavResource.getPath()+"/"+fileName);
			
			//STRATEGIA beinformed
			webdavResource.lockMethod(webdavResource.getPath()+"/"+fileName, password.getText(), LockMethod.DEPTH_INFINITY, LockMethod.SCOPE_EXCLUSIVE);

			if (!webdavResource.getMethod(webdavResource.getPath()+"/"+fileName, file))
				utilMsg.msgError(webdavResource.getStatusCode() + ": " + webdavResource.getStatusMessage());				

			
			
			fileOpenAction.doOpen(file.getAbsolutePath(), false);

        } catch (HttpException we) {
        	//utilMsg.msgError("Error " + we.getStatusCode() + ": " + we.getMessage());
        	utilMsg.msgError("Error " + we.getReasonCode() + ": " + we.getMessage());
			return false;										
		} catch (IOException e) {
			return false;
		}
		return true;
	}
			
	private boolean cwdDAV(String dir){	
		try {
			if (!dir.equals(""))
				if (dir.equals("..")) {
					webdavResource.setPath(webdavResource.getPath().substring(0,webdavResource.getPath().lastIndexOf('/')));
					if (webdavResource.getPath().length()<30)
    					currDir.setText(webdavResource.getPath());
    				else
    					currDir.setText("... " + webdavResource.getPath().substring(webdavResource.getPath().length()-30,webdavResource.getPath().length()));					
				}	
				else {			 
					webdavResource.setPath(webdavResource.getPath()+ '/' +  dir);
					if (webdavResource.exists()) 
		                if (webdavResource.isCollection()) { 
		    				if (webdavResource.getPath().length()<30)
		    					currDir.setText(webdavResource.getPath());
		    				else
		    					currDir.setText("... " + webdavResource.getPath().substring(webdavResource.getPath().length()-30,webdavResource.getPath().length()));
		                	return true;
		                }	
					return false;					
				}			
		} catch (HttpException we) {
			//utilMsg.msgError("Error " + we.getStatusCode() + ": " + we.getMessage());
			utilMsg.msgError("Error " + we.getReasonCode() + ": " + we.getMessage());
			return false;
		} catch (IOException e) {
			return false;
		}	
		return true;
	}	


	
	//Funzioni FTP
	private boolean openFTP(String url, int port, String user, String password) {
		try {
			ftpClient.openServer(url);
			ftpClient.login(user, password);
			filedir.addMouseListener(mouseAdapter);
			file.setEnabled(true);
			dirFtp = ".";
			currDir.setText(dirFtp);
			etiDir.setVisible(true);
		    return true;
		}
		catch (IOException e) {
		    return false;
		}		
	}
	
	private boolean closeFTP(){	
		try {
			ftpClient.closeServer();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
			
	private boolean writeFTP(String fileName, String dir){	
		try {
			if (fileName.equals(""))
				return false;
			if (!cwdFTP(dir))
				return false;
			
			File file=new File("temp/tempFTP.xml");
			
			
			//TODO utilizzare (SE POSSIBILE) qualcosa del tipo 
			//fileSaveAction.doSave();  o il metodo privato:  saveFile(File file);
			
			String encoding;
			if (documentManager.getEncoding() == null) {
				logger.warn("No encoding found. Using default: UTF-8");
				encoding = "UTF-8";
			} else
				encoding = documentManager.getEncoding();
			DOMWriter domWriter = new DOMWriter();
			domWriter.setCanonical(false);
			domWriter.setFormat(true);
			try {
				domWriter.setOutput(file, encoding);
				domWriter.write(documentManager.getDocumentAsDom());
				documentManager.setChanged(false);
				documentManager.setSourceName(file.getAbsolutePath());
				documentManager.setNew(false);
			} catch (UnsupportedEncodingException ex) {
				utilMsg.msgError("action.file.save.error.encoding");
				logger.error(ex.toString(), ex);
			} catch (FileNotFoundException ex) {
				utilMsg.msgError("action.file.save.error.file");
				logger.error(ex.toString(), ex);
			}

			
			String nomeFileDest=fileName;
			OutputStream out = ftpClient.put(nomeFileDest);
			InputStream in = new FileInputStream(file);
			byte c[] = new byte[4096];
			int read = 0;
			while ((read = in.read(c)) != -1 )
				out.write(c, 0, read);			
			in.close();
			out.close();
							
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private boolean readDirFTP(String dir){	
		try {
			if (!cwdFTP(dir))
				return false;
			listModel.removeAllElements();
		    TelnetInputStream lst = ftpClient.list();
		    
		    //if (!ftpClient.pwd().equals("/"))		LO COMMENTO molti ftp piantano la connessione su questa richiesta
		    	listModel.addElement("DIR:  ..");
		    String str = "";
		    String etiDir;
		    while (true) {
			  etiDir = "DIR:  ";
		      int c = lst.read();
		      char ch = (char) c;
		      if (c < 0 || ch == '\n') {

		          StringTokenizer tk = new StringTokenizer(str);
		          if (tk.hasMoreTokens() && !tk.nextToken().substring(0,1).equals("d"))
		        	  etiDir="File: ";
		          String nomeFile="";
		          while (tk.hasMoreTokens()) 
		            nomeFile = tk.nextToken();

		          if (!nomeFile.equals("")) 
		        	  listModel.addElement(etiDir + nomeFile);
		          str = "";
		      }
		      if (c <= 0)
		        break;
		      str += ch;
		    }
		    
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private boolean readFTP(String fileName, String dir){	
		try {
			if (fileName.equals(""))
				return false;
			if (!cwdFTP(dir))
				return false;
			
			File file=new File("temp/"+fileName);
			
			InputStream in = ftpClient.get(file.getName());
			OutputStream out = new FileOutputStream(file);
			byte c[] = new byte[4096];
			int read = 0;
			while ((read = in.read(c)) != -1 )
				out.write(c, 0, read);
			in.close();
			out.close();
			
			fileOpenAction.doOpen(file.getAbsolutePath(), false);
									
		} catch (Exception e) {
			return false;
		}
		return true;
	}
			
	private boolean cwdFTP(String dir){	
		try {
			if (!dir.equals("")) {
				ftpClient.cd(dir);
				if (dir.equals(".."))
					if (dirFtp.lastIndexOf('/')!=-1)
						dirFtp = dirFtp.substring(0,dirFtp.lastIndexOf('/')-1);
					else
						dirFtp = ".";
				else
					dirFtp = dirFtp+"/"+dir;
				if (dirFtp.length()<30)
					currDir.setText(dirFtp);
				else
					currDir.setText("... " + dirFtp.substring(dirFtp.length()-30,dirFtp.length()));
			}	
		} catch (IOException e) {
			return false;
		}	
		return true;
	}	
				    
	
}

