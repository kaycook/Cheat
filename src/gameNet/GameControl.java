package gameNet;

/**
 * 
 * 
 *
 * The <b>GameControl</b> class provides the overall framework for gameNet.  
 * This class has a constructor for starting up a server to host a game.
 * Another constructor is available for connecting to an existing server. 
 *  
 */
public class GameControl 
{   
    int serverPortNum = 54321; // Starting point for Server Port
    GameServer gameServer = null;
    private String ipAddr=null;
    GameNet_CoreGame coreGame = null;
    GameCreator gameCreator = null;
    
    /**
     * This is method returns the IP address used for this game.
     * @return IP address for this game
     */
    String getIpAddress()
    {
    	return ipAddr;
    }
    /**
     * This is method returns the port number used for this game.
     * @return port number for this game
     */
    int getPortNum()
    {
    	return serverPortNum;
    }
    /**
     * This constructor is used when everything is on the same computer and you
     * aren't needing to network.  See myGame2 in the tictactoe example.
     * 
     * 
     */
    GameControl(GameCreator gc)
    {
    	gameCreator = gc;
    }
    /**
     * This is used to connect to a 
     * server that lives elsewhere.
     * 
     * @param ipAddr is the IP address needed to connect to the desired Server
     * @param serverPortNum is the port number needed to connect to the desired Server
     * 
     */

    void connect_to_server(String ipAddr, int serverPortNum)
    {
    	this.ipAddr=ipAddr;
    	this.serverPortNum=serverPortNum;
    }
    
      

    /**
     * If your GameControl requires a thread to return updates that are not related to 
     * user inputs, then this method must be called to send the updates back 
     * to the GUI.  For example, a Pong game would need this to send ball updates
     * that are not directly associated with  a user input. 
     * 
     * @param GameOutputObj is an output that is returned to the GUI from the GameControl
     */

    public void putMsgs(Object ob)
    {
        if (gameServer!= null)
            gameServer.putOutputMsgs(ob);
    }

   
    
    /**
     * Use this constructor if the Server for this game lives in this program. 
     * Note that the actual variable createServer is not actually used. 
     * 
     */
    void startServer()
    {          
        try
        {                 
        	coreGame = gameCreator.createGame();
            gameServer = new GameServer(serverPortNum, coreGame);
            gameServer.start();
           
        	coreGame.startGame(this);
            
            // Note that getPortNum will not return until the server has 
            // started.  If for some reason we can't start the server 
            // after 20 port numbers, an exception will occur
            
            serverPortNum = gameServer.getPortNum()  ; 
            ipAddr= gameServer.inetAddress;
            
            System.out.println("Starting GameControl Server ipAddress("+ipAddr + 
                ")  portNum ("+ serverPortNum + ")");
             
        } 
        catch (RuntimeException e)
        {
            System.out.println("GameControl: Runtime Exception:" + e);
        }
 
    }
    
    

    
    
    /**
     * endGame will shutdown a server. 
     * 
     */
    
    void endGame()
    {
       
        if (gameServer != null)
        {
            System.out.println("endGame " );
            gameServer.stopServer();
        }
            

    }
    
    
    
    
    
        
}
