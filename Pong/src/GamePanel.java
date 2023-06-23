import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {
	//dichiaro final e static per convenzione: static perch� se avessi pi� istanze del game panel condivideranno una unica variabile che �
	//final perch� fissa e immodificabile
	static final int GAME_WIDTH=1000;
	//altezza del tavolo da gioco castata a int e proporzionata alla larghezza precedentemente fissata
	static final int GAME_HEIGHT= (int)(GAME_WIDTH*(0.5555));
	static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT);
	static final int BALL_DIAMETER = 20;
	static final int PADDLE_WIDTH = 25;
	static final int PADDLE_HEIGHT = 100;
	Thread gameThread;
	Image image;
	Graphics graphics;
	Random random;
	Paddle paddle1;
	Paddle paddle2;
	Ball ball;
	Score score;
	
	GamePanel() {
		newPaddles();
		newBall();
		score = new Score(GAME_WIDTH,GAME_HEIGHT);
		this.setFocusable(true);
		this.addKeyListener(new AL());
		this.setPreferredSize(SCREEN_SIZE);
		
		gameThread = new Thread(this);
		gameThread.start();
		
	}
	
	public void newBall() {
		//col random facciamo in modo che la palla inizi non perfettamente al centro, ma in un punto a caso al centro dello schermo sull'asse y
		random = new Random();
		ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2),random.nextInt(GAME_HEIGHT-BALL_DIAMETER),BALL_DIAMETER,BALL_DIAMETER);
		
	}
	
	public void newPaddles() {
		paddle1 = new Paddle(0,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,1);
		paddle2 = new Paddle(GAME_WIDTH-PADDLE_WIDTH,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,2);
	}
	
	public void paint(Graphics g) {
		image = createImage(getWidth(),getHeight());
		graphics = image.getGraphics();
		draw(graphics);
		g.drawImage(image,0,0,this);
		
	}
	
	public void draw(Graphics g) {
		paddle1.draw(g);
		paddle2.draw(g);
		ball.draw(g);
		score.draw(g);
		Toolkit.getDefaultToolkit().sync(); //questa linea di codice aiuta con l'animazione
	}
	
	public void move() {
		//per rendere il movimento pi� fluido
		paddle1.move();
		paddle2.move();
		ball.move();
		
	}
	
	public void checkCollision() {
		
		//per far rimbalzare la palla sul borso superiore e inferiore dello schermo
		if(ball.y <= 0) {
			ball.setYDirection(-ball.yVelocity);
		}
		if(ball.y >= GAME_HEIGHT-BALL_DIAMETER) {
			ball.setYDirection(-ball.yVelocity);
		}
		//per far rimbalzare la palla sulle racchette
		//ereditiamo il metodo intersects da rectangle dato che ball � sottoclasse di rectangle
		if(ball.intersects(paddle1)) {
			ball.xVelocity = Math.abs(ball.xVelocity);
			ball.xVelocity++; //opzionale 
			if (ball.yVelocity>0)
				ball.yVelocity++; //opzionale
			else
				ball.yVelocity--;
			ball.setXDirection(ball.xVelocity);
			ball.setYDirection(ball.yVelocity);
			
		}
		if(ball.intersects(paddle2)) {
			ball.xVelocity = Math.abs(ball.xVelocity);
			ball.xVelocity++; //opzionale 
			if (ball.yVelocity>0)
				ball.yVelocity++; //opzionale
			else
				ball.yVelocity--;
			ball.setXDirection(-ball.xVelocity);
			ball.setYDirection(ball.yVelocity);
			
		}
		
		
		//ferma le racchette ai bordi del campo, cos� non escono dallo schermo
		if (paddle1.y<=0)
			paddle1.y=0;
		if(paddle1.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
			paddle1.y = GAME_HEIGHT-PADDLE_HEIGHT;
		
		if (paddle2.y<=0)
			paddle2.y=0;
		if(paddle2.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
			paddle2.y = GAME_HEIGHT-PADDLE_HEIGHT;
		
		//dai a un giocatore 1 punto, resetta palla e racchette
		if(ball.x <= 0) {
			score.player2++;
			newPaddles();
			newBall();
			System.out.println("Player 2: "+score.player2);
		}
		if(ball.x >= GAME_WIDTH-BALL_DIAMETER) {
			score.player1++;
			newPaddles();
			newBall();
			System.out.println("Player 1: "+score.player1);
			
		}
		
	}
	
	public void run() {
		// game loop
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		while(true) {
			long now = System.nanoTime();
			delta +=(now - lastTime)/ns;
			lastTime = now;
			if(delta >= 1) {
				move();
				checkCollision();
				repaint();
				delta--;
			}
		}
		
	}
	
//inner class action listener
	public class AL extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			paddle1.keyPressed(e);
			paddle1.keyPressed(e);
	}
		public void keyReleased(KeyEvent e) {
			paddle1.keyReleased(e);
			paddle2.keyReleased(e);
		}
		
}
}
