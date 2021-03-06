import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.List;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.print.DocFlavor.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;
import sun.tools.jar.Main;
import java.io.*;
import javax.sound.sampled.*;


interface Constants
{   
	final int MOVEAMOUNT = 6;
	
	final int PAINT_WIDTH = 500;
	
	final int PAINT_HEIGHT = 500;
	
	final int ASTEROID_COLLIDE_RADIUS = 30;
	
	final int SHIP_COLLIDE_RADIUS = 20;
	
	final int SCREEN_SIZE = 500;
	
	final int LASER_SPEED = 10;
	
	final int SIZE_OF_ASTEROID = 20;
	
	final int SLEEP_AMOUNT = 75;
	
	final int ASTEROID_GENERATION_RATE = 7;
	
	final int SCORE_AMOUNT_ONE = 100;
	
	final int SHIP_SIZE = 38;
	
	final int MAX_ASTEROIDS_GENERATED = 20;
	
	final int LASER_SIZE = 20;
	
	final int FRAMES_PER_GRNERATION = 5;
	
	final String BackgroundString = "C://Users//Chris//Downloads//490497-n300_irac.jpg";
	
	final String smallAsteroidString = "C://Users//Chris//Downloads//oie_transparent.png";
	
	final String SpaceshipString = "C://Users//Chris//Downloads//Ship.png";
}

public class firstApplet extends JApplet implements Constants
{
	ActionPanel mainPanel;
	
	AudioPlayer MGP;
	
	public void init()
	{		
		setSize(SCREEN_SIZE, SCREEN_SIZE);
		
		mainPanel=new ActionPanel();		
		
		add(mainPanel);
		
		mainPanel.init();
		
	}
	
	public void paint(Graphics g)
	{
		mainPanel.paintComponents(g);		
	}
	
	public void stop()
	{
		
	}
	
	public void start()
	{		
		music();
		
		mainPanel.start();		
	}

	public void music()
	{
		
		try 
		{
		    File yourFile = new File("C://Users//Chris//Documents//GitHub//applet//music.wav");
		    
		    AudioInputStream stream;
		    
		    AudioFormat format;
		    
		    DataLine.Info info;
		    
		    Clip clip;

		    stream = AudioSystem.getAudioInputStream(yourFile);
		    
		    format = stream.getFormat();
		    
		    info = new DataLine.Info(Clip.class, format);
		    
		    clip = (Clip) AudioSystem.getLine(info);
		    
		    clip.open(stream);
		    
		      
		    clip.start();
		}
		catch (Exception e) {
		   System.out.println("No music for you!");
		}
	}
}




class ActionPanel extends JPanel implements KeyListener, Runnable, Constants
{
	
	volatile boolean shootLaser  = false;
	
	AllThings gameItems;
	
	boolean paused;
	
	int dx,dy;
	
	Thread runner;
	
	ScoreBoard score;
	
	public void init()
	{
		score = new ScoreBoard();
		
		this.add(score);
		
		gameItems = new AllThings();
		
		repaint();
		
		this.setFocusable(true);
		
		this.requestFocusInWindow();
		
		this.setVisible(true);
				
		dx=0;
		
		dy=0;
		
		paused = false;
	}
	
	public void paint(Graphics g)
	{
		
		//System.out.println("paint");
		
		paintBackground(g);
		
		g.setColor(Color.BLUE);
		
		score.paint(g);
		
		gameItems.paintObjects(g);
		
		
	}
	
	public void paintBackground(Graphics g)
	{
		BufferedImage picture=null;
		
		try 
		{
			picture = ImageIO.read(new File(BackgroundString));
		} 
		catch (IOException e) 
		{
			System.out.println("DID NOT GET FILE");
			
			e.printStackTrace();
		}
		
		g.drawImage(picture, 0, 0, null);
	}
	
	@Override
	public void keyPressed(KeyEvent e) 
	{
						
		int key = e.getKeyCode();
		
		if(key==KeyEvent.VK_UP)
		{
			dy=-MOVEAMOUNT;
		}
		else if (key==KeyEvent.VK_DOWN)
		{
			dy=MOVEAMOUNT;
		}
		else if (key==KeyEvent.VK_LEFT)
		{
			dx=-MOVEAMOUNT;
		}
		else if (key==KeyEvent.VK_RIGHT)
		{
			dx=MOVEAMOUNT;
		}
		else if(key==KeyEvent.VK_SPACE)
		{
			shootLaser=true;
		}
		else if(key==KeyEvent.VK_ESCAPE)
		{
			//System.out.println("Pause typed");
			
			paused = !paused;
		}
		
		//System.out.println("needUpdating listening = " +needUpdating);
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		// TODO Auto-generated method stub
		dx=0;
		dy=0;
	}

	@Override
	public void keyTyped(KeyEvent e) 
	{
		int key = e.getKeyCode();
		
		if(key==KeyEvent.VK_UP)
		{
			dy=-MOVEAMOUNT;
		}
		else if (key==KeyEvent.VK_DOWN)
		{
			dy=MOVEAMOUNT;
		}
		else if (key==KeyEvent.VK_LEFT)
		{
			dx=-MOVEAMOUNT;
		}
		else if (key==KeyEvent.VK_RIGHT)
		{
			dx=MOVEAMOUNT;
		}
		else if(key==KeyEvent.VK_SPACE)
		{
			shootLaser=true;
		}
		
	}
	
	public void pauseOrContinue()
	{
		
	}
	
	public void processMovement()
	{
		gameItems.update(dx,dy,shootLaser);
		
		this.shootLaser=false;
		
		if(this.gameItems.collisionThisTurn)
		{
			score.addToScore(SCORE_AMOUNT_ONE);
		}
	}

	public void start()
	{
		runner = new Thread(this);
		
		runner.start();
	}

	@Override
	public void run() 
	{
		
		this.addKeyListener(this);
		
		while(true)
		{
			try 
			{
				Thread.sleep(SLEEP_AMOUNT);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
				if(!paused)
				{
					processMovement();				
					
					repaint();
					
					//System.out.println("Should paint");
				}
								

				
			
		}
	}
}


abstract class GameObject implements Comparable<GameObject>, Constants
{
	int x,y,dx,dy;
	
	int collideRadius;
	
	BufferedImage picture;
	
	public GameObject(int x,int y, int dx, int dy, int collideRadius, String imageLocation)
	{
		//need to get picture
		picture = null;
		
		try 
		{
			this.picture = ImageIO.read(new File(imageLocation));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		this.x=x;
		
		this.y=y;
		
		this.collideRadius = collideRadius;
		
		this.dx = dx;
		
		this.dy = dy;
	}	
	
	//will be different for each class

	public boolean outOfBoundaries(int width, int height)
	{
		return x+collideRadius<0 || x-collideRadius>width || y-collideRadius > height || y+collideRadius<0;
	}
	
	void move()
	{
		x+=dx;
		y+=dy;
	}

	void drawObject(Graphics g)
	{
		g.drawImage(this.picture, this.x, this.y, null);
	}
	
	public int compareTo(GameObject other) 
	{
		return this.x-other.x;
	}
}

class MainGuy extends GameObject
{		
	public MainGuy(int x, int y, int collide) 
	{
		super(x, y,0,0, collide,SpaceshipString );
		// TODO Auto-generated constructor stub
	}

	public void collideAction() 
	{
		System.exit(0);		
	}
}

class Laser extends GameObject
{

	public Laser(int x, int y,int dx, int dy, int collideRadius, String imageLocation) 
	{
		super(x, y,dx,dy, collideRadius, imageLocation);
	}
	
}

class Asteroid extends GameObject
{
	
	
	public Asteroid(int x, int y, int collide, int velX, int velY) 
	{
		super(x, y,velX,velY, collide,smallAsteroidString);		
	}
		
	//Precondition: one.x <= two.x, will not collide along y
	static void collideAsteroids(Asteroid one, Asteroid two)
	{
		//System.out.println("bounced");
		
		one.bouncex();
		two.bouncex();
	}
	
	public void move()
	{
		x+=dx;
		y+=dy;
	}
	
	public void bouncex()
	{
		dx=-dx;
	}
	
	public void bouncey()
	{
		dy=-dy;
	}

}

class ScoreBoard extends JTextField
{
	int score;
	
	public ScoreBoard()
	{
		score=0;
	}
	
	public void addToScore(int x)
	{
		score+=x;
	}
	
	public void paint(Graphics g)
	{
		g.drawString("Your score : " + score, 0, 10);
	}
}

class AllThings implements Constants
{	
	ArrayList <Asteroid> asteroids = new ArrayList<Asteroid>();
		
	ArrayList <Laser> lasers = new ArrayList<Laser>();
	
	MainGuy SgtPepper;
	
	boolean collisionThisTurn = false;
	
	int numFrames;
	
	AllThings()
	{
		numFrames = 0;
		
		addAsteroid(0, 0, 10, 5);
		
		addAsteroid(500, 0, -10, 5);	
		
		addAsteroid(200, 0, -10, 5);	
		
		addAsteroid(300, 0, 10, 5);	
		
		SgtPepper = new MainGuy(250, 470, SHIP_COLLIDE_RADIUS);
	}
	
	void update(int dx, int dy, boolean shootLaser)
	{
		collisionThisTurn = false;
		
		if(shootLaser)
		{
			shootLaser();
		}
		
		
		if(numFrames%FRAMES_PER_GRNERATION==0)
		{
			keepAddingAsteroids();
		}
		
		//updates based on dx and dy (player's speed)
		
		SgtPepper.dx = dx;
		
		SgtPepper.dy = dy;		
		
		Collections.sort(asteroids);
		
		collisionHandle();			

		moveAll();
		
		numFrames++;
	}
	
	void keepAddingAsteroids()
	{
		Random rand = new Random();
		
		int randomX,randomVX, randomVY,i,asteroidsToGenerate;
				
		asteroidsToGenerate = (int) Math.ceil((numFrames*numFrames/1000));
		
		if(asteroidsToGenerate>MAX_ASTEROIDS_GENERATED)
		{
			asteroidsToGenerate = MAX_ASTEROIDS_GENERATED;
		}
		
		for(i=0;i<MAX_ASTEROIDS_GENERATED;i++)
		{

			randomX=rand.nextInt()%500;

			randomVX=rand.nextInt()%5;
			
			randomVY=rand.nextInt()%2+5;
			
			if(rand.nextInt()%ASTEROID_GENERATION_RATE==1)
			{
				addAsteroid(randomX, 0, randomVX, randomVY);	
			}
		}		
	}

	void addAsteroid(int x,int y, int velx, int vely)
	{
		Asteroid asteroidToAdd = new Asteroid(x, y,ASTEROID_COLLIDE_RADIUS,velx, vely);
		
		asteroids.add(asteroidToAdd);
	}
	
	void shootLaser()
	{
		Laser laserToAdd = new Laser(SgtPepper.x + SHIP_SIZE/2-LASER_SIZE/2, SgtPepper.y,SgtPepper.dx,SgtPepper.dy - LASER_SPEED, ASTEROID_COLLIDE_RADIUS,"C://Users//Chris//Downloads//laser_bullet.png");
 
		lasers.add(laserToAdd);
		
		//System.out.println("Laser#: " + (lasers.size()-1));
	}
	
	void moveAll()
	{
		SgtPepper.move();
		
		int i, numAsteroids;
		
		numAsteroids = asteroids.size();
		
		int numLasers = lasers.size();
		
		for(i=0;i<numAsteroids;i++)
		{
			asteroids.get(i).move();
		}
		
		for(i=0;i<numLasers;i++)
		{
			//System.out.println(lasers.get(i).dx);
			
			lasers.get(i).move();
		}
	}
	
	void remove(Asteroid kill)
	{
		//System.out.println("destroy asteroid");
		
		asteroids.remove(kill);
		
		//System.out.println("after kill asteroid");
		
		Collections.sort(asteroids);
		
		//System.out.println("after destroy asteroid");
	}
	
	void remove(Laser kill)
	{
		//System.out.println("destroy laser");
		
		lasers.remove(kill);
		
		//System.out.println("after kill laser");
		
		Collections.sort(lasers);
		
		//System.out.println("after destroy laser");
	}
	
	void collisionHandle()
	{
		handleAsteroids();
		
		handlePlayer();
	}
	
	void remove(GameObject laserOrAsteroid)
	{
		remove(laserOrAsteroid);
	}
	
	//Sorted list by x value, so only have to check neighbors
	void handleAsteroids()
	{
		//System.out.println("Handling the asteroid collisions");
		
		int i,size = asteroids.size();
		
		if(size==1)
		{
			return;
		}
		
		int laserLength = lasers.size(),j;
		
		//First remove unneccessairy lasers		
		for(j=0;j<laserLength;j++)
		{

			if(sideCollisionHandle(lasers.get(j)))
			{
				j--;
				
				laserLength --;
				
				continue;
			}
		}
		
		//Now we will check for asteroid collision and laser-asteroid collision		
		for(i=0;i<size;i++)
		{
			laserLength = lasers.size();
			
			for(j=0;j<laserLength;j++)
			{
				
				if(size>0 && laserHitAsteroid(lasers.get(j), asteroids.get(i)))
				{
					//System.out.println("Laser#: " +j);
					
					//System.out.println("Asteroid position X: " +asteroids.get(i).x + " Y: "+ asteroids.get(i).y);
					
					collisionThisTurn = true;
					
					remove(asteroids.get(i));
					
					remove(lasers.get(j));
					
					//System.out.println("Should destroy");
					
					size--;
					
					laserLength --;
					
					i--;
					
					break;
				}
			}
			
			//System.out.println("size = "+ size+ " i = "+i);
			
			//System.out.println(" real size = "+ asteroids.size()+ " i = "+i);
			
			if(i != size-1 && didCollide(asteroids.get(i), asteroids.get(i+1)) && isOnCollidingPath(asteroids.get(i), asteroids.get(i+1)))
			{
				//System.out.println("collided");
				
				Asteroid.collideAsteroids(asteroids.get(i), asteroids.get(i+1));				
			}
			
			sideCollisionHandle(asteroids.get(i));
						
			//System.out.println(" after");
			
			size = asteroids.size();
		}
	}
	
	//for asteroids, to see if they need to bounce
	static boolean isOnCollidingPath(Asteroid one, Asteroid two)
	{
		int velXone = one.dx;
		
		int velXtwo = two.dx;	
		
		return velXone > 0 && velXtwo < 0;
	}
	
	void sideCollisionHandle(Asteroid collider)
	{
		if(collider.x <=0)
		{
			collider.dx = Math.abs(collider.dx) ;
		}
		
		else if(collider.x + ASTEROID_COLLIDE_RADIUS >= SCREEN_SIZE)
		{
			collider.dx = -Math.abs(collider.dx) ;
		}
		
		if(collider.y > SCREEN_SIZE || collider.y < 0)
		{
			remove(collider);
		}
	}
	
	boolean sideCollisionHandle(Laser collider)
	{
		//System.out.println(collider.y);
		
		if(collider.y > SCREEN_SIZE || collider.y < 0)
		{
			//System.out.println("remove");
			
			remove(collider);
			
			return true;
		}
		
		return false;
	}
	
	//laser is >= x value of the asteroid
	static boolean laserHitAsteroid(Laser laser, Asteroid asteroid)
	{		
		int yDistance = laser.y-asteroid.y, xDistance = laser.x -asteroid.x;
		
		return Math.abs(yDistance) <= SIZE_OF_ASTEROID && xDistance <= SIZE_OF_ASTEROID/2 && xDistance > - SIZE_OF_ASTEROID/2;
	}
	
	static boolean didCollide(GameObject one, GameObject two)
	{
		int distancex = Math.abs(one.x-two.x);
		
		int distancey = Math.abs(one.y-two.y);
		
		int collideDistance = one.collideRadius/2 + two.collideRadius/2;
		
		if(distancex < collideDistance )// && distancey < collideDistance)
		{
			if(Math.sqrt(distancex*distancex + distancey*distancey) <= collideDistance)
			{				
				return true;
			}
		}
		
		return false;
	}
	
	void handlePlayer()
	{
		int i,size = asteroids.size();
		
		if(size==1)
		{
			return;
		}
	 	Asteroid asteroidArray[] = new Asteroid[size];
				
		asteroidArray = asteroids.toArray(asteroidArray);
		
		for(i=0;i<size;i++)
		{
			if(didCollide(asteroids.get(i), SgtPepper))
			{
				//System.out.println("Ship is destroyed asteroid index : "+ i);
				
				SgtPepper.collideAction();		
			}	
		}
	}
	
	public void paintObjects(Graphics g)
	{
		//System.out.println("start painting objects");
		
		int i, numAsteroids = this.asteroids.size(), numLasers = this.lasers.size();
		
		
		for(i=0;i<numAsteroids;i++)
		{
			this.asteroids.get(i).drawObject(g);
		}
		
		for(i=0;i<numLasers;i++)
		{
			this.lasers.get(i).drawObject(g);
		}
		
		this.SgtPepper.drawObject(g);
	} 
}
