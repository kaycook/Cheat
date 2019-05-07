package gameNet;



/**
 * Your GUI will likely want to implement this interface to ever receive
 * updates from the GameControl class. A call will be made to the receivedMessage
 * whenever a new GameOutputObj is being sent to your GUI.
 */
public interface GameNet_UserInterface
{
	public void receivedMessage(Object ob);
	public void startUserInterface (GamePlayer player);
}