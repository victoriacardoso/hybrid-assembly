package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CustomPanel extends JPanel {
	int progress = 0;

	public void addProgress(int value) {
		this.progress += value;
		this.repaint();
	}

	public void paint(Graphics g) {
		super.paint(g);
		this.setLayout(null);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(this.getWidth() / 2, this.getHeight() / 2);
		g2.rotate(Math.toRadians(270));
		Arc2D.Float arc = new Arc2D.Float(Arc2D.PIE);
		Ellipse2D cicle = new Ellipse2D.Float(0, 0, 30, 30);
		arc.setFrameFromCenter(new Point(0, 0), new Point(40, 40));
		cicle.setFrameFromCenter(new Point(0, 0), new Point(30, 30));
		arc.setAngleStart(1);
		arc.setAngleExtent(progress * 3.6);
		g2.setColor(Color.black);
		g2.draw(arc);
		g2.fill(arc);
		g2.setColor(Color.WHITE);
		g2.draw(cicle);
		g2.fill(cicle);
		g2.setColor(Color.black);
		g2.rotate(Math.toRadians(90));
		g2.setFont(new Font("Verdana", Font.PLAIN, 18));
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D r = fm.getStringBounds(progress + "%", g);
		int x = (0 - (int) r.getWidth()) / 2;
		int y = (0 - (int) r.getHeight()) / 2 + fm.getAscent();
		g2.drawString(progress + "%", x, y);
	}
}
