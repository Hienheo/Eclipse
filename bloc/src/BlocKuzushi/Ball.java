package BlocKuzushi;

import java.awt.Graphics;	
import java.util.Random;
import java.awt.Image;	
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

public class Ball {

    public static final int SIZE = 10; // ボールのサイズ
    private Image ball;

    private int x, y; // ボールの位置
    private int vx, vy; // ボールの速度
    private boolean collidedWithBlock; // ブロックと衝突したかどうかを示すフラグ
    

    
    public static final int TOP_SIDE = 0;
    public static final int BOTTOM_SIDE = 1;
    public static final int LEFT_SIDE = 2;
    public static final int RIGHT_SIDE = 3;
    // コンストラクタ
    public Ball() {
        // ボールの初期位置を設定
        x = Racket.centerPos - SIZE/2;
        y = MainPanel.HEIGHT - SIZE - Racket.HEIGHT;
        // 初期速度を設定
        Random random = new Random();
        do {
            vx = random.nextInt(14) - 7;
            vy = random.nextBoolean() ? -7 : 7;
        } while (vx == 0 || vy == 0);
        collidedWithBlock = false;
        // ブロックとの衝突状態を初期化
        try {
            ball = ImageIO.read(new File("img/ball.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ボールを描画するメソッド
    public void draw(Graphics g) {
    	g.drawImage(ball, x, y, null);
    }

    // ボールを移動させるメソッド
    public void move() {
	        x += vx;
	        y += vy;

	        // 画面端に衝突した場合の処理
	        if (x <= 0 || x >= MainPanel.WIDTH - SIZE) {
	            bounceX(); // X方向の速度を反転
	            if (x <= 0)
	                x = 0;
	            if (x >= MainPanel.WIDTH - SIZE)
	                x = MainPanel.WIDTH - SIZE;
	        }
	        if (y <= 0) {
	            bounceY(); // Y方向の速度を反転
	            if (y <= 0)
	                y = 0;
	        }
    }
    
    public void moveWithRacket(int racketX) {
        int newBallX = racketX - (Racket.WIDTH / 2) + (Racket.WIDTH - SIZE) / 2;

        if (newBallX < 0) {
            x = 0;
        } else if (newBallX + SIZE > MainPanel.WIDTH) {
        	x = MainPanel.WIDTH - SIZE;
        } else {
            x = newBallX;
        }
    }
    // X方向の速度を反転するメソッド
    public void bounceX() {
        vx = -vx;
    }

    // Y方向の速度を反転するメソッド
    // Y方向の速度を反転するメソッド
    public void bounceY() {
        vy = -vy;
    }

    // X座標を取得するメソッド
    public int getX() {
        return x;
    }

    // Y座標を取得するメソッド
    public int getY() {
        return y;
    }
    

    // ボールのサイズを取得するメソッド
    public int getSize() {
        return SIZE;
    }

    // ブロックと衝突したかどうかを取得するメソッド
    public boolean hasCollidedWithBlock() {
        return collidedWithBlock;
    }

    // ブロックとの衝突状態を設定するメソッド
    public void setCollidedWithBlock(boolean collided) {
        collidedWithBlock = collided;
    }
}