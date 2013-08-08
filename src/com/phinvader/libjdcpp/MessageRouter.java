package com.phinvader.libjdcpp;

public class MessageRouter {
	
	public static void startRouting(MessageHandler handler) throws InterruptedException{

		while (true) {
			DCMessage msg = handler.getNextMessage();
			
			//if(msg!=null)
			//    DCLogger.Log("GOT :"+msg.toString());
			
			
			if (msg.command != null && msg.command.equals("MyINFO")) {
				UsersHandler.addNick(msg);
			} else if (msg.command != null && msg.command.equals("HubQuit"))
				break;
			
			else {
				
				// When a user quits, msg = "Quit : NICK"
				// No associated command.
				String[] quit_nick = msg.toString().split(":");
				if(quit_nick[0].trim().equals("Quit")){
					UsersHandler.deleteNick(quit_nick[1].trim());
				}
				
			}
		}

		
	}

	
}
