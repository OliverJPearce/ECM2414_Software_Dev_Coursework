import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class CardGameTest {

	@AfterEach
	void clearGame() {
		// Wait for the player threads to stop before beginning the next test
		Player[] players = CardGame.getPlayers();
		CardDeck[] cardDecks = CardGame.getDecks();

		for (Player player : players) {
			player.stopPlayer(1);
		}

		for (CardDeck deck : cardDecks) {
            deck.endGame();
        }

		for (Player player : players) {
			try {
				player.join();
			} catch (InterruptedException e) {
				System.out.println("ERROR CLEARING GAME: Player join interrupted");
			}
		}
	}

	@Test
	void playerInitialisedCheck() {
		int[] cardValues = new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);
		Player[] players = CardGame.getPlayers();

		// Check that there are 2 players
		assertEquals(2, players.length, "Game should be initialised with 2 players");
	}

	@Test
	void deckInitialisedCheck() {
		int[] cardValues = new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);
		CardDeck[] cardDecks = CardGame.getDecks();

		// Check that there are 2 decks each with 4 cards
		assertEquals(2, cardDecks.length, "There should be 2 card decks when 16 cards are present");

		assertEquals(4, cardDecks[0].getDeckSize(), "Deck 1 should be initialised with 4 cards");
		assertEquals(4, cardDecks[1].getDeckSize(), "Deck 2 should be initialised with 4 cards");
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

		assertEquals(4, cardDeck.getDeckSize(), "The deck should be initialised with a length of 4");

		try {
			assertEquals(1, cardDeck.drawCard().getValue(), "The first drawn card should be the 1");
			assertEquals(3, cardDeck.getDeckSize(), "The deck size after drawing 1 is 3");

		} catch (InterruptedException e) {
			System.out.println("TEST cardDeckDrawCardCheck: Card draw was interrupted");
			assertFalse("cardDeckDrawCardCheck was interrupted", true);	// Fail the test since drawCard was interrupted
		}
	}

	@Test
	void cardDeckDiscardCardCheck() {
		int[] cardValues = new int[]{1,2,3,4};
		CardDeck cardDeck = new CardDeck(10, cardValues);

		assertEquals(4, cardDeck.getDeckSize(), "The deck should be initialised with a length of 4");

		Card discardCard = new Card(5);
		cardDeck.addCard(discardCard);

		assertEquals(5, cardDeck.getCard(4).getValue(), "The card at index 4 should be the newly added vard with value 5");
		assertEquals(5, cardDeck.getDeckSize(), "The deck should be 5 long with the newly added card");
	}

	@Test
	void cardDeckFIFOOrderingCheck() {
		int[] cardValues = new int[]{1,2,3,4};
		CardDeck cardDeck = new CardDeck(10, cardValues);

		// Add card to test FIFO deck ordering
		cardDeck.addCard(new Card(5));

		try {
			assertEquals(1, cardDeck.drawCard().getValue());
			assertEquals(2, cardDeck.drawCard().getValue());
			assertEquals(3, cardDeck.drawCard().getValue());
			assertEquals(4, cardDeck.drawCard().getValue());
			assertEquals(5, cardDeck.drawCard().getValue());
		} catch (InterruptedException e) {
			System.out.println("TEST cardDeckFIFOOrderingCheck: Card draw was interrupted");
			assertFalse("cardDeckFIFOOrderingCheck was interrupted", true);	// Fail the test since drawCard was interrupted
		}
	}

	@Test
	void invalidInputCheck() {
		int[] noCardValues = CardGame.readPack("invalid_location.txt");
		assertEquals(null, noCardValues, "Card values should be null since the pack invalid_location.txt doesn't exist");

		int[] cardValues = CardGame.readPack("test_cards.txt");
		assertEquals(32, cardValues.length, "Should have loaded 32 card values from test_cards.txt");

		for (int i = 0; i < cardValues.length; i++) {
			assertEquals(i + 1, cardValues[i], "Card " + (i + 1) + " should have value " + (i + 1));
		}
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

		// Wait for the players to finish the game
		Player[] players = CardGame.getPlayers();

		for (Player player : players) {
			try {
				player.join();
			} catch (InterruptedException e) {
				System.out.println("ERROR winningPlayerEndingHandCheck: Player join interrupted");
			}
		}

		// Check that player 1 has a whole hand of 1s
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

		// Wait for the players to finish the game
		Player[] players = CardGame.getPlayers();

		for (Player player : players) {
			try {
				player.join();
			} catch (InterruptedException e) {
				System.out.println("ERROR cardStabilityCheck: Player join interrupted");
			}
		}

		// Check that there are still the same number of cards in the game as at the start
		CardDeck[] cardDecks = CardGame.getDecks();
		int totalCards = cardDecks[0].getDeckSize() + cardDecks[1].getDeckSize() + 4 + 4;

		assertEquals(16, totalCards, "The total cards at the end of the game should be the same as at the start");
	}

	@Test
	@Timeout(5)	// Prevent the test from running forever, so fail after 5 seconds of the game running
	void playerThreadsTerminatedCheck() {
		int[] cardValues = new int[]{
			// P1, P2
			   1,  1,
			   1,  6,
			   1,  7,
			   4,  8,
			// D1, D2
				9, 13,
			   10, 14,
			   11, 15,
			   12, 16
		};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);

		// Run the game
		CardGame.startGame();

		// Wait for the players to finish the game
		Player[] players = CardGame.getPlayers();

		for (Player player : players) {
			try {
				player.join();
			} catch (InterruptedException e) {
				System.out.println("ERROR playerThreadsTerminatedCheck: Player join interrupted");
			}
		}

		// Check that all the player threads have been ended
		for (Player player : players) {
			assertFalse("Player " + player.getPlayerId() + " should be terminated after the game ends", player.isAlive());
		}
	}
	
	@Test
	@Timeout(5)	// Prevent the test from running forever, so fail after 5 seconds of the game running
	void playerOutputFileContentsCheck() {
		int[] cardValues = new int[]{
			// P1, P2
			   1,  1,
			   1,  6,
			   1,  7,
			   4,  8,
			// D1, D2
				9, 13,
			   10, 14,
			   11, 15,
			   12, 16
		};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);

		// Run the game
		CardGame.startGame();

		// Wait for the players to finish the game
		Player[] players = CardGame.getPlayers();

		for (Player player : players) {
			try {
				player.join();
			} catch (InterruptedException e) {
				System.out.println("ERROR playerThreadsTerminatedCheck: Player join interrupted");
			}
		}

		// Check that the output log files of player 1 contain the correct information
        try (BufferedReader reader = new BufferedReader(new FileReader("player1_output.txt"))) { 
            String line = reader.readLine();
			assertEquals("player 1 initial hand 1 1 1 4", line.trim(), "First line should display the correct initial hand for player 1");
			
			int endIndex = 0;
			boolean foundEnd = false;

			while ((line = reader.readLine()) != null) {
				if (line.contains("wins")) foundEnd = true;

				if (foundEnd) {
					if (endIndex == 0) assertEquals("player 1 wins", line.trim(), "Player 1 should win");
					if (endIndex == 1) assertEquals("player 1 exits", line.trim(), "Player 1 should exit");
					if (endIndex == 2) assertEquals("player 1 final hand: 1 1 1 1", line.trim(), "Last line should display the correct ending hand for player 1");
					endIndex++;
				}
			}
			
			reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
			assertFalse("Player 1 output file could not be found", false);
        }

		// Check that the output log files of player 2 contain the correct information
        try (BufferedReader reader = new BufferedReader(new FileReader("player2_output.txt"))) { 
            String line = reader.readLine();
			assertEquals("player 2 initial hand 1 6 7 8", line.trim(), "First line should display the correct initial hand for player 2");
			
			int endIndex = 0;
			boolean foundEnd = false;

			while ((line = reader.readLine()) != null) {
				if (line.contains("won")) foundEnd = true;

				if (foundEnd) {
					if (endIndex == 0) assertEquals("player 1 has informed player 2 that player 1 has won", line.trim(), "Player 1 should have told player 2 that they won");
					if (endIndex == 1) assertEquals("player 2 exits", line.trim(), "Player 2 should exit");
					if (endIndex == 2) assertTrue("Last line should display the ending hand for player 2", line.trim().contains("player 2 final hand: "));
					endIndex++;
				}
			}
			
			reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
			assertFalse("Player 2 output file could not be found", false);
        }
	}

	@Test
	@Timeout(5)	// Prevent the test from running forever, so fail after 5 seconds of the game running
	void playerDiscardDenominationCheck() {
		int[] cardValues = new int[]{
			// P1, P2
			   1,  1,
			   2,  6,
			   3,  1,
			   4,  2,
			// D1, D2
				9, 13,
			    2, 14,
			    1,  2,
			   12,  1
		};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);

		// Run the game
		CardGame.startGame();

		// Wait for the players to finish the game
		Player[] players = CardGame.getPlayers();

		for (Player player : players) {
			try {
				player.join();
			} catch (InterruptedException e) {
				System.out.println("ERROR playerThreadsTerminatedCheck: Player join interrupted");
			}
		}

		// Check that the output log files show that player 1 never discards a 1
        try (BufferedReader reader = new BufferedReader(new FileReader("player1_output.txt"))) { 
            String line;

			while ((line = reader.readLine()) != null) {
				if (line.contains("discards")) {
					assertFalse("Player 1 should never discard a 1", line.substring(10).contains(" 1 "));
				}
			}
			
			reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
			assertFalse("Player 1 output file could not be found", false);
        }

		// Check that the output log files show that player 2 never discards a 2
        try (BufferedReader reader = new BufferedReader(new FileReader("player2_output.txt"))) { 
            String line;

			while ((line = reader.readLine()) != null) {
				if (line.contains("discards")) {
					assertFalse("Player 2 should never discard a 2", line.substring(10).contains(" 2 "));
				}
			}
			
			reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
			assertFalse("Player 2 output file could not be found", false);
        }
	}
}
