package cheat;

import java.io.Serializable;
import java.util.ArrayList;

public class CheatDeck implements Serializable {

    private static final long serialVersionUID = 1L;
    
    ArrayList<Card> Cards;
    CardType[] cardTypes = {CardType.Ace, CardType.Two, CardType.Three, CardType.Four, CardType.Five, CardType.Six, CardType.Seven, CardType.Eight, CardType.Nine, CardType.Ten, CardType.Jack, CardType.Queen, CardType.King};
    CardSymbol[] cardSymbols = {CardSymbol.Clubs, CardSymbol.Diamonds, CardSymbol.Hearts, CardSymbol.Spades};
    
    CheatDeck(){
        Cards = new ArrayList<Card>();
        for(CardType ty: cardTypes){
            for(CardSymbol sym: cardSymbols){
                if(sym == CardSymbol.Clubs || sym == CardSymbol.Spades)
                    Cards.add(new Card(ty, sym, CardColor.Black));
                else
                    Cards.add(new Card(ty, sym, CardColor.Red));
            }
        }
        shuffle();
    }
    
    void shuffle(){
        Card temp;
        int random;
        for(int i = 0; i<Cards.size(); i++){
            temp = Cards.get(i);
            random = (int) (Math.random() * Cards.size() );
            Cards.set(i, Cards.get(random));
            Cards.set(random, temp);
        }
    }
    
    void print(){
        for(int i = 0; i<Cards.size(); i++){
            System.out.println(Cards.get(i));
        }
    }

}
