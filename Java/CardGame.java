
class Player extends Thread {   
    public void run() {
        
        System.out.println("Player thread running");
    }
}

class Card extends Thread {
    public void run() {
        System.out.println("Card thread running");
    }
}

public class CardGame {

    public static void main(String[] args) {
        System.out.println("Hello!");

        Player player = new Player();
        player.start();

    }
}