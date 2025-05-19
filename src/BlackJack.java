import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class BlackJack{

    
    
    
    // ---------------card eliment---------------
    private class Card{
        String value;
        String type;


        Card(String value,String type){
            this.value = value;
            this.type = type;

        }

       

        
        public String toString(){
            return value+"-"+type;
        }


       public int getValue() {
           if ("AJQK".contains(value)) {
                if (value.equals("A")) {
                    return 11;
                }
            return 10;
           }
        return Integer.parseInt(value);
       }


       public boolean isAce(){
        return value == "A";
       }

       public String getImagePath(){
         return "./cards/"+toString()+".png";
       }

    }


    ArrayList<Card>deck;
    Random random = new Random();


    //dealler
    Card hiddenCard;
    ArrayList<Card>dealerHand;
    int dealerSum;
    int dealerAceCount;


    //player
    ArrayList<Card>playerHand;
    int playerSum;
    int playerAceCount;

    //Window
    int boardWidth = 600;
    int boardHeight = boardWidth;

    int cardWidth = 110;
    int cardHeight = 154;

    JFrame frame =new JFrame("Black Jack");


    //--------------additional feacher---------
    int playerWins = 0;            
    int dealerWins = 0;            
    int playerBalance = 1000;      
    int currentBet = 0;            

    JLabel statusLabel = new JLabel();             
    JLabel scoreLabel = new JLabel();              
    JLabel balanceLabel = new JLabel();
    
    JPanel gamePanel = new JPanel(){
       
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            
           try{

            //--------------draw hidden card---
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if(!stayButton.isEnabled()){
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg,20,20,cardWidth,cardHeight,null);


            //----------------dealerhand card image-----

             for(int i=0 ;i<dealerHand.size(); i++){
                  Card card = dealerHand.get(i);
                  Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                 g.drawImage(cardImage, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                    
             }


            //-------------- player hand image----------
             
            for(int i=0 ;i<playerHand.size(); i++){
                Card card = playerHand.get(i);
                 Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                 g.drawImage(cardImage, 20 + (cardWidth + 5) * i, 320, cardWidth, cardHeight, null);

            }

            if(!stayButton.isEnabled()){
                 dealerSum = reduceDealerAce();
                 playerSum = reducePlayerAce();
                 System.out.println("STAY : ");
                 System.out.println(dealerSum);
                 System.out.println(playerSum);

                 String message = "";
                 if(playerSum > 21){
                   message = "You Lose!";
                    dealerWins++;
                    playerBalance -= currentBet;  

                 }
                 else if(dealerSum >21){
                    message = "You Win!";
                    playerWins++;                            
                    playerBalance += currentBet;
                 }

                 else if(playerSum == dealerSum){
                    message = "Tie!";
                 }

                 else if(playerSum > dealerSum){
                    message = "You win!";
                    playerWins++;
                    playerBalance += currentBet;
                 }
                 else if(playerSum <  dealerSum){
                    message = "You Lose!";
                    dealerWins++; 
                    playerBalance -= currentBet; 
                 }

                 g.setFont(new Font("Arial",Font.PLAIN,30));
                 g.setColor(Color.white);
                 g.drawString(message,220,250);

                 updateStatus();   
            }




            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            
            
            

        }
    };


    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");
    JButton restartButton = new JButton("Restart");



    BlackJack(){
        startGame();
        

        //-----------create fame---------
        frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //---------------set colour-------
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53,101,77));
        frame.add(gamePanel);


        //----------------button-----------
        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        frame.add(buttonPanel,BorderLayout.SOUTH);
        restartButton.setFocusable(false);
        buttonPanel.add(restartButton);


        JPanel infoPanel = new JPanel();                           
        infoPanel.setLayout(new GridLayout(3, 1));                 
        infoPanel.add(statusLabel);                                
        infoPanel.add(scoreLabel);                                 
        infoPanel.add(balanceLabel);                               
        frame.add(infoPanel, BorderLayout.NORTH); 
        

        //-----------------button action--------
        hitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
               Card card = deck.remove(deck.size()-1);
               playerSum += card.getValue();
               playerAceCount += card.isAce()? 1:0;
               playerHand.add(card);
               if(reducePlayerAce()>21){
                hitButton.setEnabled(false);
                 stayButton.setEnabled(false);
               }

               gamePanel.repaint();


            }
            
        });

        stayButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while(dealerSum <17){
                    Card card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce()? 1:0;
                    dealerHand.add(card);
                }
                  gamePanel.repaint();

            }
        });


          restartButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                  if (playerBalance <= 0) {
                    JOptionPane.showMessageDialog(frame, "Game Over! You're out of money.");
                    System.exit(0);                               
                }
                promptBet();   
                restartGame(); 
            }
        });

        
        promptBet();                                      
        startGame();
        updateStatus(); 
        gamePanel.repaint(); 

   }

   public void promptBet() {                                      
        while (true) {
            String input = JOptionPane.showInputDialog(frame, "Enter your bet amount: ");
            try {
                int bet = Integer.parseInt(input);
                if (bet > 0 && bet <= playerBalance) {
                    currentBet = bet;
                    break;
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid bet. Try again.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Please enter a number.");
            }
        }
    }

    public void updateStatus() {                                    
        scoreLabel.setText("Player Wins: " + playerWins + " | Dealer Wins: " + dealerWins);
        balanceLabel.setText("Balance: $" + playerBalance + " | Bet: $" + currentBet);
    }



    public void restartGame() {
        hitButton.setEnabled(true);
        stayButton.setEnabled(true);
        startGame();
        updateStatus();                                            
        gamePanel.repaint();
    }


   public void startGame(){
       buildDeck(); 
       suffleDeck();

       
       
       //dealer
       dealerHand =new ArrayList<Card>();
       dealerSum=0;
       dealerAceCount=0;
       hiddenCard = deck.remove(deck.size()-1); //remove card last index
       
      dealerSum += hiddenCard.getValue();
      dealerAceCount +=  hiddenCard.isAce() ? 1:0;

      Card card = deck.remove(deck.size()-1);
      dealerSum += card.getValue();
      dealerAceCount += card.isAce() ? 1:0;
      dealerHand.add(card);

      System.out.println("DEALER : ");
      System.out.println(hiddenCard);
      System.out.println( dealerHand);
      System.out.println(dealerSum);
      System.out.println(dealerAceCount);


      //player
      playerHand =new ArrayList<Card>();
      playerSum=0;
      playerAceCount=0;

      for(int i =0;i<2;i++){
        card = deck.remove(deck.size()-1);
        playerSum += card.getValue();
        playerAceCount +=  card.isAce() ? 1:0;
        playerHand.add(card);

      }


      System.out.println("PLAYER : ");
      System.out.println( playerHand);
      System.out.println(playerSum);
      System.out.println(playerAceCount);


   }



   //------------build deck--------------

   public void buildDeck(){
       deck = new ArrayList<Card>();
       String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
       
       String[] types = {"C", "D", "H", "S"};

       for(int i =0;i<types.length;i++){
         for(int j=0;j<values.length;j++){
              Card card = new Card(values[j],types[i]);
              deck.add(card);
         }

       }


       System.out.println("Bulid Deck : ");
       System.out.println(deck);
   }

  



   //-------------suffleDeck--------------
   public void suffleDeck(){
    for(int i =0;i< deck.size();i++){
    
       int j = random.nextInt(deck.size());
       Card currCard  = deck.get(i) ;
       Card randomCard = deck.get(j);

       deck.set(i,randomCard);
       deck.set(j,currCard);
    }

    System.out.println("After Shuffle : ");
    System.out.println(deck);

   }


public int reducePlayerAce(){
    while(playerSum > 21 && playerAceCount > 0){
        playerSum -= 10;
        playerAceCount -= 1;
    }

    return playerSum; 
}


public int reduceDealerAce(){
    while(dealerSum > 21 && dealerAceCount > 0){
        dealerSum -= 10;
        dealerAceCount -= 1;
    }

    return dealerSum; 
}





       
}

