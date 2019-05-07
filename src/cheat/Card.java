package cheat;

import java.io.Serializable;

import javax.swing.ImageIcon;

enum CardType {Ace, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King};
enum CardSymbol {Clubs, Diamonds, Hearts, Spades};
enum CardColor { Red, Black};

public class Card implements Serializable,Comparable<Card> {

    private static final long serialVersionUID = 1L;

    CardType type;
    CardSymbol symbol;
    CardColor color;
    ImageIcon image;

    Card(CardType type, CardSymbol symbol, CardColor color){
        this.type = type;
        this.symbol = symbol;
        this.color = color;
        createImage();
    }

    private void createImage(){
        String imageName = type.toString() +
                symbol.toString()+ color.toString() +".gif"; 
        image = new ImageIcon(imageName);
    }

    public String toString(){
        return "Type: " + type + " Symbol: " + symbol;

    }

    public int compareTo(Card obj){
        Card c = (Card)obj;
        
        int retVal = -1;
        if(type.ordinal() > (c.type.ordinal())){
            retVal = 1;
        } else{ 
            if(type.ordinal() < (c.type.ordinal())){
                retVal = -1;
            } else{
                if(type.ordinal() == (c.type.ordinal())){
                    retVal = 0;
                }
            } 
        }
        if (retVal != 0) return retVal;
        if(symbol.ordinal() > (c.symbol.ordinal())){
            retVal = 1;
        } else{ 
            if(symbol.ordinal() < (c.symbol.ordinal())){
                retVal = -1;
            } else{
                if(symbol.ordinal() == (c.symbol.ordinal())){
                    retVal = 0;
                }
            }
        }
        return retVal;
        }

        public boolean equals(Object o){
            Card c = (Card)o;
            if(symbol == c.symbol){
                if(type == c.type){
                    return true;
                }
            }
            return false;
        }

        public int hashCode(){
            int i = 0;
            switch(type){
            case Ace: i+=1; break;
            case Two: i+=2; break;
            case Three: i+=3; break;
            case Four: i+=4; break;
            case Five: i+=5; break;
            case Six: i+=6; break;
            case Seven: i+=7; break;
            case Eight: i+=8; break;
            case Nine: i+=9; break;
            case Ten: i+=10; break;
            case Jack: i+=11; break;
            case Queen: i+=12; break;
            case King: i+=13; break;
            }
            switch(symbol){
            case Clubs: i*=1; break;
            case Spades: i*=2; break;
            case Hearts: i*=3; break;
            case Diamonds: i*=4; break;
            }
            return i;

        }
    }
