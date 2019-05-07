package cheat;

import gameNet.GameNet_UserInterface;
import gameNet.GamePlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MyUserInterface extends JFrame 
implements   ActionListener, GameNet_UserInterface{
    private MyGame myGame=null;
    private GamePlayer myGamePlayer = null;
    private String myName = " ";
    private MyGameInput myGameInput = new MyGameInput();

    CardType current = null;

    JPanel topPanel = new JPanel();
    JPanel rightPanel = new JPanel();
    JPanel leftPanel = new JPanel();
    JPanel bottomPanel = new JPanel();

    JLabel bottomPlayerName = new JLabel("Empty");
    JLabel leftPlayerName = new JLabel("Empty");
    JLabel topPlayerName = new JLabel("Empty");
    JLabel rightPlayerName = new JLabel("Empty");

    JPanel centerBottomPanel = new JPanel();

    JPanel cardZone = new JPanel();

    private Termination closeMonitor = new Termination();
    private JButton PassOutCards = new JButton("Pass out Cards");
    private JButton cheatYes = new JButton("Cheat!");
    private JButton cheatNo = new JButton("Let off Hook");
    private JButton pass = new JButton("Pass");
    private JLabel cheater = new JLabel(" ");
    private JButton reset = new JButton("Reset");
    private JButton done = new JButton("Done");
    private JLabel turn = new JLabel(" ");
    private JLabel updates = new JLabel("");
    private JLabel pileCount = new JLabel("");

    private CardType[] possibleCardChoices = {CardType.Ace, CardType.Two, CardType.Three, CardType.Four, CardType.Five, CardType.Six, CardType.Seven, CardType.Eight, CardType.Nine, CardType.Ten, CardType.Jack, CardType.Queen, CardType.King};
    private JComboBox<CardType> numList = new JComboBox<CardType>(possibleCardChoices);
    private ArrayList<JButton> whichToShow = new ArrayList<JButton>();
    private ArrayList<JButton> whichChoosing = new ArrayList<JButton>();

    private ArrayList<JLabel> topCards = new ArrayList<JLabel>();
    private ArrayList<JLabel> rightCards = new ArrayList<JLabel>();
    private ArrayList<JLabel> leftCards = new ArrayList<JLabel>();

    ItemListener item;

    private static final long serialVersionUID = 1L;

    MyUserInterface(){
        super("Cheat");
        item = new ItemListener(){ 

            @Override
            public void itemStateChanged(ItemEvent e){
                current = (CardType) numList.getSelectedItem();
            } 
        };
    }

    @Override
    public void receivedMessage(Object ob) {

        MyGameOutput myGameOutput = (MyGameOutput)ob;
        myGame = myGameOutput.myGame;
        updates.setText(myGame.update);

        if(myGame.won==null){

            if(myGame.reset){
                PassOutCards.setVisible(true);
                reset.setVisible(false);
            }
            pileCount.setText("Pile: " + myGame.pile.size());

            bottomPlayerName.setText(myName);
            leftPlayerName.setText(myGame.getPlayer(myGame.getYourIndex(myName),MyGame.direction.Left).name);
            topPlayerName.setText(myGame.getPlayer(myGame.getYourIndex(myName),MyGame.direction.Top).name);
            rightPlayerName.setText(myGame.getPlayer(myGame.getYourIndex(myName),MyGame.direction.Right).name);

            if(myGame.turn == myGame.getYourIndex(myName)&& myGame.checkingPlayer == false){
                numList.setVisible(true);
                turn.setVisible(false);
                centerBottomPanel.remove(numList);
                numList.removeItemListener(item);
                cheatYes.setVisible(false);
                cheatNo.setVisible(false);
                pass.setVisible(false);
                if(myGame.lastChoice == null){
                    numList = new JComboBox<CardType>(possibleCardChoices);
                    current = CardType.Ace;
                } else{
                    CardType[] card = new CardType[3];
                    int getIndex = getNumListIndex();
                    int previous = getIndex-1;
                    int next = getIndex+1;
                    if(previous <= -1) previous = 12;
                    if(next >= 13) next = 0;
                    card[0] = possibleCardChoices[previous];
                    card[1] = possibleCardChoices[getIndex];
                    card[2] = possibleCardChoices[next];
                    numList = new JComboBox<CardType>(card);
                    current = numList.getItemAt(0);
                }
                numList.addItemListener(item);
                centerBottomPanel.add(numList);
                done.setVisible(true);
                pass.setVisible(true);
            } else{
                if(myGame.turn >=0){
                    if(myGame.checkingPlayer==false){
                        numList.setVisible(false);
                        done.setVisible(false);
                        pass.setVisible(false);
                        turn.setText(myGame.getYourPlayer(myGame.turn).name + "'s is taking their turn.");
                        turn.setVisible(true);
                        cheater.setVisible(false);
                        cheatYes.setVisible(false);
                        cheatNo.setVisible(false);
                    } else{
                        if(myGame.turn != myGame.getYourIndex(myName)){
                            numList.setVisible(false);
                            done.setVisible(false);
                            pass.setVisible(false);
                            turn.setVisible(false);
                            if(myGame.cardsInQuestion !=null)
                                cheater.setText(myGame.getYourPlayer(myGame.turn).name + " placed down " + myGame.cardsInQuestion.size() + " " + myGame.playerChoice + "('s). " );
                            if(!myGame.whoAnswered[myGame.getYourIndex(myName)]){
                                cheater.setVisible(true);
                                cheatYes.setVisible(true);
                                cheatNo.setVisible(true);
                            } else{
                                cheatYes.setVisible(false);
                                cheatNo.setVisible(false);
                            }
                        }else{
                            numList.setVisible(false);
                            numList.setVisible(false);
                            done.setVisible(false);
                            pass.setVisible(false);
                            turn.setText("Waiting for players Choices...");
                            turn.setVisible(true);
                            for(int i = whichChoosing.size()-1; i>=0;i--){
                                cardZone.remove(whichChoosing.get(i));
                                whichChoosing.remove(i);
                            }

                        }
                    }

                }
            }
        }else{
            reset.setVisible(true);
            cheater.setVisible(false);
            cheatYes.setVisible(false);
            cheatNo.setVisible(false);
            turn.setVisible(false);
            pass.setVisible(false);
            done.setVisible(false);
            numList.setVisible(false);
        }
        if(myGame.turn != -1)
            PassOutCards.setVisible(false);
        Player temp = myGame.getPlayer(myGame.getYourIndex(myName), MyGame.direction.Top);
        if(temp !=null && temp.currHand.size() >=0){
            while(topCards.size() != temp.currHand.size()){
                if(topCards.size() < temp.currHand.size()){
                    addLabel(topCards, topPanel);
                } else{
                    if(topCards.size() > temp.currHand.size()){
                        removeLabel(topCards, topPanel);
                    }
                }
            }
        }

        temp = myGame.getPlayer(myGame.getYourIndex(myName), MyGame.direction.Left);
        if(temp !=null && temp.currHand.size()>=0){
            while(leftCards.size() != temp.currHand.size()){
                if(leftCards.size() < temp.currHand.size()){
                    addLabel(leftCards, leftPanel);
                } else{
                    if(leftCards.size() > temp.currHand.size()){
                        removeLabel(leftCards, leftPanel);
                    }
                }
            }
        }

        temp = myGame.getPlayer(myGame.getYourIndex(myName), MyGame.direction.Right);
        if(temp !=null && temp.currHand.size() >=0){
            while(rightCards.size() != temp.currHand.size()){
                if(rightCards.size() < temp.currHand.size()){
                    addLabel(rightCards, rightPanel);
                } else{
                    if(rightCards.size() > temp.currHand.size()){
                        removeLabel(rightCards, rightPanel);
                    }
                }
            }
        }
        temp = myGame.getYourPlayer(myGame.getYourIndex(myName));
        if(temp !=null && temp.currHand !=null){
            if(whichToShow.size() > 0 ){
                removeAllPanel(whichToShow, bottomPanel);
            }
            getCardsAndMakeArray(temp);
        }

        revalidate();
    }

    private int getNumListIndex() {
        for(int i = 0; i < possibleCardChoices.length;i++){
            if(myGame.lastChoice == possibleCardChoices[i]){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void startUserInterface(GamePlayer gamePlayer) {
        myGamePlayer = gamePlayer;
        myName = gamePlayer.getPlayerName();
        myGameInput.setName(myName);

        sendMessage(MyGameInput.JOIN_GAME);

        addWindowListener(this.closeMonitor);
        screenLayout();
        setVisible(true);
    }

    private void screenLayout() {
        setLayout(new BorderLayout());

        setSize(900,550);
        JPanel centerPanel = new JPanel();

        centerPanel.setLayout(new BorderLayout());

        bottomPanel.setLayout(new GridLayout(0,10,4,4));
        topPanel.setLayout(new GridLayout(0,10,4,4));
        leftPanel.setLayout(new GridLayout(0,3,4,4));
        rightPanel.setLayout(new GridLayout(0,3,4,4));

        JPanel centerCenterPanel = new JPanel();
        JPanel centerTopPanel = new JPanel();
        JLabel cheatLabel = new JLabel("Cheat");
        centerTopPanel.add(cheatLabel);

        centerBottomPanel.add(numList, BorderLayout.SOUTH);
        centerBottomPanel.add(done, BorderLayout.SOUTH);
        centerBottomPanel.add(turn, BorderLayout.SOUTH);
        centerTopPanel.add(reset);
        centerTopPanel.add(pileCount);

        numList.setVisible(false);
        done.setVisible(false);
        reset.setVisible(false);
        reset.addActionListener(this);

        centerBottomPanel.add(PassOutCards);
        centerBottomPanel.add(pass);
        PassOutCards.addActionListener(this);
        centerBottomPanel.add(cheatYes);
        centerBottomPanel.add(cheatNo);
        cheatYes.setVisible(false);
        cheatNo.setVisible(false);
        pass.setVisible(false);
        cheatYes.addActionListener(this);
        cheatNo.addActionListener(this);
        done.addActionListener(this);
        pass.addActionListener(this);

        bottomPanel.setBackground(Color.white);
        centerPanel.add(centerTopPanel, BorderLayout.NORTH);
        centerPanel.add(centerBottomPanel, BorderLayout.SOUTH);
        centerPanel.add(centerCenterPanel, BorderLayout.CENTER);
        centerCenterPanel.setLayout(new BorderLayout());
        centerCenterPanel.add(cardZone, BorderLayout.CENTER);

        JPanel centercenterbottom = new JPanel();
        centercenterbottom.setLayout(new GridLayout(0,1));
        centerCenterPanel.add(centercenterbottom, BorderLayout.SOUTH);
        centercenterbottom.add(cheater, BorderLayout.SOUTH);
        centercenterbottom.add(updates, BorderLayout.SOUTH);

        bottomPanel.add(bottomPlayerName);

        leftPanel.setBackground(Color.white);
        leftPanel.add(leftPlayerName);

        topPanel.setBackground(Color.white);
        topPanel.add(topPlayerName);

        rightPanel.setBackground(Color.white);
        rightPanel.add(rightPlayerName);

        add(topPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void sendMessage(int command){
        myGameInput.command = command;
        myGamePlayer.sendMessage(myGameInput);
    }

    private void sendMessageSelection(){
        ArrayList<Card> selection = getCards();
        myGameInput.setCards(selection);
        myGameInput.setChoice(current);
        for(int i = 0; i < selection.size();i++){
            for(int f = (whichToShow.size()-1); f >= 0; f--){
                if(selection.get(i).image==(ImageIcon) whichToShow.get(f).getIcon()){
                    whichToShow.get(f).removeActionListener(this);
                    whichToShow.remove(f);
                }
            }
        }
        removeAllPanel(whichChoosing, cardZone);
        whichChoosing.removeAll(whichChoosing);
        sendMessage(MyGameInput.SELECT_CARDS);
    }

    //Goes through CurrHand and if image matches JButton whichChoosing it adds to temp which will later be sent as their selection.
    private ArrayList<Card> getCards() {
        ArrayList<Card> temp = new ArrayList<Card>();
        Player you = myGame.getYourPlayer(myGame.getYourIndex(myName));
        for(int i = 0; i < whichChoosing.size();i++){
            for(int c = 0; c< you.currHand.size();c++){
                if(you.currHand.get(c).image == whichChoosing.get(i).getIcon()){
                    temp.add(you.currHand.get(c));
                    break;
                }
            }
        }
        return temp;
    }

    public void actionPerformed(ActionEvent e) {

        if(e.getSource()== PassOutCards){
            sendMessage(MyGameInput.PASS_OUT_CARDS);
            return;
        }

        if(e.getSource()==pass){
            sendMessage(MyGameInput.PASS_TURN);
            removeAllPanel(whichChoosing, cardZone);
            whichChoosing.removeAll(whichChoosing);
        }

        if(e.getSource()==reset){
            sendMessage(MyGameInput.RESETTING);
        }

        if(e.getSource()== done && myGame.checkingPlayer == false){
            if(whichChoosing.size() > 0)
                sendMessageSelection();
            else
                sendMessage(MyGameInput.PASS_TURN);
            return;
        }
        if(myGame.checkingPlayer){
            if(e.getSource() == cheatYes){
                sendMessage(MyGameInput.CALL_CHEAT);
                return;
            }

            if(e.getSource() == cheatNo){
                sendMessage(MyGameInput.LET_GO);
            }
        }

        if(myGame.checkingPlayer == false && myGame.turn == myGame.getYourIndex(myName)){
            for(int i = 0; i < whichToShow.size();i++){
                if(e.getSource() == whichToShow.get(i)){
                    JButton temp = whichToShow.get(i);
                    bottomPanel.remove(temp);
                    whichToShow.remove(i);
                    whichChoosing.add(temp);
                    cardZone.add(temp);
                    revalidate();
                    return;
                }
            }

            for(int i = 0; i < whichChoosing.size();i++){
                if(e.getSource() == whichChoosing.get(i)){
                    JButton temp = whichChoosing.get(i);
                    cardZone.remove(temp);
                    whichChoosing.remove(i);
                    whichToShow.add(temp);
                    bottomPanel.add(temp);
                    revalidate();
                    return;
                }
            }

            return;
        }
    }

    public void addLabel(ArrayList<JLabel> array, JPanel panel){
        array.add(new JLabel(new ImageIcon("blank2.gif")));
        panel.add(array.get(array.size()-1));
    }

    public void removeLabel(ArrayList<JLabel> array, JPanel panel){
        panel.remove(array.get(array.size()-1));
        array.remove(array.get(array.size()-1));
    }

    public void removeAllPanel(ArrayList<JButton> array, JPanel panel){
        for(int i = array.size()-1; i >= 0;i--){
            panel.remove(array.get(i));
            array.get(i).removeActionListener(this);
            array.remove(i);
            revalidate();
        }
    }

    public void getCardsAndMakeArray(Player player){
        for(int i = 0; i<player.currHand.size();i++){
            whichToShow.add(new JButton(player.currHand.get(i).image));
            bottomPanel.add(whichToShow.get(i));
            whichToShow.get(i).addActionListener(this);
        }
    }

    class Termination extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            sendMessage(MyGameInput.DISCONNECTING);
            myGamePlayer.doneWithGame();
            System.exit(0);
        }
    }

}
