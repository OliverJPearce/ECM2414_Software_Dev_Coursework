import java.io.*;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

class Card {
    private final int cardValue;

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
    private int deckCardsLength = 0;

    private int deckId = 0;
    private static int TOTAL_DECKS = 0;

    public CardDeck(int totalCardCount, int[] cardValues) {
        // Make the deck able to fit all cards in at once (to prevent overflow)
        deckCards = new Card[totalCardCount];
        
        for (int i = 0; i < cardValues.length; i++) {
            deckCards[i] = new Card(cardValues[i]);
            deckBackPointer++;
            deckCardsLength++;
        }

        this.deckId = ++TOTAL_DECKS;
    }

    public synchronized Card drawCard() throws InterruptedException {
        // Wait until the deck has cards to draw
        while (deckCardsLength <= 0) wait();
        
        // Get the current front card
        Card frontCard = deckCards[deckFrontPointer];
        deckCards[deckFrontPointer] = null;

        deckFrontPointer = (deckFrontPointer + 1) % deckCards.length;
        deckCardsLength--;

        // Return the front card
        return frontCard;
    }

    public synchronized void addCard(Card card) {
        // Add the card to the back of the deck
        deckBackPointer = (deckBackPointer + 1) % deckCards.length;
        deckCards[deckBackPointer] = card;
        deckCardsLength++;

        // Notify any waiting players that the deck has a new card
        notifyAll();
    }

    private void addLog(String message) {

        String logFileName = "deck" + deckId + "_output.txt";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName));
            writer.write(message);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}

class Player extends Thread {
    private Card[] heldCards = new Card[4];

    private CardDeck drawDeck;
    private CardDeck discardDeck;
    
    private boolean hasWon = false;

    private int playerId = 0;
    private static int TOTAL_PLAYERS = 0;
    
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
        drawDeck = deck;
    }

    public void setDiscardDeck(CardDeck deck) {
        discardDeck = deck;
    }

    public void setCard(Card newCard, int handIndex) {
        heldCards[handIndex] = newCard;
    }

    public void setCard(int cardValue, int handIndex) {
        heldCards[handIndex] = new Card(cardValue);
    }

    private void takeTurn() {
        // Draw the next card
        Card drawCard = null;
        
        while (drawCard == null) {
            try {
                drawCard = this.drawDeck.drawCard();
            } catch (InterruptedException e) {
                // Interrupt occured during wait so retry turn
                return;
            }
        }

        // Pick random card to discard
        Random rand = new Random();
        int randomCard = rand.nextInt(4);

        // Prevent player from discarding preffered cards
        while (heldCards[randomCard].getValue() == this.playerId) {
            randomCard = rand.nextInt(4);
        }

        // Add the card to the discard deck
        discardDeck.addCard(heldCards[randomCard]);

        // Replace the card in the player's hand
        heldCards[randomCard] = drawCard;

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

        CardGame.announceWinner(this.playerId);
    }

    private void addLog(String message) {

        String logFileName = "player" + playerId + "_output.txt";


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName));
            writer.write(message);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
    
}

public class CardGame {

    private static Player[] PLAYERS;
    private static CardDeck[] CARD_DECKS;

    private static boolean gameFinished = false;

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);

        boolean isValidInput = false;
        int playerCount = 0;

        while (!isValidInput) {
            isValidInput = true;

            try {
                System.out.println("Please enter the number of players:");
                playerCount = scanner.nextInt();

                if (playerCount <= 0) {
                    System.out.println("Number of players should be 1 or greater");
                    isValidInput = false;
                    continue;
                }

            } catch (InputMismatchException e) {
                System.out.println("ERROR: Number of players entered was not in the correct format, please try again");
                isValidInput = false;
            }
        }

        final int TOTAL_CARDS = 8 * playerCount;

        isValidInput = false;
        int[] cardValues = new int[0];

        while (!isValidInput) {
            isValidInput = true;

            try {
                System.out.println("Please enter the location of a pack to load:");
                String packLocation = scanner.next();

                // Check the pack is valid
                cardValues = readPack(packLocation);
                if (cardValues == null) {
                    isValidInput = false;
                    continue;
                }

                if (cardValues.length != TOTAL_CARDS) {
                    System.out.println("ERROR: Pack has incorrect number of cards, needs to have exactly " + TOTAL_CARDS);
                    isValidInput = false;
                    continue;
                }

            } catch (InputMismatchException e) {
                System.out.println("ERROR: Pack location entered was not in the correct format, please try again");
                isValidInput = false;
            }
        }

        scanner.close();

        // Create the decks
        CARD_DECKS = new CardDeck[playerCount];

        int deckCardsStart = cardValues.length / 2; // First half are player cards, second half is deck cards

        for (int i = 0; i < playerCount; i++) {
            int card1 = cardValues[deckCardsStart + (0*playerCount) + i];
            int card2 = cardValues[deckCardsStart + (1*playerCount) + i];
            int card3 = cardValues[deckCardsStart + (2*playerCount) + i];
            int card4 = cardValues[deckCardsStart + (3*playerCount) + i];

            int[] deckCards = new int[]{card1, card2, card3, card4};
            CARD_DECKS[i] = new CardDeck(TOTAL_CARDS, deckCards);
        }

        // Create the players
        PLAYERS = new Player[playerCount];

        for (int i = 0; i < playerCount; i++) {
            int card1 = cardValues[(0*playerCount) + i];
            int card2 = cardValues[(1*playerCount) + i];
            int card3 = cardValues[(2*playerCount) + i];
            int card4 = cardValues[(3*playerCount) + i];

            int[] playerHand = new int[]{card1, card2, card3, card4};
            // Player picks up from same deck number as their number and discards to the next deck
            PLAYERS[i] = new Player(playerHand, CARD_DECKS[i], CARD_DECKS[(i + 1) % playerCount]);
        }

        // Start the player threads
        for (Player p : PLAYERS) {
            p.start();
        }
    }

    public static synchronized void announceWinner(int playerId) {
        // Only allow 1 player to announce themselves as a winner
        if (gameFinished) return;
        gameFinished = true;

        System.out.println("Player " + playerId + " has won the game!");

        // Stop all other player threads
    }

    private static int[] readPack(String packLocation) {

        String packContents = "";

        // Read the given pack file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(packLocation));
            packContents = reader.readAllAsString();
            reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }

        // Convert the contents of the pack file into an array of card values
        String[] cardValuesStr = packContents.split("\n");
        int[] cardValues = new int[cardValuesStr.length];

        try {
            for (int i = 0; i < cardValuesStr.length; i++) {
                cardValues[i] = Integer.parseInt(cardValuesStr[i].trim());
            }
        } catch (NumberFormatException e) {
            System.out.println("ERROR: File contains non-integer values");
            return null;
        }

        return cardValues;
    }
}