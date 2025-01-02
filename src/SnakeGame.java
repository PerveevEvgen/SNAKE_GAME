import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    int width, height;
    int tileSize = 25;
    int score = 0;

    private static class Tile {
        int x, y;

        public Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public Tile() {}
    }


    // Snake attributes
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // Food tile
    Tile food;

    private Image foodImage;
    private Image hadImage;
    private Image bodyImage;
    private Image backgroundImage;

    Random random;

    // Game logic
    Timer gameLoop;
    int velocityX, velocityY;
    boolean gameOver = false;

    SnakeGame(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(this.width, this.height));
        setBackground(Color.BLACK);


        snakeHead = new Tile();
        snakeBody = new ArrayList<>();

        backgroundImage = loadImage("src/back.png");
        foodImage = loadImage("src/food.png");
        bodyImage = loadImage("src/body.png");
        hadImage = loadImage("src/head.png");

        food = new Tile();

        addKeyListener(this);
        setFocusable(true);

        random = new Random();

        placeFood();
        placeSnake();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    private Image loadImage(String path) {
        try {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                System.out.println("Image loaded successfully: " + path);
                return ImageIO.read(imgFile);
            } else {
                System.out.println("Image file not found: " + path);
            }
        } catch (IOException e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
        return null; // Return null if the image couldn't be loaded
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw grid
       // g.setColor(Color.GRAY);
        for (int x = 0; x < width / tileSize; x++) {
            for (int y = 0; y < height / tileSize; y++) {
                g.drawImage(backgroundImage, x * tileSize, y * tileSize, tileSize, tileSize ,this);
            }
            //g.drawLine(x * tileSize, 0, x * tileSize, height);
            //g.drawLine(0, x * tileSize, width, x * tileSize);
            //g.drawImage(backgroundImage, food.x * tileSize, food.y * tileSize, tileSize, tileSize ,this);
        }

        // Draw food
       // g.setColor(Color.RED);
       // g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);
        g.drawImage(foodImage, food.x * tileSize + 1, food.y * tileSize + 1, tileSize -2, tileSize -2,this);

        // Draw snake
        //g.setColor(Color.GREEN);
        //g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);
        g.drawImage(hadImage, snakeHead.x * tileSize + 1, snakeHead.y * tileSize + 1, tileSize -2, tileSize -2,this);
        for (Tile tail : snakeBody) {
            //g.fillRect(tail.x * tileSize, tail.y * tileSize, tileSize, tileSize);
            g.drawImage(bodyImage, tail.x * tileSize + 1, tail.y * tileSize + 1, tileSize -2, tileSize -2,this);
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 10);


    }



    public void placeFood() {
        food.x = random.nextInt(width / tileSize);
        food.y = random.nextInt(height / tileSize);
    }

    public void placeSnake() {
        snakeHead.x = random.nextInt(width / tileSize);
        snakeHead.y = random.nextInt(height / tileSize);
        snakeBody.clear();
    }

    public boolean collision(Tile t1, Tile t2) {
        return t1.x == t2.x && t1.y == t2.y;
    }

    public void moveSnake() {
        Tile newTail = null;
        if (collision(snakeHead, food)) {
            newTail = new Tile(snakeBody.isEmpty() ? snakeHead.x : snakeBody.get(snakeBody.size() - 1).x,
                    snakeBody.isEmpty() ? snakeHead.y : snakeBody.get(snakeBody.size() - 1).y);
            placeFood();
            score += 10; // Increase score when food is eaten
        }

        // Move snake's body
        for (int i = snakeBody.size() - 1; i >= 1; i--) {
            snakeBody.get(i).x = snakeBody.get(i - 1).x;
            snakeBody.get(i).y = snakeBody.get(i - 1).y;
        }
        if (!snakeBody.isEmpty()) {
            snakeBody.get(0).x = snakeHead.x;
            snakeBody.get(0).y = snakeHead.y;
        }

        // Move snake's head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Add new tail part after movement
        if (newTail != null) {
            snakeBody.add(newTail);
        }

        // Check for game over conditions
        checkCollisions();
    }

    public void checkCollisions() {
        // Check for collision with body
        for (Tile snakePart : snakeBody) {
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
                gameLoop.stop();
                showGameOverMessage();
            }
        }

        // Check for collision with walls
        if (snakeHead.x < 0 || snakeHead.x >= width / tileSize || snakeHead.y < 0 || snakeHead.y >= height / tileSize) {
            gameOver = true;
            gameLoop.stop();
            showGameOverMessage();
        }
        //Check for collision with rocks
    }

    public void showGameOverMessage() {
        JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score);
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            moveSnake();
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (velocityY != 1) {
                    velocityX = 0;
                    velocityY = -1;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (velocityY != -1) {
                    velocityX = 0;
                    velocityY = 1;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (velocityX != 1) {
                    velocityX = -1;
                    velocityY = 0;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (velocityX != -1) {
                    velocityX = 1;
                    velocityY = 0;
                }
                break;
        }
    }
    // do not need
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

}
