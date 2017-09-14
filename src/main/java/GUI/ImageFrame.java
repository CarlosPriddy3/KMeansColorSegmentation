package GUI;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class ImageFrame extends JFrame
{
	public ImageComponent imageComponent;
	public ImageFrame(BufferedImage image, String caption)
	{
		setTitle(caption);
		setSize(image.getWidth(),image.getHeight());
		imageComponent = new ImageComponent(image);
		this.add(imageComponent);
		this.setVisible(true);
		this.setEnabled(true);
	}

	public ImageFrame(BufferedImage image)
	{
		setTitle("Output Image");
		setSize(image.getWidth(),image.getHeight());
		imageComponent = new ImageComponent(image);
		this.add(imageComponent);
		this.setVisible(true);
		this.setEnabled(true);
	}
	
	public void resetImage(BufferedImage image)
	{
		imageComponent.resetImage(image);
		imageComponent.draw();
		this.repaint();
	}
	public void resetImage(BufferedImage image, String caption)
	{
		this.setTitle(caption);
		imageComponent.resetImage(image);
		imageComponent.draw();
		this.repaint();
	}
}
