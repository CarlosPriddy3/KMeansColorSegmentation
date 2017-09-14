package GUI;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class ImageComponent extends JComponent
{
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private Graphics graphics;
	
	public ImageComponent(BufferedImage image)
	{
		this.image=image;
	}
	
	public void paintComponent(Graphics g)
	{
		if(image==null)return;
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		graphics=g;
		graphics.drawImage(image, 0, 0, this);
	}
	
	public void resetImage(BufferedImage image)
	{
		this.image=image;
	}
	
	public void draw()
	{
		graphics.drawImage(image, 0, 0, this);
	}
}