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
		assertEquals(players.length, 2);
	}

	@Test
	void deckInitialisedCheck() {
		int[] cardValues = new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);
		CardDeck[] cardDecks = CardGame.getDecks();

		// Check that there are 2 decks each with 4 cards
		assertEquals(cardDecks.length, 2);

		assertEquals(cardDecks[0].getDeckSize(), 4);
		assertEquals(cardDecks[1].getDeckSize(), 4);
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

		assertEquals(players[0].getCard(0).getValue(), 1);
		assertEquals(players[0].getCard(1).getValue(), 2);
		assertEquals(players[0].getCard(2).getValue(), 3);
		assertEquals(players[0].getCard(3).getValue(), 4);
		
		assertEquals(players[1].getCard(0).getValue(), 5);
		assertEquals(players[1].getCard(1).getValue(), 6);
		assertEquals(players[1].getCard(2).getValue(), 7);
		assertEquals(players[1].getCard(3).getValue(), 8);
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
		
		assertEquals(cardDecks[0].getCard(0).getValue(), 9);
		assertEquals(cardDecks[0].getCard(1).getValue(), 10);
		assertEquals(cardDecks[0].getCard(2).getValue(), 11);
		assertEquals(cardDecks[0].getCard(3).getValue(), 12);
		
		assertEquals(cardDecks[1].getCard(0).getValue(), 13);
		assertEquals(cardDecks[1].getCard(1).getValue(), 14);
		assertEquals(cardDecks[1].getCard(2).getValue(), 15);
		assertEquals(cardDecks[1].getCard(3).getValue(), 16);
	}

	@Test
	void cardDeckDrawCardCheck() {
		assertEquals(1, 2);
	}

	@Test
	void cardDeckDiscardCardCheck() {
		assertEquals(1, 2);
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

		assertEquals(players[0].getCard(0).getValue(), 1);
		assertEquals(players[0].getCard(1).getValue(), 1);
		assertEquals(players[0].getCard(2).getValue(), 1);
		assertEquals(players[0].getCard(3).getValue(), 1);
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

		assertEquals(totalCards, 16);
	}
	
}
