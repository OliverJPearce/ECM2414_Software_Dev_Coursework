
class Card {
    private int cardValue;

    public Card(int cardValue) {
        this.cardValue = cardValue;
    }

    public int getValue() {
        return this.cardValue;
    }
}

class CardDeck {
    private Card[] deckCards;
    private int deckFrontPointer = 0;   // Points at the first card in deckCards
    private int deckBackPointer = -1;   // Points at the last card in deckCards

    public CardDeck(int totalCardCount, int[] cardValues) {
        // Make the deck able to fit all cards in at once (to prevent overflow)
        deckCards = new Card[totalCardCount];
        
        for (int i = 0; i < cardValues.length; i++) {
            deckCards[i] = new Card(cardValues[i]);
            deckBackPointer++;
        }
    }

    public CardDeck(int totalCardCount) {
        // Make the deck able to fit all cards in at once (to prevent overflow)
        deckCards = new Card[totalCardCount];
    }

    public synchronized Card drawCard() {
        // Get the current front card
        Card frontCard = deckCards[deckFrontPointer];

        deckFrontPointer++;
        if (deckFrontPointer >= deckCards.length) deckFrontPointer = 0;

        // Return the front card
        return frontCard;
    }

    public synchronized void addCard(Card card) {
        deckBackPointer++;
        if (deckBackPointer >= deckCards.length) deckBackPointer = 0;

        // Add the card to the back of the deck
        deckCards[deckBackPointer] = card;
    }
}

class Player extends Thread {
    private Card[] heldCards = new Card[4];

    private CardDeck drawDeck;
    private CardDeck discardDeck;
    
    private boolean hasWon = false;
    private int playerId = 0;
    private static int TOTAL_PLAYERS = 0;

    public Player(CardDeck drawDeck, CardDeck discardDeck) {
        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;

        this.playerId = ++TOTAL_PLAYERS;
    }
    
    public Player(int[] heldCardValues, CardDeck drawDeck, CardDeck discardDeck) {
        if (heldCardValues.length != 4) return;

        for (int i = 0; i < 4; i++) {
            this.heldCards[i] = new Card(heldCardValues[i]);
        }

        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;

        this.playerId = ++TOTAL_PLAYERS;
        this.checkIfWon();
    }

    public void run() {
        System.out.println("Player thread running");

        takeTurn();
    }

    public void setDrawDeck(CardDeck deck) {
        this.drawDeck = deck;
    }

    public void setDiscardDeck(CardDeck deck) {
        this.discardDeck = deck;
    }

    public void setCard(Card newCard, int handIndex) {
        this.heldCards[handIndex] = newCard;
    }

    public void setCard(int cardValue, int handIndex) {
        this.heldCards[handIndex] = new Card(cardValue);
    }

    private void replaceHeldCard(Card newCard, int replaceIndex) {
        // Add the card to the discard deck
        this.discardDeck.addCard(heldCards[replaceIndex]);

        // Replace the card in the player's hand
        this.heldCards[replaceIndex] = newCard;
    }

    private void takeTurn() {
        // Draw the next card
        Card drawCard = this.drawDeck.drawCard();

        // Pick random card to discard
        int randomCard = 0;

        // Prevent player from discarding preffered cards
        if (this.heldCards[randomCard].getValue() != this.playerId) {
            replaceHeldCard(drawCard, randomCard);
        }

        // Check if player has won
        this.checkIfWon();
    }
    
    private void checkIfWon(){
        //Scan Hand if all cards are the same value, starting from the card in index 0
        int checkValue = this.heldCards[0].getValue();
        
        for (int i = 0; i < 4; i++) {
            if (checkValue != this.heldCards[i].getValue()) {
                hasWon = false;
                return;
            }
        };
        
        // Should go here if all cards are the same.
        hasWon = true;
        return;
    }
    
}

public class CardGame {

    public static void main(String[] args) {
        System.out.println("Hello!");

        final int TOTAL_CARDS = 20;

        CardDeck deck1 = new CardDeck(TOTAL_CARDS);
        CardDeck deck2 = new CardDeck(TOTAL_CARDS);

        Player player = new Player(deck1, deck2);
        player.setCard(2, 0);
        player.setCard(2, 1);
        player.setCard(2, 2);
        player.setCard(2, 3);

        player.start();
    }
}