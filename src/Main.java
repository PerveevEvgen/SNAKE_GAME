import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        int width = 800;
        int height = width;

        JFrame frame = new JFrame("Snake Game");
        frame.setVisible(true);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    SnakeGame snakeGame = new SnakeGame(width, height);
    frame.add(snakeGame);
    frame.pack();
    snakeGame.requestFocus();
    }
}