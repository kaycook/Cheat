package cheat;

import java.io.Serializable;
import java.util.ArrayList;

class Track implements Serializable{
    private static final long serialVersionUID = 1L;
    int num;
    CardType type;
    
    Track(int num, CardType card){
        this.num = num;
        type = card;
    }
    
    public String toString(){
        return num + " of " + type;
    }
}

public class Computer extends Player implements Serializable {

    private static final long serialVersionUID = 1L;
    int which;
    ArrayList<Card> choices = new ArrayList<Card>();
    ArrayList<Track> past = new ArrayList<Track>();
    double callChance = .1;
    double cheatChance = 2;

    Computer(String n) {
        super(n);
    }
    
    void makeNewCompPlayer(Player player){
        currHand = player.currHand;
        
    }
    
    void forget(){
        
    }


}
