package BlocKuzushi;	

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Racket {
    public static final int WIDTH = 80; 
    public static final int HEIGHT = 5; 

    public static int centerPos; 

    public Racket() {
        centerPos = MainPanel.WIDTH / 2; 
    }

    public void move(int x) {
        if (x < WIDTH / 2) {
            centerPos = WIDTH / 2;
        } else if (x > MainPanel.WIDTH - WIDTH / 2) {
            centerPos = MainPanel.WIDTH - WIDTH / 2;
        } else {
            centerPos = x;
        }
    }
    
    public void moveLeft() {
    	int leftLimit = WIDTH / 2;
        if (centerPos > leftLimit) {
            centerPos -= 10;
            if (centerPos < leftLimit) {
                centerPos = leftLimit;
            }
        }
}
    
    public void moveRight() {
    	int rightLimit = MainPanel.WIDTH - WIDTH / 2;
        if (centerPos < rightLimit) {
            centerPos += 10;
            if (centerPos > rightLimit) {
                centerPos = rightLimit;
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(centerPos - WIDTH / 2, MainPanel.HEIGHT - HEIGHT, WIDTH, HEIGHT); 
    }
    
    public boolean intersects(int x, int y, int width, int height) {
        Rectangle thisRect = new Rectangle(centerPos - WIDTH/2, MainPanel.HEIGHT - HEIGHT, WIDTH, HEIGHT);
        Rectangle targetRect = new Rectangle(x, y, width, height);
        return thisRect.intersects(targetRect); 
    }
}