package yolanda;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Yolanda {
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setSize(432, 644+30);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		GameFrame gf = new GameFrame();
		f.add(gf);
		f.setVisible(true);
		
		gf.action();
	}
}

class GameFrame extends JPanel{
	Bird bird;
	boolean started;
	boolean gameOver;
	BufferedImage bg;
	BufferedImage startImage;
	BufferedImage gameOverImage;
	Ground ground;
	Column c1 ;
	Column c2;
	public GameFrame(){
		try {
			bg = ImageIO.read(getClass().getResource("bg.png"));
			startImage = ImageIO.read(getClass().getResource("start.png"));
			System.out.println(startImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		start();
		
	}
	public void start(){
		bird = new Bird();
		ground = new Ground();
		c1 = new Column(1);
		c2 = new Column(2);
		started = false;
		gameOver = false;
	}
	public void paint(Graphics g) {
		g.drawImage(bg, 0, 0, null);
		
		Graphics2D g1 = (Graphics2D) g;
		g1.rotate(-bird.alpha,bird.x,bird.y);
		g1.drawImage(bird.image, 132-bird.width/2, bird.y-bird.height/2, null);
		g1.rotate(bird.alpha,bird.x,bird.y);
		
		g.drawImage(c1.image, c1.x-c1.width/2, c1.y-c1.height/2, null);
		g.drawImage(c2.image, c2.x-c2.width/2, c2.y-c2.height/2, null);
		
		g.drawImage(ground.image, ground.x, ground.y, null);
		if(!started){
			System.out.println("画开始按钮");
			g.drawImage(startImage, 0, 0, null);
		}
		
		if(gameOver){
			g.drawImage(gameOverImage, 0, 0, null);
		}
		
	}
	
	public void action(){
		MouseAdapter l = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!gameOver){
					bird.flappy();
					started = true;
				}else{
					start();
				}
			}
		};
		addMouseListener(l);
		addMouseMotionListener(l);
		requestFocus();
		
		while(true){
			if(started){
				ground.step();
				c1.step();
				c2.step();
				bird.step();
				repaint();
				try {
					Thread.sleep(1000/100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}

class Ground{
	int x;
	int y;
	BufferedImage image;
	
	public Ground(){
		try {
			image = ImageIO.read(getClass().getResource("ground.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		x = 0;
		y = 500;
	}
	
	public void step(){
		x --;
		if(x<=-110){
			x = 0;
		}
	}
}

class Bird {
	BufferedImage image;
	BufferedImage[] images = new BufferedImage[8];
	int x;//中心点
	int y;//中心点
	int width;
	int height;
	double speed = 20;
	int g = 5;
	double t = 0.25;//运动时间
	double s;//t时间的位移
	int size;
	double alpha;
	int index;
	
	public Bird(){
		for(int i =0;i<8;i++){
			try {
				images[i] = ImageIO.read(getClass().getResource(i+".png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			image = images[0];
			width = image.getWidth();
			height = image.getHeight();
			x = 132;
			y = 240;
			
		}
		image = images[0];
		width = image.getWidth();
		height = image.getHeight();
	}
	
	public void flappy(){
		speed = 20;
	}
	
	public void step(){
		//每0.25秒动一下
		double v0 = speed;
		s= v0*t-g*t*t/2;
		speed = v0-g*t;
		y =  y -(int) s;
		alpha = Math.atan(s/8);
	}
	public void fly(){
		if(index == 8){
			index=0;
		}
		image = images[index++%8];
	}
}
class Column{
	int x;//柱子的中心点x
	int y;//柱子的中心点y
	BufferedImage image;//图片
	int width;
	int height;
	int distance=245;//两个柱子之间的距离
	int grap=144;//柱子之间的间隙
	
	public Column(int num){
		try {
			image = ImageIO.read(getClass().getResourceAsStream("column.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		width = image.getWidth();
		height = image.getHeight();
		Random r = new Random();
		x = (num-1)*distance+550;
		y = r.nextInt(218)+132;
	}
	
	public void step(){
		x--;
		if(x<-width/2){
			Random r = new Random();
			x = 490-width/2;
			y = r.nextInt(218)+132;
		}
	}
}
