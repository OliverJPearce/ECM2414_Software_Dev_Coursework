import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class CardGameTest {

	@Test
	void playerAndDeckInitialisedCheck() {
		int[] cardValues = new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

		// Total cards = 8 * player count
		CardGame.distributeCards(cardValues, 2, 16);
		Player[] players = CardGame.getPlayers();
		CardDeck[] cardDecks = CardGame.getDecks();

		// Check that there are 2 players and 2 decks
		assertEquals(players.length, 2);
		assertEquals(cardDecks.length, 2);
	}

	@Test
	void playerStartingHandCheck() {
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
		CardDeck[] cardDecks = CardGame.getDecks();

		// THESE SHOULD REALLY ALL BE SEPERATE TESTS!!!
		assertEquals(players.length, 2);
		assertEquals(cardDecks.length, 2);

		assertEquals(players[0].getCard(0).getValue(), 1);
		assertEquals(players[0].getCard(1).getValue(), 2);
		assertEquals(players[0].getCard(2).getValue(), 3);
		assertEquals(players[0].getCard(3).getValue(), 4);
		
		assertEquals(players[1].getCard(0).getValue(), 5);
		assertEquals(players[1].getCard(1).getValue(), 6);
		assertEquals(players[1].getCard(2).getValue(), 7);
		assertEquals(players[1].getCard(3).getValue(), 8);

		assertEquals(cardDecks[0].getCard(0).getValue(), 9);
		assertEquals(cardDecks[0].getCard(1).getValue(), 10);
		assertEquals(cardDecks[0].getCard(2).getValue(), 11);
		assertEquals(cardDecks[0].getCard(3).getValue(), 12);
		
		assertEquals(cardDecks[1].getCard(0).getValue(), 13);
		assertEquals(cardDecks[1].getCard(1).getValue(), 14);
		assertEquals(cardDecks[1].getCard(2).getValue(), 15);
		assertEquals(cardDecks[1].getCard(3).getValue(), 16);
	}
}
