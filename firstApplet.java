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
import javax.swing.JPanel;

import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;
import sun.tools.jar.Main;


public class firstApplet extends JApplet implements Constants
{
	ActionPanel mainPanel;
	
	boolean needUpdating;
	
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
		mainPanel.start();
		
		//music();
	}

	public void music()
	{
		
		  new Thread(new Runnable() {
			  // The wrapper thread is unnecessary, unless it blocks on the
			  // Clip finishing; see comments.
			    public void run() {
			      try {
			        Clip clip = AudioSystem.getClip();
			        AudioInputStream inputStream = AudioSystem.getAudioInputStream(
			        Main.class.getResourceAsStream("C://Users//Chris//Documents//GitHub//applet//music.wav"));
			  	  clip.open(inputStream);
			        clip.start(); 
			      } catch (Exception e) {
			        System.err.println(e.getMessage());
			      }
			    }
			  }).start();
	}
}




class ActionPanel extends JPanel implements KeyListener, Runnable
{
	
	final int MOVEAMOUNT = 3;
	
	final int PAINT_WIDTH = 500;
	
	final int PAINT_HEIGHT = 500;
	
	volatile boolean needUpdating = true;
	
	AllThings gameItems;
	
	int dx,dy;
	
	Thread runner;
	
	public void init()
	{

		gameItems = new AllThings();
		
		repaint();
		
		this.setFocusable(true);
		
		this.requestFocusInWindow();
		
		this.setVisible(true);
				
		dx=0;
		
		dy=0;
	}
	
	public void paint(Graphics g)
	{
		
		//System.out.println("paint");
		
		g.setColor(Color.black);
		
	    g.fillRect(0, 0, PAINT_WIDTH, PAINT_HEIGHT);
		
		g.setColor(Color.BLUE);
		
		gameItems.paintObjects(g);
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
		// TODO Auto-generated method stub
		
		needUpdating = true;
		
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
	public void keyTyped(KeyEvent e) {

		
	}
	
	public void processMovement()
	{
		gameItems.update(dx,dy);
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
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
				processMovement();				
				
				repaint();				

				//System.out.println("Should paint");
			
		}
	}
}


abstract class GameObject implements Comparable<GameObject>
{
	int x,y,dx,dy;
	
	int collideRadius;
	
	BufferedImage picture;
	
	public GameObject(int x,int y, int collideRadius, String imageLocation)
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
	}	
	
	public abstract void collideAction(GameObject other);
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
	
	@Override
	public int compareTo(GameObject other) 
	{
		return this.x-other.x;
	}
}

class MainGuy extends GameObject
{		
	public MainGuy(int x, int y, int collide) 
	{
		super(x, y, collide, "C://Users//Chris//Downloads//asteroidSmall.png");
		// TODO Auto-generated constructor stub
	}

	public void collideAction(GameObject other) 
	{
		System.exit(0);		
	}
}

class Asteroid extends GameObject
{
	
	
	public Asteroid(int x, int y, int collide, int velX, int velY) 
	{
		super(x, y, collide,"C://Users//Chris//Downloads//" +
				"red_dot.png");
		
		this.dx=velX;
		this.dy=velY;
		
		
	}
		
	//Precondition: one.x <= two.x, will not collide along y
	static void collideAsteroids(Asteroid one, Asteroid two)
	{
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

	@Override
	public void collideAction(GameObject other) {
		// TODO Auto-generated method stub
		
	}

}


interface Constants
{
	final static int ASTEROID_COLLIDE_RADIUS = 50;
	
	final static int SCREEN_SIZE=500;
}

class AllThings implements Constants
{	
	ArrayList <Asteroid> asteroids = new ArrayList<Asteroid>();
	
	MainGuy SgtPepper;
	
	AllThings()
	{
		addAsteroid(0, 0, 5,10, 5);
		
		addAsteroid(500, 0, 5, -10, 5);	
		
		addAsteroid(200, 0, 5, -10, 5);	
		
		addAsteroid(300, 0, 5, 10, 5);	
		
		SgtPepper = new MainGuy(250, 0, 5);
	}

	void addAsteroid(int x,int y, int radius, int velx, int vely)
	{
		Asteroid asteroidToAdd = new Asteroid(x, y, radius,velx, vely);
		
		asteroids.add(asteroidToAdd);
	}
	
	void update(int dx, int dy)
	{
		keepAddingAsteroids();
		//updates based on dx and dy (player's speed)
		
		
		SgtPepper.dx = dx;
		
		SgtPepper.dy=dy;		
		
		Collections.sort(asteroids);
		
		collisionHandle();			

		moveAll();
	}
	
	void keepAddingAsteroids()
	{
		Random rand = new Random();
		
		int randomX,randomVX, randomVY;
		
		randomX=rand.nextInt()%500;

		randomVX=rand.nextInt()%5;
		
		randomVY=rand.nextInt()%2+5;
		
		if(randomVY<0)
		{
			System.out.println(randomVY);
		}
		
		if(rand.nextInt()%5==1)
		{
			addAsteroid(randomX, 0, ASTEROID_COLLIDE_RADIUS, randomVX, randomVY);	
		}
	}
	
	void moveAll()
	{
		SgtPepper.move();
		
		int i, numAsteroids;
		
		numAsteroids = asteroids.size();
		
		for(i=0;i<numAsteroids;i++)
		{
			asteroids.get(i).move();
		}
	}
	
	void destroyAsteroid(GameObject kill)
	{
		asteroids.remove(kill);
	}
	
	void collisionHandle()
	{
		handleAsteroids();
		
		handlePlayer();
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
		Asteroid asteroidArray[] = new Asteroid[size];
				
		asteroidArray = asteroids.toArray(asteroidArray);
		
		for(i=0;i<size-1;i++)
		{
			if(didCollide(asteroidArray[i], asteroidArray[i+1]) && isOnCollidingPath(asteroidArray[i], asteroidArray[i+1]))
			{
				//System.out.println("collided");
				
				Asteroid.collideAsteroids(asteroids.get(i), asteroids.get(i+1));				
			}
			
			sideCollisionHandle(asteroids.get(i));
		}
		
		sideCollisionHandle(asteroids.get(i));
	}
	
	//for asteroids, to see if they need to bounce
	static boolean isOnCollidingPath(Asteroid one, Asteroid two)
	{
		int velXone = one.dx;
		
		int velXtwo = two.dx;	
		
		return velXone > 0 && velXtwo < 0;
	}
	
	static void sideCollisionHandle(GameObject collider)
	{
		if(collider.x <=0)
		{
			collider.dx = Math.abs(collider.dx) ;
		}
		
		else if(collider.x + ASTEROID_COLLIDE_RADIUS >= SCREEN_SIZE)
		{
			collider.dx = -Math.abs(collider.dx) ;
		}
	}
	
	static boolean didCollide(GameObject one, GameObject two)
	{
		int distancex = Math.abs(one.x-two.x);
		
		int distancey = Math.abs(one.y-two.y);
		
		if(distancex < ASTEROID_COLLIDE_RADIUS)
		{
			if(distancex*distancex + distancey*distancey <= 4*ASTEROID_COLLIDE_RADIUS*ASTEROID_COLLIDE_RADIUS)
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
			if(didCollide(asteroidArray[i], SgtPepper))
			{
				//System.out.println("collided with player");							
			}			
		}
	}
	
	public void paintObjects(Graphics g)
	{
		//System.out.println("start painting objects");
		
		int i, numItems = this.asteroids.size();
		
		for(i=0;i<numItems;i++)
		{
			this.asteroids.get(i).drawObject(g);
		}
		
		this.SgtPepper.drawObject(g);
	} 
}

