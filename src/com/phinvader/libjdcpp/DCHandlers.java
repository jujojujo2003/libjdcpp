package com.phinvader.libjdcpp;

import java.util.ArrayList;
import java.util.List;

public class DCHandlers {
	public static class BoardMessageHandler implements DCCommand{

		List<DCMessage> listOfMessages = new ArrayList<DCMessage>();
		
		@Override
		public void onCommand(DCMessage msg) {
			listOfMessages.add(msg);			
		}
		
		public List<DCMessage> getLatestMessages(){
			return getLatestMessages(100);
		}
		
		public List<DCMessage> getLatestMessages(int limit){
			int lowerBound = listOfMessages.size()-1-limit;
			int upperBound = listOfMessages.size()-1;
			if(limit > listOfMessages.size()){
				lowerBound = 0 ; 
			}
			return listOfMessages.subList(lowerBound, upperBound);
			
						
		}
		
		public String toString(){
			int size = listOfMessages.size();
			String logString = "Size : "+Integer.toString(listOfMessages.size());
			if( size > 5){
				logString += "..."+listOfMessages.get(size-4)+","+listOfMessages.get(size-3)+","+listOfMessages.get(size-2)+","+listOfMessages.get(size-1);
			}
			return logString;
		}
		
	}

}
