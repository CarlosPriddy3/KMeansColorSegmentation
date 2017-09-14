
package GUI;
//jerry priddy
//ece309 section 001
//lab 3 - gui chat client

import java.net.Socket;
import CoreProgram.MainClass;
import java.util.List;
import java.util.Vector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class KMeansGUI implements Runnable, FocusListener, ActionListener, ListSelectionListener, javax.swing.event.ChangeListener
{
	String[] colorspaceModeList = {"RGB","LAB"};
	String[] distanceModeList = {"Euclidean", "Neighborhood", "Mahalanobis", "Mahalanobis-NEI", "MAH-NEI-EUC"};
	String[] picNames = {""};
	Vector<ImageIcon> pictureVector = new Vector<ImageIcon>();
	BufferedImage guiImage;
	
	double specificity = .00005;
	double base = 5;
	int power = -3,
		k,
		picSelection = -1,
		CURTYPE = 1;

	BufferedImage myImage;
	ImageFrame myOutputWindow;
	MainClass colorSeg;
	
	Boolean 
	debug = false,
	running = false;
	
	File 
	workingDirectory = new File(System.getProperty("user.dir"));
	
	JList<ImageIcon> 
	myPicList = new JList<ImageIcon>();
	
	//ComboBoxModel<String> picNames;
	
	JFrame 	
	mainWindow		= new JFrame("KMeans Image Processing GUI"),
	menuWindow		= new JFrame("Options"),
	myPicWindow		= new JFrame("My Pictures: "+workingDirectory);
	
	JButton	
	clearPicSelection		= new JButton("Clear Selection"),
	processButton			= new JButton("Process Image"),
	previewPicturesButton	= new JButton("Preview Pictures"),
	stopButton				= new JButton("STOP");
	
	JTextArea	
	messageArea	= new JTextArea(5,50); //where messages are displayed
	
	JSlider
	specificityPower   	= new JSlider(),
	specificityBase   	= new JSlider(),
	kSlider				= new JSlider();
	
	JPanel
	leftPanel		= new JPanel(),
	upperleftPanel	= new JPanel(),
	lowerleftPanel	= new JPanel(),
	toolPanel		= new JPanel(),
	messagePanel	= new JPanel(),
	rightPanel		= new JPanel(); 	
	
	JScrollPane	
	topPane		= new JScrollPane(),
	messagePane	= new JScrollPane(messageArea), //where messages show up
	picsPane	= new JScrollPane(myPicList);
				
	JSplitPane 
	splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPane, messagePane);
	
	JTextField	
	serverAddressTF	= new JTextField(12),
	serverPortTF	= new JTextField(12),
	chatNameTF		= new JTextField(12);
	
	JLabel	
	blank				= new JLabel(" "),
	kLabel				= new JLabel("4"),
	specificityLabel	= new JLabel(String.format("Specificity: %.11f", specificity)),
	baseLabel			= new JLabel("Base: 5.0"),
	powerLabel			= new JLabel("Power of ten: -5"),
	myPicWindowLabel	= new JLabel("Select a picture.");
	
	JComboBox<String> 
	colorspaceModes = new JComboBox<String>(colorspaceModeList),
	distanceModes	= new JComboBox<String>(distanceModeList),
	pictureList		= new JComboBox<String>(picNames);
	
	JMenuBar 
	menuBar = new JMenuBar();
	
	JMenu 
	pullDownMenu = new JMenu("Menu");
	
	JMenuItem 
	debugMode = new JMenuItem("Debug Mode"),
	verticalSplit	= new JMenuItem("vertical");
	
	String newLine  = System.lineSeparator();
	
	//------------------Loader-------------------------------------------
	public static void main(String[] args)
	{
	    if (args.length==0) new KMeansGUI(); //load constructor
	    else System.out.println("Too many arguments.");
	}

	//------------------Constructors-------------------------------------
	public KMeansGUI()
	{
		if(debug) sendMessage("ChatClient()");
		if(debug) sendMessage("Local directory is " + workingDirectory);
		
		//----------------------build main window----------------------
		
				/* Upper Panel */
		leftPanel.setLayout(new GridLayout(2,6));
	    upperleftPanel.setLayout(new GridLayout(1,6));// rows/cols
	    lowerleftPanel.setLayout(new GridLayout(1,6));// rows/cols
	    //fir
	    leftPanel.add(processButton);	// 1
	    leftPanel.add(blank);	// 2
	    leftPanel.add(blank);  	// 3
	    leftPanel.add(blank);	// 4
	    leftPanel.add(blank);   	// 5
	    leftPanel.add(previewPicturesButton);   	// 6
	    //second row
	    leftPanel.add(stopButton);   	// 1        
	    leftPanel.add(blank);	// 2
	    leftPanel.add(blank);	// 3
	    leftPanel.add(blank);   	// 4
	    leftPanel.add(blank);	// 5
	    leftPanel.add(blank);   // 6
	    
	    processButton.setForeground(Color.blue);
	     
				/* Tool Panel */	 
	    toolPanel.setLayout(new GridLayout(5,2));
	    toolPanel.add(kSlider);
	    toolPanel.add(kLabel);
	    toolPanel.add(pictureList);
	    toolPanel.add(specificityLabel);
	    toolPanel.add(specificityPower);
	    toolPanel.add(specificityBase);
	    toolPanel.add(powerLabel);
	    toolPanel.add(baseLabel);
	    toolPanel.add(colorspaceModes);
	    toolPanel.add(distanceModes);
	    
				/* Lower Panel */
	    messagePane.setPreferredSize(new Dimension(40,450));
	    rightPanel.setLayout(new GridLayout(1,2));	
	    rightPanel.add(toolPanel);
	    rightPanel.add(messagePane);
	    messagePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    //messagePane.setViewportView(messagePanel);
	    
	    		/* Drop down menu */
	    pullDownMenu.add(debugMode);
	    menuBar.add(pullDownMenu);
	    

	    		/* Main Window */
	    mainWindow.getContentPane().add(leftPanel, "North");
		mainWindow.getContentPane().setComponentZOrder(leftPanel, 0);
		mainWindow.getContentPane().add(rightPanel, "South");
		mainWindow.getContentPane().setComponentZOrder(rightPanel, 1);
		
		
		mainWindow.setJMenuBar(menuBar);

				/* Message area */
		sendMessage("Starting...");
		messageArea.setEditable(false);

	    		/* picture window */
	    myPicWindow.getContentPane().add(clearPicSelection, "North");
	    myPicWindow.getContentPane().add(picsPane, "Center");
	    myPicWindow.getContentPane().add(myPicWindowLabel, "South");
	    myPicWindowLabel.setForeground(Color.red);
	    myPicList.setSelectionMode(0);
	    
	    		/* Behaviors */
		debugMode.addActionListener(this); 
		verticalSplit.addActionListener(this);
	    processButton.addActionListener(this);
	    stopButton.addActionListener(this);
	    clearPicSelection.addActionListener(this);
	    previewPicturesButton.addActionListener(this);
	    myPicList.addListSelectionListener(this);
	    specificityPower.addChangeListener(this);
	    specificityBase.addChangeListener(this);
	    kSlider.addChangeListener(this);
	    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    specificityPower.setValue(70);
	    kSlider.setMinimum(2);
	    kSlider.setMaximum(20);
	    kSlider.setValue(4);
	    pictureList.addFocusListener(this);
	    k=4;
	    //messageArea.setFont(messageArea.getFont().deriveFont(90));
	    Font myFont = messageArea.getFont().deriveFont((float) 16);
	    messageArea.setFont(myFont);
	    
	    		/* Window sizes and positions */
	    mainWindow.setSize(800,600);
	    myPicWindow.setSize(250,500);
	    myPicWindow.setLocation(1030,0);
	    
	    //--------------finalization------------------
		mainWindow.setVisible(true);
		findFiles();
		sendMessage("Working directory is " + workingDirectory);
		sendMessage("Ready!");
	}

	//------------------Event Listener-----------------------------------
	public void actionPerformed(ActionEvent ae)
	{
		if(debug) sendMessage("actionPerformed()");
		if(ae.getSource()==stopButton&&running)
		{
			colorSeg.stop=true;
		}
		if(ae.getSource()==processButton && running==false) process();
		if(ae.getSource()==debugMode) debug=!debug;

		//I chose to toggle instead of simply "turn on" the windows here to make the button behavior a bit more intuitive
		if(ae.getSource()==previewPicturesButton)
		{
			if(picSelection==-1)
				sendMessage("No image selected!");
			else
			{
				setImage(pictureList.getItemAt(picSelection));
				sendImage(myImage,"Previewing "+pictureList.getItemAt(picSelection));
			}
		}
		if(ae.getSource()==clearPicSelection)
		{
			myPicList.clearSelection();
			myPicWindowLabel.setText("No picture selected.");
		}
	}

	//------------------Process Button--------------------------------------
	//here's where we'll call up the image processor
	private byte process()
	{
		BufferedImage inputImage;
		
		byte 	processingError=0,
				parseError=0x01,
				noImageError=0x02,
				genericError=0x04;

		int colorSpaceSelection;
		int distanceSelection;
		
		if(debug) sendMessage("process()");
		boolean imageSelected = !myPicList.isSelectionEmpty();

		if(ParseInputs()!=0)
		{
			processingError+=parseError;
		}
		else
		{
			colorSpaceSelection=colorspaceModes.getSelectedIndex()+1;
			distanceSelection=distanceModes.getSelectedIndex()+1;
			
			if (picSelection == -1)
			{
				processingError+=noImageError;
				sendMessage("No picture was selected.");
			}
			else
			{
				setImage(pictureList.getItemAt(picSelection));
				if(debug) sendMessage("Trying to process image: "+pictureList.getItemAt(picSelection));
				//call processor here
				colorSeg = new MainClass(myImage, k, specificity, colorSpaceSelection, distanceSelection, this);
				running=true;
			}
		}

		return processingError;
	}

	private byte ParseInputs() 
	{
		byte parseError=0;
		
		return parseError;
	}

	@Override
	public void valueChanged(ListSelectionEvent lse) 
	{
		if (lse.getValueIsAdjusting()) return; //wait for selection resolution
		
		ImageIcon selectedPicture = myPicList.getSelectedValue();
		if (selectedPicture == null) return; // selection was removed!
		
		String pictureDescription = selectedPicture.getDescription();
		myPicWindowLabel.setText(pictureDescription);
	}
	
	public void sendMessage(String message)
	{
		messageArea.append(message+newLine);
		messageArea.moveCaretPosition(messageArea.getText().length());
	}
	
	public void setImage(String filename)
	{
		try {
			myImage = ImageIO.read(new File(filename));
		} catch (IOException e) {
			sendMessage("Found file but couldn't load it, it might be corrupt.");
		}
	}
	
	public BufferedImage getImage()
	{
		return myImage;
	}
	
	public void sendImage(BufferedImage image)
	{
		if(debug)sendMessage("Displaying image");
		ImageFrame outputWindow = new ImageFrame(image);
		myOutputWindow=outputWindow;
	}
	
	public void sendImage(BufferedImage image, String caption)
	{
		if(debug)sendMessage("Displaying image");
		ImageFrame outputWindow = new ImageFrame(image,caption);
		myOutputWindow=outputWindow;
	}
	
	public void updateImage(BufferedImage image)
	{
		if(myOutputWindow!=null)
		{
			myOutputWindow.resetImage(image);
		}
	}
	public void updateImage(BufferedImage image, String caption)
	{
		if(myOutputWindow!=null)
		{
			myOutputWindow.resetImage(image, caption);
		}
	}

	@Override
	public void run() 
	{
	}

	@Override
	public void stateChanged(ChangeEvent e) 
	{
		if(e.getSource()==specificityPower)
		{
			power=(specificityPower.getValue()-100)/10;
			powerLabel.setText("Power: "+Integer.toString(power));
		}
		if(e.getSource()==specificityBase)
		{
			base=(double)specificityBase.getValue()/10;
			baseLabel.setText("Base: "+Double.toString(base));
		}
		if(e.getSource()==kSlider)
		{
			k=kSlider.getValue();
			kLabel.setText("k = "+Integer.toString(k));
		}
		specificity=base*Math.pow(10, power);
		//specificityLabel.setText(String.valueOf(specificity));
		specificityLabel.setText(String.format("Specificity: %.11f", specificity));
		
	}
	
	public void sendStop()
	{
		running=false;
	}

	@Override
	public void focusGained(FocusEvent e) 
	{
		if(e.getSource()==pictureList)
		{
			findFiles();
		}
	}

	@Override
	public void focusLost(FocusEvent e) 
	{
		picSelection = pictureList.getSelectedIndex();
	}
	
	public void findFiles()
	{
		pictureList.removeAllItems();
		String[] filesFound = workingDirectory.list(); //where we store all files found
		int fileCount = 0;
		
		if (filesFound.length==0) //don't try to process an empty list
		{
			if(debug) sendMessage("No pictures were found in the local directory.");
			return;
		}
		
		for(String file:filesFound) //run through all files found
		{
			if(file.endsWith(".png")||file.endsWith(".jpg")||file.endsWith(".gif"))
			{
				pictureList.insertItemAt(file, fileCount);
				fileCount++;
			}
		}
		if(picSelection!=-1)
		{
			pictureList.setSelectedIndex(picSelection);
		}
			
		pictureList.repaint();
		toolPanel.validate();
	}
	
}
