import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class CardGameTest {

	@Test
	void playerInitialisedCheck() {
		int[] cardValues = new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);
		Player[] players = CardGame.getPlayers();

		// Check that there are 2 players
		assertEquals(2, players.length);
	}

	@Test
	void deckInitialisedCheck() {
		int[] cardValues = new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);
		CardDeck[] cardDecks = CardGame.getDecks();

		// Check that there are 2 decks each with 4 cards
		assertEquals(2, cardDecks.length);

		assertEquals(4, cardDecks[0].getDeckSize());
		assertEquals(4, cardDecks[1].getDeckSize());
	}

	@Test
	void playerStartingHandsCheck() {
		int[] cardValues = new int[]{
			// P1, P2
			   1,  5,
			   2,  6,
			   3,  7,
			   4,  8,
			// D1, D2
			   9,  13,
			   10, 14,
			   11, 15,
			   12, 16
		};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);
		Player[] players = CardGame.getPlayers();

		assertEquals(1, players[0].getCard(0).getValue());
		assertEquals(2, players[0].getCard(1).getValue());
		assertEquals(3, players[0].getCard(2).getValue());
		assertEquals(4, players[0].getCard(3).getValue());
		
		assertEquals(5, players[1].getCard(0).getValue());
		assertEquals(6, players[1].getCard(1).getValue());
		assertEquals(7, players[1].getCard(2).getValue());
		assertEquals(8, players[1].getCard(3).getValue());
	}

	@Test
	void deckStartingCardsCheck() {
		int[] cardValues = new int[]{
			// P1, P2
			   1,  5,
			   2,  6,
			   3,  7,
			   4,  8,
			// D1, D2
			   9,  13,
			   10, 14,
			   11, 15,
			   12, 16
		};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);
		CardDeck[] cardDecks = CardGame.getDecks();
		
		assertEquals(9, cardDecks[0].getCard(0).getValue());
		assertEquals(10, cardDecks[0].getCard(1).getValue());
		assertEquals(11, cardDecks[0].getCard(2).getValue());
		assertEquals(12, cardDecks[0].getCard(3).getValue());
		
		assertEquals(13, cardDecks[1].getCard(0).getValue());
		assertEquals(14, cardDecks[1].getCard(1).getValue());
		assertEquals(15, cardDecks[1].getCard(2).getValue());
		assertEquals(16, cardDecks[1].getCard(3).getValue());
	}

	@Test
	void cardDeckDrawCardCheck() {
		int[] cardValues = new int[]{1,2,3,4};
		CardDeck cardDeck = new CardDeck(10, cardValues);

		assertEquals(4, cardDeck.getDeckSize());

		Card drawnCard = new Card(0);

		try {
			drawnCard = cardDeck.drawCard();

			assertEquals(1, drawnCard.getValue());
			assertEquals(3, cardDeck.getDeckSize());

		} catch (InterruptedException e) {
			System.out.println("TEST cardDeckDrawCardCheck: Card draw interrupted");
			assertEquals(1, 2);	// Fail the test since drawCard was interrupted
		}
	}

	@Test
	void cardDeckDiscardCardCheck() {
		int[] cardValues = new int[]{1,2,3,4};
		CardDeck cardDeck = new CardDeck(10, cardValues);

		assertEquals(4, cardDeck.getDeckSize());

		Card discardCard = new Card(5);
		cardDeck.addCard(discardCard);

		assertEquals(5, cardDeck.getCard(4).getValue());
		assertEquals(5, cardDeck.getDeckSize());
	}

	@Test
	@Timeout(5)	// Prevent the test from running forever, so fail after 5 seconds of the game running
	void winningPlayerEndingHandCheck() {
		// Setup means player 2 should discard all the 1s allowing player 1 to win
		int[] cardValues = new int[]{
			// P1, P2
			   1,  1,
			   2,  1,
			   3,  1,
			   4,  8,
			// D1, D2
			   9,  13,
			   10, 14,
			   11, 15,
			   12, 16
		};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);

		// Run the game
		CardGame.startGame();

		// Wait for the game to finish
		while (!CardGame.isGameFinished()) {
			try {
            	Thread.sleep(50);
			} catch (InterruptedException e) {
				// Continue waiting for game to be over
			}
        }

		// Check that player 1 has a whole hand of 1s
		Player[] players = CardGame.getPlayers();

		assertEquals(1, players[0].getCard(0).getValue());
		assertEquals(1, players[0].getCard(1).getValue());
		assertEquals(1, players[0].getCard(2).getValue());
		assertEquals(1, players[0].getCard(3).getValue());
	}

	@Test
	@Timeout(5)	// Prevent the test from running forever, so fail after 5 seconds of the game running
	void cardStabilityCheck() {
		int[] cardValues = new int[]{
			// P1, P2
			   1,  1,
			   2,  6,
			   3,  7,
			   4,  8,
			// D1, D2
				9, 13,
			   10,  1,
			    1, 15,
			   12, 16
		};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);

		// Run the game
		CardGame.startGame();

		// Wait for the game to finish
		while (!CardGame.isGameFinished()) {
			try {
            	Thread.sleep(50);
			} catch (InterruptedException e) {
				// Continue waiting for game to be over
			}
        }

		// Check that there are still the same number of cards in the game as at the start
		CardDeck[] cardDecks = CardGame.getDecks();
		int totalCards = cardDecks[0].getDeckSize() + cardDecks[1].getDeckSize() + 4 + 4;

		assertEquals(16, totalCards);
	}
	
}
