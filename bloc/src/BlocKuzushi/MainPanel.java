package BlocKuzushi;

import java.awt.Color;		
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics2D;
import java.io.*;
import java.io.File;
import javax.sound.sampled.*;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class MainPanel extends JPanel implements Runnable, MouseListener, MouseMotionListener, KeyListener {

    public static final int WIDTH = 360;
    public static final int HEIGHT = 480;
    private static final int READY = 0;
    private static final int PLAYING = 1;
    private static final int GAME_OVER = 2;
    private static final int GAME_CLEAR = 3;
    private boolean blink = true;
    private Timer timer;
    private ArrayList<Ball> balls = new ArrayList<>();

    
    private Racket racket;  
    private Ball ball;
    private Thread gameThread; 
    private int score; 
    private int totalScore = 0;
    private boolean gameOver; 
    private boolean gameReset;
    private boolean getBall = true;
    private int scene; 
    private LevelGame levelGame;
    private int level;
    private int lives;


    private final int resetX = MainPanel.WIDTH / 2 - 40;
    private final int resetY = MainPanel.HEIGHT / 2 + 25 ;
    private final int resetWidth = 60;
    private final int resetHeight = 20;
    private final int nextX = MainPanel.WIDTH / 2 - 40;
    private final int nextY = MainPanel.HEIGHT / 2;
    private final int nextWidth = 60;
    private final int nextHeight = 20;
    private Image backgroundImage;
    private Sound Sound;


//===========================MAIN======================================///
    
    public MainPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        Sound = new Sound();
        Sound.backSound("sound/backsound.wav");
        Sound.blockSound("sound/blockbreak.wav");
        Sound.overSound("sound/over1.wav");
        score = 0;
    	lives = 3;
        gameOver = false;
        racket = new Racket();
        ball = new Ball();
        timer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                blink = !blink;
                repaint();
            }
        });
        timer.start();
		backgroundImage = new ImageIcon("img/background.png").getImage();
		levelGame = new LevelGame();
		scene = READY;
        Sound.playBackSound(0.5f);
		level = 3;
		repaint();
    }

    
  //=============================FUNCTIONS================================//

    // ゲームループ
    public void run() {
        while (true) {
            if (scene == PLAYING) {
            	for (Ball ball : balls) {
            		 if (gameReset == true) {
                         ball.move();
            			 getBall = false;
            		 }
                }
                checkGame();
                repaint();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
        
    // ゲームをリセットするメソッド
    public void startGame() {
    	scene = PLAYING;
        racket = new Racket();
        balls.clear();
        for (int i = 0; i < level; i++) {
        	balls.add(new Ball());
        }
        levelGame.setLevel(level);
        gameOver = false;
        gameReset = false;
        score = totalScore;
        Sound.playBackSound(-20f);
        repaint();
    }
    
    
//=========================================Conditions==========================================//

    public void drawBalls(Graphics g) {
            ball.draw(g);
    }

    public void checkGame() {
        if (!gameOver) {
            checkScore();
            boolean allBallsFallen = true;
            for (Ball ball : balls) {
                if (ball.getY() < MainPanel.HEIGHT - Racket.HEIGHT) {
                    allBallsFallen = false;
                    break;
                }
     			 getBall = false;
            }
            if (allBallsFallen) {
            	lives--;
            	if (lives <= 0) {
	                gameOver = true;
	                scene = GAME_OVER;
	                Sound.playOverSound();
	                level = 1;
	                totalScore = 0;
            	} else {
                    resetAfterFall();
	                gameReset = false;
            	}
            }
        }
    }
        
        private void resetAfterFall() {
            racket = new Racket();
            balls.clear();
            for (int i = 0; i < level; i++) {
                balls.add(new Ball());
            }
            levelGame.setLevel(level);
            gameOver = false;
            getBall = true;
            score = totalScore;
            Sound.playBackSound(-20f);
            repaint();
        }

    private boolean allBlocksCleared() {
        for (int i = 0; i < levelGame.getBlocks().length; i++) {
            if (levelGame.getBlocks()[i].isPainted()) {
                return false;
            }
        }
        return true;
    }

    // スコアをチェックするメソッド
    private void checkScore() {
    	for (Ball ball : balls) {
	        for (int i = 0; i < levelGame.getBlocks().length; i++) {
	            if (!levelGame.getBlocks()[i].isPainted()) {
	                continue;
	            }
	            if (levelGame.getBlocks()[i].isPainted() && levelGame.getBlocks()[i].ballHit(ball)) {
	                if (levelGame.getBlocks()[i].getType().equals("red")) {
	                    score += 5;
	                } else if (levelGame.getBlocks()[i].getType().equals("blue")) {
	                    score += 10;
	                }
	                levelGame.getBlocks()[i].delete();
	                Sound.playBlockSound();
	                break;
	            }
	        }
	        if (allBlocksCleared()) {
	        	level++;
	            scene = GAME_CLEAR;
	            if ( level == 3) {
	            	break;
	            }
	            return;
	        }
	            repaint();
	        if (racket.intersects(ball.getX(), ball.getY(), ball.getSize(), ball.getSize())) {
	            ball.bounceY();
	        }
	    }
    }
    

//=============================================-GRAPHICS==================================//
    
    public void paint(Graphics g) {
    	super.paintComponent(g);
    	switch(scene) {
    	case READY:
    		ready(g);	
    		break;
    	case PLAYING:
    		playing(g);
    		break;
    	case GAME_CLEAR:
    		gameclear(g);
    		break;
    	case GAME_OVER:
    		gameover(g); 
    		break;
    	} 
    }
    
    public void ready(Graphics g) {
    	Graphics2D g2D = (Graphics2D) g;
    	g2D.drawImage(backgroundImage, 0, 0, this);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(blink ? Color.WHITE : Color.GREEN);
        g.drawString("START", MainPanel.WIDTH / 2 - 40, MainPanel.HEIGHT / 2);
    }
    
    public void playing(Graphics g) {
    	g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
    	g.setColor(Color.WHITE);
        g.drawString("SCORE:" + score, 290, 300);
        g.drawString("LIVES: " + lives, 300, 330);
        racket.draw(g);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("STAGE " + level , MainPanel.WIDTH / 2 - 50, 30);
        for (Ball ball : balls) {
            ball.draw(g);
        }
        Block[] blocks = levelGame.getBlocks();
        if (blocks != null) {
            for (int i = 0; i < blocks.length; i++) {
                if (blocks[i] != null && blocks[i].isPainted()) {
                	blocks[i].draw(g);
                }
            }
        }
    }

    public void gameover(Graphics g) {
    	g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("HIGHSCORE: " + score, MainPanel.WIDTH / 2 - 50, MainPanel.HEIGHT / 2 + 20);
        g.setColor(Color.WHITE);
        g.drawRect(resetX, resetY, resetWidth, resetHeight);
        g.drawString("RESET", MainPanel.WIDTH / 2 - 30, MainPanel.HEIGHT / 2 + 40);
	    g.setColor(Color.WHITE);
	    g.setFont(new Font("Arial", Font.BOLD, 24));
	    g.drawString("GAME OVER", MainPanel.WIDTH / 2 - 80, MainPanel.HEIGHT / 2);
    }
    
    public void gameclear(Graphics g) {
    	g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        if ( level <= 3) {
	        g.setColor(Color.WHITE);
	        g.drawString("CLEAR GAME", MainPanel.WIDTH / 2 - 40, MainPanel.HEIGHT / 2);
	        g.setColor(Color.WHITE);
	        g.drawString("NEXT", MainPanel.WIDTH / 2 - 20, MainPanel.HEIGHT / 2 + 20);
        } else if ( level > 3){
        	g.setFont(new Font("Arial", Font.BOLD, 24));
        	g.setColor(blink ? Color.RED : Color.GREEN);
    	    g.drawString("YOU WIN", MainPanel.WIDTH / 2 - 55, MainPanel.HEIGHT / 2);
        }
    }

    
//============================MOUSE SETTING======================================//
    
    // リセットボタンがクリックされたかを判定するメソッド
    private boolean isResetClicked(int x, int y) {
        return x >= resetX && x <= resetX + resetWidth && y >= resetY && y <= resetY + resetHeight;
    }

    private boolean isNextClicked(int x, int y) {
        return x >= nextX && x <= nextX + nextWidth && y >= nextY && y <= nextY + nextHeight;
    }
    
    // スタートボタンがクリックされたかを判定するメソッド
    private boolean isStartClicked(int x, int y) {
    	return x >= 0 && x <= WIDTH && y >= 0 && y <= HEIGHT;
    }
    
    private boolean isgameReset(int x, int y) {
        return x >= 0 && x <= WIDTH && y >= 0 && y <= HEIGHT;
    }

    // マウスクリック時の処理を行うメソッド
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if (isgameReset(x, y)) {
        	gameReset = true;
        }
        
        if (scene == READY) {
        	if (isStartClicked(x, y)) {
        		startGame();
        		gameThread = new Thread(this);
                gameThread.start(); // ゲームスレッドを開始する
        	}
        } else if (scene == GAME_OVER) {
        	if (isResetClicked(x, y)) {
        		lives = 3;
        		startGame();
                gameThread = new Thread(this);
                gameThread.start(); // ゲームスレッドを開始する
        	}
        } else if (scene == GAME_CLEAR) {
        	totalScore += score;
        	if (isNextClicked(x, y)) {
        		levelGame.setLevel(level);
        		startGame();
        		gameThread = new Thread(this);
                gameThread.start();
        	}
        }
    }
    
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        racket.move(x);
        if (getBall) {
	        for (Ball ball : balls) {
	                ball.moveWithRacket(Racket.centerPos);
	        }
        }
        repaint();
    }
    
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            racket.moveLeft();
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            racket.moveRight();
        }
        if (getBall) {
	        for (Ball ball : balls) {
	            ball.moveWithRacket(Racket.centerPos);
	        }
        }
        repaint();
    }
    
    public void mouseDragged(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void keyTyped(KeyEvent e) {}
    
    
    
    
//=============================SOUND================================//
    
    public class Sound {
        private Clip backgroundClip;
        private Clip blockClip;
        private Clip overClip;

        public void backSound(String filePath) {
            try {
                File soundFile = new File(filePath);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                backgroundClip = AudioSystem.getClip();
                backgroundClip.open(audioIn);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
        
        public void playBackSound(float volume) {
            if (backgroundClip != null) {
            	backgroundClip.setFramePosition(0);
            	backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
                
                Control control = backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
                FloatControl floatControl = (FloatControl) control;
                
                floatControl.setValue(volume);
                
                backgroundClip.start();
            }
        }
        

        public void blockSound(String filePath) {
            try {
                File soundFile = new File(filePath);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                blockClip = AudioSystem.getClip();
                blockClip.open(audioIn);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }

        public void playBlockSound() {
            if (blockClip != null) {
                blockClip.setFramePosition(0);
                blockClip.start();
            }
        }
        
        public void overSound(String filePath) {
            try {
                File soundFile = new File(filePath);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                overClip = AudioSystem.getClip();
                overClip.open(audioIn);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
        public void playOverSound() {
            if (overClip != null) {
                overClip.setFramePosition(0);
                overClip.start();
            }
        }
    }
}
