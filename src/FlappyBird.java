
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
/**
 * 飞扬的小鸟
 * @author 张兰
 * @version 1.0
 *
 */
public class FlappyBird { 
	public static void main(String[] args)throws Exception {
		JFrame frame = new JFrame("飞扬的小鸟");
		World world = new World();
		frame.add(world);
		frame.setVisible(true);//设置可见性
		frame.setSize(432,644+30);//设置大小
		//快速提示: alt + /
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置默认关闭操作
		frame.setLocationRelativeTo(null);//设置显示位置
		frame.setResizable(false);//设置是否可改变大小
		
		world.action();
	}
}

/**
 * 游戏界面(鸟活动的"世界")
 * @author 张兰
 *
 */
class World extends JPanel{
	//快速导包: ctrl + shift + o
	BufferedImage image;// 保存图片
	Ground ground;
	Column column1;
	Column column2;
	Bird bird;
	boolean gameOver;//标识游戏是否结束
	int score; //计分
	BufferedImage startImage;
	BufferedImage gameOverImage;
	boolean started;//标识游戏是否开始
	
	
	public World()throws Exception{
		image = ImageIO.read(getClass().getResource("bg.png"));
		startImage = ImageIO.read(getClass().getResource("start.png"));
		gameOverImage = ImageIO.read(getClass().getResource("gameover.png"));
		start();
	}
	
	public void start() throws Exception{
		ground = new Ground();
		column1 = new Column(1);
		column2 = new Column(2);
		bird = new Bird();
		gameOver = false;
		score = 0;
		started = false;
	}
	
	//绘制方法(Graphics 画笔)
	public void paint(Graphics g){
		g.drawImage(image,0,0,null);
		g.drawImage(column1.image,column1.x-column1.width/2,column1.y-column1.height/2,null);
		g.drawImage(column2.image,column2.x-column2.width/2,column2.y-column2.height/2,null);
		g.drawImage(ground.image,ground.x,ground.y,null);
		
		Font font = new Font(Font.SANS_SERIF,Font.BOLD,20);//定义字体
		g.setFont(font);//设置字体
		g.setColor(Color.BLACK);//设置颜色
		g.setColor(Color.WHITE);
		g.drawString("分数: "+score,15,30);//绘制分数
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.rotate(-bird.alpha,bird.x,bird.y);//旋转坐标系
		g.drawImage(bird.image,bird.x-bird.width/2,bird.y-bird.height/2,null);
		g2d.rotate(bird.alpha,bird.x,bird.y);
		
		if(gameOver){//游戏结束
			g.drawImage(gameOverImage,0,0,null);
		}
		
		if(!started){//游戏还未开始
			g.drawImage(startImage,0,0,null);
		}
		
	}
	
	//一切准备就绪，开始...
	public void action() throws Exception{
		//内部类
		//定义鼠标监听器
		MouseListener l = new MouseAdapter(){
			//快速提示快捷键: alt + /
			@Override
			public void mousePressed(MouseEvent e) {
				if(gameOver){
					try {
						start();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}else{
					bird.flappy();
					started = true;					
				}
			}
		};
		
		addMouseListener(l);//绑定鼠标监听器
		
		//定义键盘监听器
		KeyListener kl = new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				if(code == KeyEvent.VK_SPACE){
					bird.flappy();
				}
			}
		};
		addKeyListener(kl);//绑定键盘监听器
		requestFocus(); //自动获取焦点
		
		while(true){
			if(!gameOver){
				if(started){
					ground.step();
					column1.step();
					column2.step();
					bird.step();
					bird.fly();	
				}
				if(bird.x == column1.x || bird.x == column2.x){//鸟穿过了柱子
					score ++;
				}
			}
			
			if(bird.hit(ground)|| bird.hit(column1)|| bird.hit(column2)){
				gameOver = true;//游戏结束
			}
		
			repaint();// 尽快重新调用repaint();
			Thread.sleep(1000/100);//每隔1秒钟切换10帧
		}
	}
}

/**
 * 地面
 * @author 张兰
 *
 */
class Ground{
	int x;
	int y;
	BufferedImage image;
	
	public Ground() throws Exception{
		image = ImageIO.read(getClass().getResource("ground.png"));
		x = 0;
		y = 500;
	}
	
	//地面移动
	public void step(){
		x--;
		if(x <= -110){
			x = 0;
		}
	}
}

/**
 * 柱子
 * @author 张兰
 */
class Column{
	int x;//中心点横坐标
	int y;//中心点纵坐标
	int width;
	int height;
	BufferedImage image;
	int gap = 144; //柱子之间的间隙
	int distance = 245;//上一根柱子与下一根柱子之间的间距
	Random random = new Random();//随机数发生器
	
	//num 表示柱子编号:(1号柱子,2号柱子...)
	public Column(int num) throws Exception{
		image = ImageIO.read(getClass().getResource("column.png"));
		width = image.getWidth();
		height = image.getHeight();
		x = (num-1)*distance + 550;
		y = random.nextInt(218)+132;//[132,349]
	}
	
	public void step(){
		x--;
		if(x <= -width/2){
			x = 2 * distance - width/2;
			y = random.nextInt(218) + 132;
		}
	}
}

/**
 * 鸟
 *
 */
class Bird{
	int x;//中心点X坐标
	int y;//中心点Y坐标
	int width;
	int height;
	BufferedImage image;//保存当前的图片
	int size;//大小
	double g = 4;//重力加速度
	double t = 0.25;//经过的时间
	double s; // 经过时间t秒后运动的位移
	double speed = 20;//速度(初始速度/下一个速度)
	double alpha; //倾角
	BufferedImage[] images;//保存鸟运行状态的不同图片(8)
	int index; //下标: 便于提取图片的位置
	
	public Bird() throws Exception{
		images = new BufferedImage[8];
		for(int i=0; i<images.length; i++){
			//代码移动快捷键: alt + ↑(↓)
			images[i] = ImageIO.read(getClass().getResource(i+".png"));
			
		}
		image = images[0];
		index = 0;
		width = image.getWidth();
		height = image.getHeight();
		x = 132;
		y = 280;
		size = 40;
		alpha = 0;
	}

	//鸟的移动(抛物线的运动=上抛+自由落体)
	public void step(){
		System.out.println(speed+"before");
		double v0 = speed;// 初始速度
		s = v0*t - 0.5*g*t*t; //计算上抛运动位移
		double vt = v0 - g*t; //计算下次的速度
		speed = vt;
		y = y - (int)s; //计算鸟的Y坐标
		alpha = Math.atan(s/8); //反正切函数，计算倾角
		System.out.println(speed+"after");
	}
	
	//鸟的翅膀扇动
	public void fly(){
		index++;
		image = images[(index/8)%images.length];
		//index:  0 1 2 3 4 5 6 7 8 9 10 11 12 13 .. 16 17
		//images: 0 1 2 3 4 5 6 7 0 1 2  3  4  5  .. 0  1
		//images: 0 0...        0 1 1 ...
	}
	
	public void flappy(){
		speed = 20;
	}
	
	//判断撞击地面
	public boolean hit(Ground ground){
		boolean hit = (y + size/2 >= ground.y);
		if(hit){
			y = ground.y-size/2;
			alpha = -3.14/2;
		}
		return hit;
	}
	
	//判断撞击柱子
	public boolean hit(Column column){
		int x1 = column.x - column.width/2 - size/2;
		int x2 = column.x + column.width/2 + size/2;
		int y1 = column.y - column.gap/2 + size/2;
		int y2 = column.y + column.gap/2 - size/2;
		
		if(x > x1 && x < x2){//撞到了柱子
			if(y > y1 && y < y2){//除去柱子间隙
				return false;
			}
			return true;
		}
		return false;
	}
}

































