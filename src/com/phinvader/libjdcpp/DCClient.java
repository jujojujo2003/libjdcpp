package com.phinvader.libjdcpp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * This is the core main class which serves as the DC Client , all operations
 * occur through interfacing with a connected instance of this object. The
 * DCClient must be connected before any operations can take place
 * 
 * @author phinfinity
 * @see DCPreferences
 */
public class DCClient {
	InetAddress HubAddress;
	int HubPort;

	private Socket DCConnectionSocket;
	private MessageHandler handler;
	private UsersHandler mainUserHandler;
	private MessageRouter mainMessageRouter;
	public DCDownloader downloadHandler;
	public DCHandlers.MainDownloadHandler mainDownloader;
	public DCRevconnect revConnectHandler;
	private DCHandlers.BoardMessageHandler mainBoardMessageHandler;

	// BASIC ALIASES to other classes.

	public static class NotifyUsersChange extends UsersHandler {
		/* Just a stub */
	}

	public static class DCBasicDownloadHandler
			extends
				DCDownloader.DownloadQueueEntity {

		public DCBasicDownloadHandler(DCUser target_user, DCUser my_user,
				String remote_filename, String local_filename) {
			super(target_user, my_user, remote_filename, local_filename);
		}
	}

	public static class PassiveDownloadConnection extends DCRevconnect {

		public PassiveDownloadConnection(DCUser target_user, DCUser my_user,
				DCPreferences prefs, String local_filename,
				String remote_filaname, DCClient client) {
			super(target_user, my_user, prefs, local_filename, remote_filaname,
					client);
		}
		/* Just a stub */
	}

	public static class BasicCallbackHandler implements DCCommand {

		@Override
		public void onCommand(DCMessage msg) {
			/* This is just a stub */
		}

	}

	/**
	 * 
	 * This function is used to connect to a DC-enabled hub on the specified ip
	 * address and port. The DCClient object must first be connected before any
	 * other operations can take place
	 * 
	 * @param ip
	 *            - IP Address of Server to Connect to
	 * @param port
	 *            - Port Number to connect to (default 411)
	 * 
	 * @param pref
	 *            - Preference object containing user prefences , such as
	 *            nickname
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws InterruptedException
	 * 
	 * @see DCClient#connect(String, DCPreferences)
	 * 
	 */
	public void connect(String ip, int port, DCPreferences pref)
			throws UnknownHostException, IOException, InterruptedException {

		DCConnectionSocket = new Socket(ip, port);
		handler = new MessageHandler(DCConnectionSocket);
		DCMessage lock = handler.getNextMessage();
		DCMessage hubname = handler.getNextMessage();
		DCLogger.Log("Connected to :" + hubname);

		ArrayList<String> supported_methods = new ArrayList<String>();
		supported_methods.add("NoHello");
		supported_methods.add("NoGetINFO");

		handler.send_supports(supported_methods);
		handler.send_key(DCFunctions.convert_lock_to_key(lock.lock_s.getBytes()));

		handler.send_validatenick(pref.getNick());

		while (true) {
			DCMessage msg = handler.getNextMessage();
			DCLogger.Log(msg.toString());
			if (msg.command != null)
				if (msg.command.equals("Hello")
						&& msg.hello_s.equals(pref.getNick()))
					break;
		}

		handler.send_version();
		handler.send_getnicklist();

		DCUser myuser = new DCUser();
		myuser.nick = pref.getNick();
		myuser.share_size = pref.getShare_size();

		handler.send_myinfo(myuser);
	}

	/**
	 * To start all transactions.
	 */
	public void bootstrap(DCUser myuser) {
		mainMessageRouter = new MessageRouter(handler);
		mainUserHandler = new UsersHandler();
		mainBoardMessageHandler = new DCHandlers.BoardMessageHandler();
		downloadHandler = new DCDownloader();
		mainDownloader = new DCHandlers.MainDownloadHandler(myuser,
				downloadHandler, this);
		downloadHandler.initDownloadQ();
	}

	/**
	 * Start listening to Messages
	 */
	public void InitiateDefaultRouting() {

		mainMessageRouter.subscribe("MyINFO", mainUserHandler);
		mainMessageRouter.subscribe("Quit", mainUserHandler);
		mainMessageRouter.subscribe("BoardMessage", mainBoardMessageHandler);
		mainMessageRouter.subscribe("ConnectToMe", mainDownloader);
		Thread routing_thread = new Thread(mainMessageRouter);
		routing_thread.start();
	}

	/**
	 * set handlers to change userlist
	 * 
	 * @param handler
	 */
	public void setUserChangeHandler(DCCommand handler) {
		mainMessageRouter.subscribe("MyINFO", handler);
		mainMessageRouter.subscribe("Quit", handler);
	}

	/**
	 * Custom callback for changes in userlist
	 * 
	 * @param handler
	 */
	public void setCustomUserChangeHandler(DCCommand handler) {
		mainMessageRouter.customSubscribe("MyINFO", handler);
		mainMessageRouter.customSubscribe("Quit", handler);
	}

	public DCDownloader.DownloadQueueEntity startPassiveDownload(
			PassiveDownloadConnection o, int timeout)
			throws InterruptedException {
		DCDownloader.DownloadQueueEntity downloadE = new DCDownloader.DownloadQueueEntity(
				o.getTarget_user(), o.getMy_user(), o.getRemote_filaname(),
				o.getLocal_filename());
		downloadHandler.addDownloadEntity(o.getTarget_user().nick, downloadE);
		handler.send_revconnect(o.getMy_user(), o.getTarget_user());
		return downloadE;
	}

	public boolean stopDownloadHandler(
			DCDownloader.DownloadQueueEntity downloadEntity) {
		// TODO: A better shutdown?
		downloadEntity.connectionHandler.close();
		if (downloadEntity.thread.isAlive()) {
			downloadEntity.thread.interrupt();
		} else {
			return false;
		}

		return true;
	}
	public DCDownloader.DownloadQueueEntity startPassiveDownload(
			PassiveDownloadConnection o) throws InterruptedException {
		return startPassiveDownload(o, 1000);
	}

	public void unsetPassiveDownloadHandler(DCCommand o) {
		DCLogger.Log("UNSETTING" + o.toString());
		mainMessageRouter.unsubscribe("ConnectToMe", o);
	}

	public void setSearchHandler(DCCommand handler) {
		mainMessageRouter.subscribe("SR", handler);
	}
	public void setCustomSearchHandler(DCCommand handler) {
		mainMessageRouter.customSubscribe("SR", handler);
	}

	public void setCustomBoardMessageHandler(DCCommand handler) {
		mainMessageRouter.subscribe("BoardMessage", handler);
	}

	public void setCustomRawHandler(String command, DCCommand handler) {
		mainMessageRouter.subscribe(command, handler);
	}

	public void searchForFile(String key, DCUser myuser) {
		handler.send_search(key, myuser);
	}

	public void startDownloadingFile(DCDownloader.DownloadQueueEntity entity,
			DCClient client, DCMessage rlock, MessageHandler handler) {

		downloadHandler = new DCDownloader();

		DCDownloader.downloadManager dm = downloadHandler.new downloadManager(
				entity, rlock, handler);
		Thread dm_thread = new Thread(dm);
		dm_thread.start();

	}

	/**
	 * Connects using the default port 411.
	 * 
	 * @param ip
	 *            - IP Address of Server to Connect to
	 * 
	 * @param pref
	 *            - Preference object containing user prefences , such as
	 *            nickname
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws InterruptedException
	 * 
	 * @see DCClient#connect(String, int, DCPreferences)
	 */
	public void connect(String ip, DCPreferences pref)
			throws UnknownHostException, IOException, InterruptedException {
		connect(ip, 411, pref);
	}

	/**
	 * @return The list of users registered with this hub.
	 */
	public ArrayList<DCUser> get_nick_list() {
		ArrayList<DCUser> nick_array = new ArrayList<DCUser>();
		Iterator<DCUser> it = mainUserHandler.nick_q.iterator();
		while (it.hasNext()) {
			nick_array.add(it.next());
		}
		return nick_array;
	}
	public List<DCMessage> getBoardMessages() {
		return mainBoardMessageHandler.getLatestMessages();
	}
	public List<DCMessage> getBoardMessages(int limit) {
		return mainBoardMessageHandler.getLatestMessages(limit);
	}
	public String getBoardMessageLog() {
		return mainBoardMessageHandler.toString();
	}
}
