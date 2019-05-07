package gameNet;

import java.util.Scanner;

public abstract class GameCreator {
	public abstract GameNet_CoreGame createGame();
	
	public void enterGame(GameNet_UserInterface ui)
	{
		String playerName;
		GamePlayer gamePlayer; 
		GameControl gameControl = new GameControl(this);
		
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter your name:");
		playerName = keyboard.next();
	    System.out.println("Server side of game?(y/n)");
	    String str = keyboard.next();
	    
	    if (str.charAt(0) == 'y')
	    {
	    	gameControl.startServer(); // Start a Server GameControl
	    }
	    else
	    {
	    	System.out.println("Enter ipaddress:");
	    	String ipaddr = keyboard.next();
	    	int port;
	    	System.out.println("Enter port number:");
	    	port = keyboard.nextInt();
	    	gameControl.connect_to_server(ipaddr,port);
	    }
	    
	 // Connect ourselves to the GameControl
	    
	    
	    gamePlayer = new GamePlayer(playerName, gameControl, ui);
	  
	    ui.startUserInterface (gamePlayer);
	    keyboard.close();
	     
	}

}
