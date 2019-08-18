

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test extends JFrame{
	public Test(){
		this.setSize(500, 400);
		
	}
	public static void main(String[] args) {
		Test t = new Test();
		T t1 = new T();
		t.add(t1);
		t.setVisible(true);
	}
}
class T extends JPanel{
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.rotate(0.5, 50, 50);
		g2d.drawLine(20, 20, 100, 100);
	}
}
