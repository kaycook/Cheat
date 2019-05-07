package cheat;

import java.io.Serializable;
import java.util.ArrayList;

public class MyGameInput implements Serializable {

    private static final long serialVersionUID = 1L;
    
    static final int NO_COMMAND=-1;
    static final int JOIN_GAME=1;
    static final int PASS_OUT_CARDS = 2;
    static final int SELECT_CARDS=3;
    static final int PASS_TURN=4;
    static final int CALL_CHEAT= 5;
    static final int LET_GO = 6;
    static final int DISCONNECTING=7;
    static final int RESETTING=8;

    int command = NO_COMMAND;
    ArrayList<Card> choices;
    CardType chosen;

    String myName;

    void setName(String myName){
        this.myName = myName;
    }
    
    void setCards(ArrayList<Card> cards){
        choices = cards;
    }
    
    void setChoice(CardType chose){
        chosen = chose;
    }
    
}
