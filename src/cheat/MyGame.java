package cheat;
import gameNet.GameNet_CoreGame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Player implements Serializable{
    private static final long serialVersionUID = 1L;
    ArrayList<Card> currHand = new ArrayList<Card>();
    String name;
    Player(String n){
        name = n;
    }
    public String toString(){
        return name + " " + currHand.toString();
    }

    public void change(int which, int size){
    }
}

public class MyGame extends GameNet_CoreGame implements Serializable {

    private static final long serialVersionUID = 1L;
    int numOfPlayers = 4;

    String update = " ";

    boolean reset = false;

    int turn=-1;
    CheatDeck deck = new CheatDeck();
    ArrayList<Card> pile = new ArrayList<Card>();
    CardType lastChoice = null;
    CardType playerChoice = null;
    boolean checkingPlayer = false;
    boolean cheat = false;
    String cheater = null;
    ArrayList<Card> cardsInQuestion;
    boolean[] whoAnswered;
    Player won = null;
    int realsizeOfPlayer = 0;

    String update1 = " ";
    String update2 = " ";

    private ArrayList<Player> currPlayers = new ArrayList<Player>();
    private ArrayList<String> playerNames = new ArrayList<String>();
    int compCount = 0;

    enum direction {Left, Top, Right};

    public MyGame(){
    }

    public Object process(Object inputs) {

        MyGameInput myGameInput = (MyGameInput)inputs;

        update2 = " ";
        update1 = " ";

        int clientIndex = getClientIndex(myGameInput.myName);

        if (clientIndex < 0)
        {
            System.out.println("Already have enough players");
            return null;
        }

        
        //checking to see if someone got same index as they did somehow -- checks if names are diff. to find out.
        if(!((currPlayers.get(clientIndex).name).equals(myGameInput.myName))){
            int realIndex = -1;
            for(int i =clientIndex+1; i<currPlayers.size();i++){
                if(currPlayers.get(i).name.equals(myGameInput.myName)){
                    if(currPlayers.get(i) instanceof Computer){
                        compCount-= 1;
                    }
                    realIndex = i;
                }
            }
            if(!(currPlayers.get(clientIndex).name.equals("computer"))){ //if it's a computer
                for(int i = 0; i < playerNames.size(); i++){
                    if(playerNames.get(i).equals("computer")){ //then I go through and find the first computer
                        clientIndex = i;
                        break;
                    }
                }

            }
            if(currPlayers.get(clientIndex) instanceof Computer){
                compCount-= 1;
            }
            Player real = new Player(myGameInput.myName);
            real.currHand = currPlayers.get(realIndex).currHand;
            currPlayers.remove(realIndex);
            real.currHand = currPlayers.get(clientIndex).currHand;
            currPlayers.set(clientIndex, real);
            playerNames.remove(myGameInput.myName);
            System.out.println(playerNames.get(clientIndex));
            playerNames.set(clientIndex, myGameInput.myName);
        }


        switch (myGameInput.command)
        {
        case MyGameInput.JOIN_GAME:
            break;
        case MyGameInput.PASS_OUT_CARDS:
            reset=false;
            passOutCards();
            break;
        case MyGameInput.SELECT_CARDS:
            makeSelection(myGameInput.choices, myGameInput.chosen);
            whoAnswered[clientIndex] = true;
            break;
        case MyGameInput.PASS_TURN:
            System.out.println("Passed");
            update2 = getYourPlayer(turn).name + " Passed their turn";
            turn = (turn+1)%numOfPlayers;
            break;
        case MyGameInput.CALL_CHEAT:
            if(!whoAnswered[clientIndex]&& checkingPlayer ==true){
                checkingPlayer = false;
                whoAnswered[clientIndex] = true;
                cheatCalled(clientIndex);
            }
            break;
        case MyGameInput.LET_GO:
            if(!whoAnswered[clientIndex]&& checkingPlayer ==true){
                whoAnswered[clientIndex] = true;
                checkIfDone();
            }
            break;
        case MyGameInput.DISCONNECTING:
            int i = getYourIndex(myGameInput.myName);
            if(turn!=-1){
                playerNames.set(i, "computer");    
                Computer comp = new Computer("Computer"+i);
                comp.makeNewCompPlayer(currPlayers.get(i));
                currPlayers.set(i, comp);
                compCount+=1;
            } else{
                playerNames.remove(i);
                currPlayers.remove(i);
            }
            realsizeOfPlayer -= 1;
            
            break;
        case MyGameInput.RESETTING:
            resetGame();   
            reset=true;
            break;
        default: /* ignore */
        }

        if(checkingPlayer==true){
            checkIfDone();
        }

        if(checkingPlayer == false && turn >=0)
            checkIfGameOver();

        if(won!=null){
            update = won.name + " won the game!";
            turn = -1;
        }
        else{
            isComputerTurn();
            update = update1 + " " + update2;
        }

        // Send game back to all clients
        MyGameOutput myGameOutput = new MyGameOutput(this);
        return myGameOutput;
    }

    private void resetGame() {
        turn = -1;
        deck = new CheatDeck();
        pile = new ArrayList<Card>();
        lastChoice = null;
        playerChoice = null;
        checkingPlayer = false;
        cheat = false;
        cheater = null;
        cardsInQuestion = new ArrayList<Card>();
        won = null;
        update = " ";
        int whereShouldStop = ((currPlayers.size()-1)-compCount);
        for(int i = (currPlayers.size()-1);i>whereShouldStop;i--){
            currPlayers.remove(i);
        }
        compCount = 0;
        for(int i = 0; i < currPlayers.size(); i++){
            currPlayers.get(i).currHand.removeAll(currPlayers.get(i).currHand);
        }
    }

    private void checkIfGameOver() {
        for(int i = 0; i< currPlayers.size();i++){
            if(currPlayers.get(i).currHand.size() ==0){
                won = currPlayers.get(i);
                /*ArrayList<Card> temp = new ArrayList<Card>();
                temp.addAll(pile);
                for(int k = 0; k<currPlayers.size();k++){
                    temp.addAll(currPlayers.get(k).currHand);
                }
                Collections.sort(temp);
                System.out.println(temp);*/
                return;
            }
        }
    }

    private void checkIfDone() { //finds out if done checking players. If all human players done, will have comps take their turns
        boolean done = true;
        int i =0;
        for(; i<whoAnswered.length;i++){
            if(whoAnswered[i]){

            } else{
                if(currPlayers.get(i) instanceof Computer){}
                else{
                    done=false;
                }
            }
        }
        if(done==false){
            return;
        }
        if(compCount != 0){
            for(i = 0; i<currPlayers.size();i++){
                if(i != turn && currPlayers.get(i) instanceof Computer){
                    Computer comp = (Computer)currPlayers.get(i);
                    int what = chooseWhatToCall(comp);
                    if(what == MyGameInput.CALL_CHEAT){
                        checkingPlayer = false;
                        int size = comp.currHand.size();
                        cheatCalled(i);
                        if(size < comp.currHand.size()){
                            comp.callChance -= 1;
                        }else{
                            comp.callChance+=.2;
                        }
                        if(comp.callChance<=0){
                            comp.callChance=.5;
                        }
                        return;
                    }
                }
            }
        }
        update1 = "No one called a cheat on " + getYourPlayer(turn).name;
        checkingPlayer = false;
        turn = (turn+1)%numOfPlayers;
        for(int j = 0; j<whoAnswered.length;j++){
            whoAnswered[j] = false;
        }
        pile.addAll(cardsInQuestion);
        cardsInQuestion = null;
        lastChoice = playerChoice;
    }

    private void makeSelection(ArrayList<Card> choices, CardType chosen) {
        //System.out.println(getYourPlayer(turn).name);
        //System.out.println(getYourPlayer(turn).currHand);
        checkingPlayer = true;
        cardsInQuestion = choices;
        playerChoice = chosen;
        getYourPlayer(turn).currHand.removeAll(choices);
        if(getYourPlayer(turn) instanceof Computer){
            ((Computer)getYourPlayer(turn)).past.add(new Track(cardsInQuestion.size(), playerChoice));
        }
        //System.out.println(getYourPlayer(turn).currHand);
        //System.out.println(cardsInQuestion);
    }

    int getClientIndex(String name){
        int index = playerNames.indexOf(name);

        if(index < 0 && realsizeOfPlayer<=numOfPlayers){
            index = realsizeOfPlayer;
            playerNames.add(name);
            currPlayers.add(new Player(name));
            realsizeOfPlayer +=1;
        } else{
        	if(index < 0)
                index = getYourIndexCurr(name);
        }
        return index;
    }

    
    int getYourIndex(String name){
        return playerNames.indexOf(name);
    }

    int getYourIndexCurr(String name){
        return currPlayers.indexOf(name);
    }

    Player getPlayer(int clientInd, direction dir){//used by myUserInterface to find out the player that goes in a certain spot
        int which = clientInd;
        switch(dir){
        case Top:
            which += 1;
            which = which%4;
            break;
        case Left:
            which += 2;
            which = which%4;
            break;
        case Right:
            which += 3;
            which = which%4;
            break;
        }
        if(currPlayers.size()<=which)
            return new Player(" ");
        return currPlayers.get(which);
    }

    Player getYourPlayer(int index){
        if(index == -1){
            return null;
        }
        return currPlayers.get(index);
    }

    void passOutCards(){
        while(currPlayers.size()!=4){
            Computer comp = new Computer("Computer"+currPlayers.size());
            currPlayers.add(comp);
            compCount +=1;
        }
        numOfPlayers = currPlayers.size();

        for(int card = 0; card<deck.Cards.size();){
            for(int player =0; player<currPlayers.size(); player++){
                currPlayers.get(player).currHand.add(deck.Cards.get(card));
                card+=1;
                if(card >= deck.Cards.size())
                    break;
            }
        }
        turn = (int)(currPlayers.size() * Math.random());
        for(int player =0; player<currPlayers.size(); player++){
            List<Card> temp = currPlayers.get(player).currHand;
            Collections.sort(temp);
        }
        whoAnswered = new boolean[currPlayers.size()];
        for(int i = 0; i<whoAnswered.length;i++){
            whoAnswered[i] = false;
        }
    }

    void cheatCalled(int whoCalled){
        for(int i = 0; i<cardsInQuestion.size();i++){
            if(!(cardsInQuestion.get(i).type.ordinal()==playerChoice.ordinal())){
                cheat = true;
            }
        }
        pile.addAll(cardsInQuestion);
        //System.out.println("Cheat!");
        if(cheat){
            getYourPlayer(turn).currHand.addAll(pile);
            Collections.sort(getYourPlayer(turn).currHand);
            update1 = getYourPlayer(turn).name + " got caught cheating by " + getYourPlayer(whoCalled).name + ". The pile was added to their cards.";
            if(getYourPlayer(turn) instanceof Computer){
                ((Computer) getYourPlayer(turn)).cheatChance -=1;
                if(((Computer) getYourPlayer(turn)).cheatChance <=0){
                    ((Computer) getYourPlayer(turn)).cheatChance = .5;
                }
                //System.out.println("Caught!");
            }
            if(getYourPlayer(whoCalled) instanceof Computer){
                ((Computer) getYourPlayer(whoCalled)).callChance +=.2;
            }
        }else{
            //System.out.println(getYourPlayer(whoCalled).name);
            getYourPlayer(whoCalled).currHand.addAll(pile);
            Collections.sort(getYourPlayer(whoCalled).currHand);
            update1 = getYourPlayer(whoCalled).name + " called cheat on " + getYourPlayer(turn).name + ". They were not. The pile was added to their cards.";
            if(getYourPlayer(whoCalled) instanceof Computer){
                ((Computer) getYourPlayer(whoCalled)).callChance -=1;
                if(((Computer) getYourPlayer(whoCalled)).cheatChance <=0){
                    ((Computer) getYourPlayer(turn)).cheatChance = .5;
                }
            }
        }
        for(int i = 0;i<currPlayers.size();i++){
            if(currPlayers.get(i) instanceof Computer)
                ((Computer)currPlayers.get(i)).past.removeAll(((Computer)currPlayers.get(i)).past);
        }
        cheat = false;
        turn = (turn+1)%numOfPlayers;
        for(int i = 0; i<whoAnswered.length;i++){
            whoAnswered[i] = false;
        }
        pile.removeAll(pile);
        cardsInQuestion = null;
        lastChoice = null;
    }


    private void isComputerTurn() {
        if(turn != -1 && getYourPlayer(turn) instanceof Computer && checkingPlayer != true){
            Computer comp = ((Computer)getYourPlayer(turn));
            chooseWhatToSelect(comp);
        }

    }

    int chooseWhatToCall(Computer comp){

        if(cardsInQuestion.size() >4){
            return MyGameInput.CALL_CHEAT;
        }
        int countOfCards= cardsInQuestion.size();
        for(int currentCheck=0; currentCheck<comp.currHand.size();currentCheck++){
            if(playerChoice==comp.currHand.get(currentCheck).type){
                countOfCards+=1;
            }
        }
        if(countOfCards>4){
            return MyGameInput.CALL_CHEAT;
        }
        for(int currentCheck=0; currentCheck<comp.past.size();currentCheck++){
            if(playerChoice== comp.past.get(currentCheck).type){
                countOfCards+=comp.past.get(currentCheck).num;
            }
        }
        if(countOfCards>4){
            return MyGameInput.CALL_CHEAT;
        }

        int rand = (int)(20 * Math.random());
        if(rand < comp.callChance){
            return MyGameInput.CALL_CHEAT;
        }

        comp.past.add(new Track(cardsInQuestion.size(),playerChoice));
        return MyGameInput.LET_GO;
    }

    void chooseWhatToSelect(Computer comp){

        CardType chosen = null;

        if(playerChoice==null){
            CardType biggest = comp.currHand.get(0).type;
            CardType biggestFinal = comp.currHand.get(0).type;
            int count = 0;
            int biggestCount=0;
            for(int i = 0; i < comp.currHand.size();i++){
                if(biggest == comp.currHand.get(i).type){
                    count +=1;
                }else{
                    if(count>biggestCount){
                        biggestCount = count;
                        biggestFinal = biggest;
                    }
                    count =1;
                    biggest = comp.currHand.get(i).type;
                }
            }

            chosen = biggestFinal;

            int rand = (int)(Math.random()*10);
            if(rand < comp.cheatChance){
                getFinalCheatCards(comp, biggestCount, getWhich(chosen.ordinal()+6));
            }else{

                for(int i = 0; i < comp.currHand.size();i++){
                    if(chosen == comp.currHand.get(i).type){
                        comp.choices.add(comp.currHand.get(i));
                    }
                }
            }
        }else{
            chosen = playerChoice;
            do{
                int rand = (int)(Math.random()*10);
                if(rand < comp.cheatChance){
                    chosen = cheat(comp,playerChoice);
                    break;
                }else{
                    goThrough(comp, chosen);
                    if(comp.choices.size()>0){
                        break;
                    }
                    chosen = getWhich(playerChoice.ordinal()-1);
                    goThrough(comp, chosen);
                    if(comp.choices.size()>0){
                        break;
                    }
                    chosen = getWhich(playerChoice.ordinal()+1);
                    for(int i = 0; i<comp.currHand.size();i++){
                        if(comp.currHand.get(i).type == chosen){
                            comp.choices.add(comp.currHand.get(i));
                        }
                    }
                    if(comp.choices.size()>0){
                        break;
                    } else{
                        comp.cheatChance += 2;
                        rand = (int)(Math.random()*10);
                        if(rand < comp.cheatChance){
                            comp.cheatChance -= 2;
                            chosen = cheat(comp,playerChoice);
                            break;
                        }else{
                            comp.cheatChance -= 2;
                            update2 = getYourPlayer(turn).name + " Passed their turn";
                            turn = (turn+1)%numOfPlayers;
                            //System.out.println("Passed");
                            isComputerTurn();
                            return;
                        }
                    }
                }
            }while(true);

        }

        if(comp.choices.size()==0||chosen==null){
            update2 = getYourPlayer(turn).name + " Passed their turn";
            turn = (turn+1)%numOfPlayers;
            //System.out.println("Passed");
            isComputerTurn();
            return;
        }

        ArrayList<Card> temp = new ArrayList<Card>();
        for(int i = 0; i<comp.choices.size();i++){
            temp.add(comp.choices.get(i));
        }
        makeSelection(temp, chosen);
        comp.currHand.removeAll(comp.choices);
        comp.choices.removeAll(comp.choices);
        whoAnswered[turn] = true;
    }

    private CardType cheat(Computer comp, CardType original) {
        CardType opposite = getWhich(original.ordinal()+6);
        CardType before =  getWhich(original.ordinal()-1);
        CardType after =  getWhich(original.ordinal()+1);
        CardType finalT = original;
        int count = goThroughOwn(comp, original);
        int temp = goThroughOwn(comp,before);//finding out how many he has of each and will keep the one he has the most of. Will choose that one.
        if(count < temp){
            count = temp;
            finalT = before;
        }
        temp = goThroughOwn(comp,after);
        if(count < temp){
            count = temp;
            finalT = after;
        }
        int cantGoOver = count;
        if(cantGoOver ==0){// if have none, goes through past and finds out which is least from there.

            int lowest = findAllPastNum(comp, original);
            int test = findAllPastNum(comp, before);
            if(test < lowest){
                lowest = test;
                finalT = before;
            }
            test = findAllPastNum(comp, after);
            if(test < lowest){
                lowest = test;
                finalT = after;
            }
            if(lowest >= 4)
                return null;
            cantGoOver = 4 - test;
            if(cantGoOver>=3){// dont want to go too high since someone else will have some of those cards. They will notice
                cantGoOver = 2;
            }
        }
        getFinalCheatCards(comp, cantGoOver, opposite); //gets all cards using cards farthest away from the one last called

        return finalT;
    }

    private void getFinalCheatCards(Computer comp, int cantGoOver, CardType opposite){
        CardType newC;
        goThrough(comp, opposite);
        if(comp.choices.size()>0){

            while(comp.choices.size()>cantGoOver){//removes extra cards to try to prevent from getting caught
                comp.choices.remove(0);
            }
            if(comp.choices.size()==cantGoOver){//if can still add more keeps going
                return;
            }
        }
        int count =1;
        //next goes above and below current CardType to find more cards to add
        do{
            newC = getWhich(opposite.ordinal()+count);
            goThrough(comp, newC);
            if(comp.choices.size()>0){
                while(comp.choices.size()>cantGoOver){ 
                    comp.choices.remove(0);
                }
                if(comp.choices.size()==cantGoOver){
                    return;
                }
            }
            newC = getWhich(opposite.ordinal()-count);
            goThrough(comp, newC);
            if(comp.choices.size()>0){
                while(comp.choices.size()>cantGoOver){
                    comp.choices.remove(0);
                }
                if(comp.choices.size()==cantGoOver){
                    return;
                }
            }
            count+=1;
        }while(count<10);
    }

    private int findAllPastNum(Computer comp, CardType opposite) {
        int count =0;
        for(int i = 0; i<comp.past.size();i++){
            if(opposite == comp.past.get(i).type){
                count +=1;
            }
        } 
        return count;
    }

    private int goThroughOwn(Computer comp, CardType chosen){
        int count =0;
        for(int i = 0; i<comp.currHand.size();i++){
            if(comp.currHand.get(i).type == chosen){
                count+=1;
            }
        }
        return count;
    }

    private void goThrough(Computer comp, CardType chosen){
        for(int i = 0; i<comp.currHand.size();i++){
            if(comp.currHand.get(i).type == chosen){
                comp.choices.add(comp.currHand.get(i));
            }
        }
    }

    private CardType getWhich(int num) {//used to get the opposite or the cards before or after it
        switch(num){
        case -1: return CardType.King;
        case 0: return CardType.Ace;
        case 1: return CardType.Two;
        case 2: return CardType.Three;
        case 3: return CardType.Four;
        case 4: return CardType.Five;
        case 5: return CardType.Six;
        case 6: return CardType.Seven;
        case 7: return CardType.Eight;
        case 8: return CardType.Nine;
        case 9: return CardType.Ten;
        case 10: return CardType.Jack;
        case 11: return CardType.Queen;
        case 12: return CardType.King;
        case 13: return CardType.Ace;
        case 14: return CardType.Two;
        case 15: return CardType.Three;
        case 16: return CardType.Four;
        case 17: return CardType.Five;
        case 18: return CardType.Six;
        case 19: return CardType.Seven;
        case 20: return CardType.Eight;
        case 21: return CardType.Nine;
        case 22: return CardType.Ten;
        case 23: return CardType.Jack;
        case 24: return CardType.Queen;
        case 25: return CardType.King;
        }
        return null;
    }
}


