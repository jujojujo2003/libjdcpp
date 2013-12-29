package com.phinvader.libjdcpp;

/**
 * This is a class just to store all related constants
 * 
 * @author phinfinity
 * 
 */
public class DCConstants {
	public static String version = "libjdcpp v1.0";
	public static String version_short = "1.0";
	public static String default_lock = "LIBJDCPPDOESNOTKNOWWHATLOCKTOGIVEU";
	public static String default_pk = "libjdcppv1.0----";
	public static enum DownloadStatus {
		UNDEFINED, INITIATED, STARTED, INTERUPTED, SHUTDOWN, FAILED, DOWNLOADING, COMPLETED
	};

	/**
	 * This is the maximum number of bytes per chunk sent while transferring
	 * files over the TCP connection. (As specified by the DC protocol)
	 */
	public static long data_chunk_size = 40906;
	/**
	 * This is the maximum number of messages that may be Queued in a given
	 * MessageHandler Instance. If more messages are arrive then the TCP Socket
	 * will block and wait till the message Queue is freed
	 */
	public static int max_message_queue_size = 1024;
	public static int max_download_queue_size = 1024;

	public static int io_buffer_size = 65536;
	
	public static int FILETYPE_AUDIO = 2;
	public static int FILETYPE_COMPRESSED = 3;
	public static int FILETYPE_DOCUMENT = 4;
	public static int FILETYPE_EXE = 5;
	public static int FILETYPE_IMAGE = 6;
	public static int FILETYPE_VIDEO = 7;
	public static int FILETYPE_DIR = 8;
	public static int FILETYPE_TTH = 9;
	public static int FILETYPE_ANY = 1;
	
}
