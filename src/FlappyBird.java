
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
 * �����С��
 * @author ����
 * @version 1.0
 *
 */
public class FlappyBird { 
	public static void main(String[] args)throws Exception {
		JFrame frame = new JFrame("�����С��");
		World world = new World();
		frame.add(world);
		frame.setVisible(true);//���ÿɼ���
		frame.setSize(432,644+30);//���ô�С
		//������ʾ: alt + /
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//����Ĭ�Ϲرղ���
		frame.setLocationRelativeTo(null);//������ʾλ��
		frame.setResizable(false);//�����Ƿ�ɸı��С
		
		world.action();
	}
}

/**
 * ��Ϸ����(����"����")
 * @author ����
 *
 */
class World extends JPanel{
	//���ٵ���: ctrl + shift + o
	BufferedImage image;// ����ͼƬ
	Ground ground;
	Column column1;
	Column column2;
	Bird bird;
	boolean gameOver;//��ʶ��Ϸ�Ƿ����
	int score; //�Ʒ�
	BufferedImage startImage;
	BufferedImage gameOverImage;
	boolean started;//��ʶ��Ϸ�Ƿ�ʼ
	
	
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
	
	//���Ʒ���(Graphics ����)
	public void paint(Graphics g){
		g.drawImage(image,0,0,null);
		g.drawImage(column1.image,column1.x-column1.width/2,column1.y-column1.height/2,null);
		g.drawImage(column2.image,column2.x-column2.width/2,column2.y-column2.height/2,null);
		g.drawImage(ground.image,ground.x,ground.y,null);
		
		Font font = new Font(Font.SANS_SERIF,Font.BOLD,20);//��������
		g.setFont(font);//��������
		g.setColor(Color.BLACK);//������ɫ
		g.setColor(Color.WHITE);
		g.drawString("����: "+score,15,30);//���Ʒ���
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.rotate(-bird.alpha,bird.x,bird.y);//��ת����ϵ
		g.drawImage(bird.image,bird.x-bird.width/2,bird.y-bird.height/2,null);
		g2d.rotate(bird.alpha,bird.x,bird.y);
		
		if(gameOver){//��Ϸ����
			g.drawImage(gameOverImage,0,0,null);
		}
		
		if(!started){//��Ϸ��δ��ʼ
			g.drawImage(startImage,0,0,null);
		}
		
	}
	
	//һ��׼����������ʼ...
	public void action() throws Exception{
		//�ڲ���
		//������������
		MouseListener l = new MouseAdapter(){
			//������ʾ��ݼ�: alt + /
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
		
		addMouseListener(l);//����������
		
		//������̼�����
		KeyListener kl = new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				if(code == KeyEvent.VK_SPACE){
					bird.flappy();
				}
			}
		};
		addKeyListener(kl);//�󶨼��̼�����
		requestFocus(); //�Զ���ȡ����
		
		while(true){
			if(!gameOver){
				if(started){
					ground.step();
					column1.step();
					column2.step();
					bird.step();
					bird.fly();	
				}
				if(bird.x == column1.x || bird.x == column2.x){//�񴩹�������
					score ++;
				}
			}
			
			if(bird.hit(ground)|| bird.hit(column1)|| bird.hit(column2)){
				gameOver = true;//��Ϸ����
			}
		
			repaint();// �������µ���repaint();
			Thread.sleep(1000/100);//ÿ��1�����л�10֡
		}
	}
}

/**
 * ����
 * @author ����
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
	
	//�����ƶ�
	public void step(){
		x--;
		if(x <= -110){
			x = 0;
		}
	}
}

/**
 * ����
 * @author ����
 */
class Column{
	int x;//���ĵ������
	int y;//���ĵ�������
	int width;
	int height;
	BufferedImage image;
	int gap = 144; //����֮��ļ�϶
	int distance = 245;//��һ����������һ������֮��ļ��
	Random random = new Random();//�����������
	
	//num ��ʾ���ӱ��:(1������,2������...)
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
 * ��
 *
 */
class Bird{
	int x;//���ĵ�X����
	int y;//���ĵ�Y����
	int width;
	int height;
	BufferedImage image;//���浱ǰ��ͼƬ
	int size;//��С
	double g = 4;//�������ٶ�
	double t = 0.25;//������ʱ��
	double s; // ����ʱ��t����˶���λ��
	double speed = 20;//�ٶ�(��ʼ�ٶ�/��һ���ٶ�)
	double alpha; //���
	BufferedImage[] images;//����������״̬�Ĳ�ͬͼƬ(8)
	int index; //�±�: ������ȡͼƬ��λ��
	
	public Bird() throws Exception{
		images = new BufferedImage[8];
		for(int i=0; i<images.length; i++){
			//�����ƶ���ݼ�: alt + ��(��)
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

	//����ƶ�(�����ߵ��˶�=����+��������)
	public void step(){
		System.out.println(speed+"before");
		double v0 = speed;// ��ʼ�ٶ�
		s = v0*t - 0.5*g*t*t; //���������˶�λ��
		double vt = v0 - g*t; //�����´ε��ٶ�
		speed = vt;
		y = y - (int)s; //�������Y����
		alpha = Math.atan(s/8); //�����к������������
		System.out.println(speed+"after");
	}
	
	//��ĳ���ȶ�
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
	
	//�ж�ײ������
	public boolean hit(Ground ground){
		boolean hit = (y + size/2 >= ground.y);
		if(hit){
			y = ground.y-size/2;
			alpha = -3.14/2;
		}
		return hit;
	}
	
	//�ж�ײ������
	public boolean hit(Column column){
		int x1 = column.x - column.width/2 - size/2;
		int x2 = column.x + column.width/2 + size/2;
		int y1 = column.y - column.gap/2 + size/2;
		int y2 = column.y + column.gap/2 - size/2;
		
		if(x > x1 && x < x2){//ײ��������
			if(y > y1 && y < y2){//��ȥ���Ӽ�϶
				return false;
			}
			return true;
		}
		return false;
	}
}

































