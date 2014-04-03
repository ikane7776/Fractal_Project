import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

class ApplicationJ extends JFrame {
	ApplicationM mandelbrotCopy;
	double[] xArray;
	double[] yArray;
	int topIndex;
	private static final int H = 301; // Height of window
	private static final int W = 401; // Width of window
	private Color previousColour = new Color(00, 00, 00);
	private Color currentColour = new Color(00, 00, 00);
	private double firstChangex = 0;
	private double x;
	private double y;
	private double minXDefault = -2;
	private double maxXDefault = 2;
	private double minYDefault = -1.5;
	private double maxYDefault = 1.5;
	private double minX = minXDefault;
	private double maxX = maxXDefault;
	private double minY = minYDefault;
	private double maxY = maxYDefault;
	int maxIterations = 100;
	private int xMargin = 150;
	private int yMargin = 80;
	private int topOffset = 93;
	private int rightMargin = 30;
	private int yAxisGap = 8;
	private int xAxisGap = 8;
	private double imC = 0;
	private double reC = 0;
	private int index = 0;
	private Transaction theAL = new Transaction();
	boolean animationStart = false;
	int delay = 120;
	boolean forward = true;
	private BufferedImage theAI;
	private Graphics2D theAG;
	private BufferedImage theAIToSave;
	private Graphics2D theAGToSave;
	private Color[] juliaColours = {new Color(36, 159, 120), new Color(0,50,150), new Color(36, 159, 120),
			                    new Color(80, 180, 80), new Color(140, 50, 180), 
			                    new Color(130, 50, 50), new Color(0, 160, 160), 
			                    new Color(190, 160, 160), new Color(0, 255, 255), 
			                    new Color(125, 220, 140), new Color(190,220,100),
			                    new Color(230,230,50)};
	private int[] juliaCutOffs = {2,3,4,5,7,9,11,14, 17, 25, 50, 58};  
	
	private Iterator iterator;
	private AxisWiz axisWiz;
	
	private int mouseX;
	private int mouseY;
	
	private JMenuBar menubar = new JMenuBar();
	private JMenu file;
	private JMenu zoom;
	private JMenu axes;
	private JMenuItem save;
	private JMenuItem exit;
	private JMenuItem setRanges;
	private JMenuItem normal;
	private JMenuItem show;
	private JMenuItem hide;
	private JLabel cValue;
	private boolean showAxis = false;
	private JLabel[] axisLabel = {new JLabel(String.valueOf(maxY)), new JLabel(String.valueOf((maxY + minY)/2)), 
			                      new JLabel(String.valueOf(minY)), new JLabel(String.valueOf(minX)),
			                      new JLabel(String.valueOf((maxX + minX)/2)), new JLabel(String.valueOf(maxX))};  
	private int fileCounter = 1;
	String outputC;
	private boolean zoomSelect = false;
	private int mouseXZoom;
	private int mouseYZoom;
	private double xZoom;
	private double yZoom;
	private int xDrag;
	private int yDrag;
	private boolean dragging = false;
	private double xPressed;
	private double yPressed;
	JOptionPane frame = new JOptionPane();
	JButton c = new JButton("Forward");
	JButton d = new JButton("Back");
	

	public ApplicationJ(ApplicationM mandelbrot) {
		
		mandelbrotCopy = mandelbrot;		
		setLocation(1080, 600);
		setSize(W + xMargin + rightMargin, H + yMargin + topOffset ); // Size of drawing area
	    setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Animate animation = new Animate();
		Container cp = getContentPane();
		cp.setLayout(null);
		
		
		addMouseListener(new MouseAdapter() 		
	    {
	        public void mousePressed (MouseEvent e)
	        {	    
	           
	               mouseX = e.getX();
	               mouseY = e.getY();
	               if (mouseInRange(mouseX, mouseY))
	               {
	                   xPressed = ((minX)+((mouseX - xMargin)*((maxX - minX)/W)));
	                   yPressed = ((maxY)-((mouseY - topOffset)*((maxY - minY)/H)));
	               }
	               dragging = false;
	        }
	        public void mouseReleased (MouseEvent e)
	        {
	        	if(y - yZoom != 0 && x - xZoom != 0 && zoomSelect && mouseInRange(e.getX(), e.getY()))
	        	{
		            mouseXZoom = e.getX();
			        mouseYZoom = e.getY();
			        xZoom = (minX)+((mouseXZoom - xMargin)*((maxX - minX)/W));
			        yZoom = (maxY)-((mouseYZoom - topOffset)*((maxY - minY)/H));
			        
			        minX = Math.min(xPressed, xZoom);
			 		maxX = Math.max(xPressed, xZoom);
			 		minY = Math.min(yPressed, yZoom);
			 		maxY = Math.max(yPressed  , yZoom);
			 	
			 		axisWiz.setAxisLabels(axisLabel, maxY, (maxY + minY)/2, 
    		 				                 minY, minX, (maxX + minX)/2, maxX);
			 		background();
			 		repaint();
	        	}
			 	zoomSelect = false;
			 	dragging = false;   	
		    }
	    });
		
		addMouseMotionListener(new MouseMotionAdapter() 
	    {
	        public void mouseDragged (MouseEvent e)
	        {
	            	xDrag = e.getX();
	            	yDrag = e.getY();
	            	if (mouseInRange(xDrag, yDrag))
		        	{
	                   dragging = true;
	        	    }
	            	else
	            	{
	            		dragging = false;
	            	    zoomSelect = false;
	            	} 
	        }
	    });
		
		JButton a = new JButton("Start");
		a.setBounds(10, 1 * 40 + 15, 95, 30);
		a.addActionListener(theAL);
		cp.add(a);
		
		JButton b = new JButton("Stop");
		b.setBounds(10, 2 * 40 + 15, 95, 30);
		b.addActionListener(theAL);
		cp.add(b);
		

		c.setBounds(10, 3 * 40 + 15, 95, 30);
		c.addActionListener(theAL);
		c.setEnabled(false);
		cp.add(c);
		

		d.setBounds(10, 4 * 40 + 45, 95, 30);
		d.addActionListener(theAL);
		d.setEnabled(false);
		cp.add(d);
		
		JButton e = new JButton("Faster");
		e.setBounds(10, 5 * 40 + 45, 95, 30);
		e.addActionListener(theAL);
		cp.add(e);
		
		JButton f = new JButton("Slower");
		f.setBounds(10, 6 * 40 + 45, 95, 30);
		f.addActionListener(theAL);
		cp.add(f);
		
		cValue = new JLabel(" c = " + reC + " + " + imC + "i");
		cValue.setBounds(150,10,200,30);
		cValue.setFont(new Font("Serif", Font.PLAIN, 14));
		cp.add(cValue);

		iterator = new Iterator(maxIterations, juliaColours, juliaCutOffs);
		axisWiz = new AxisWiz(W, H);
		 
		axisWiz.placeAxisLables(this, axisLabel, xMargin, topOffset);
		
		axisWiz.setAxisLabels(axisLabel, maxY, (maxY + minY)/2, 
                 minY, minX, (maxX + minX)/2, maxX);
		
		 menubar = new JMenuBar();
    	 setJMenuBar(menubar);
    	 
    	 file = new JMenu("File");
    	 zoom = new JMenu("Zoom");
    	 axes = new JMenu("Axes");
    	 
    	 menubar.add(file);
    	 menubar.add(zoom);
    	 menubar.add(axes);
    	 
    	 save = new JMenuItem("Save");
    	 exit = new JMenuItem("Exit");
    	 setRanges = new JMenuItem("Select Area With Mouse");
    	 normal = new JMenuItem("Normal");
    	 show = new JMenuItem("Show");
    	 hide = new JMenuItem("Hide");
    	 
    	 file.add(save);
    	 file.add(exit);
    	 zoom.add(setRanges);
    	 zoom.add(normal);
    	 axes.add(show);
    	 axes.add(hide);
    	 
    	 exit.addActionListener(new ActionListener() {
 		 	public void actionPerformed(ActionEvent arg0) {
 		 			System.exit(0);
 		 	}
    	 });
    	 
    	 save.addActionListener(new ActionListener() {
  		 	public void actionPerformed(ActionEvent arg0) {
  		 		theAIToSave = (BufferedImage) createImage(W + 130, H + 100);
  		 		theAGToSave  = theAIToSave.createGraphics();
  		 		theAGToSave.drawImage(theAI, 80, 50, null);
  		 	    outputC = "C = " + String.valueOf(reC) + " + " + String.valueOf(imC) + "i";	
  		 	    theAGToSave.drawString(outputC, 10,  20); 		
  		 		if(showAxis) 
  	   		 	{
  	   		 		axisWiz.drawAxis(theAGToSave, 80, 50, 8, 8);
  	   		 	    theAGToSave.drawString(axisWiz.axisLabelNumber(maxY), 0, 53);
  	   		 	    theAGToSave.drawString(axisWiz.axisLabelNumber((minY + maxY)/2),0, 53 + H/2);
  	   		 	    theAGToSave.drawString(axisWiz.axisLabelNumber(maxY),0, 53 + H);
  	   		 	    theAGToSave.drawString(axisWiz.axisLabelNumber(minX), 47, 50 + H + 32);
  			 	    theAGToSave.drawString(axisWiz.axisLabelNumber((minX + maxX)/2), 47 + W/2, 50 + H + 32);
  			 	    theAGToSave.drawString(axisWiz.axisLabelNumber(maxX), 47 + W, 50 + H + 32);
  	   		 	}
  		 		 
  		 		try {
					ImageIO.write(theAIToSave, "png", new File("image" + fileCounter + ".png"));
					fileCounter++;
			    	} catch (IOException e) {
					    // TODO Auto-generated catch block
				    	e.printStackTrace();
			    	}
  		    	}
     	 });
    	 
    	 
    	 show.addActionListener(new ActionListener() {
  		 	public void actionPerformed(ActionEvent arg0) {
  		 		showAxis = true;
  		 		axisWiz.setVisibility(axisLabel, true);
  		 	    repaint();
  		 	}
     	 });
    	 
    	 hide.addActionListener(new ActionListener() {
   		 	public void actionPerformed(ActionEvent arg0) {
   		 	     showAxis = false;
   		 	     axisWiz.setVisibility(axisLabel, false);
   		 	     repaint();
   		 	}
      	 });  	  
    	 
    	 normal.addActionListener(new ActionListener() {
    		 	public void actionPerformed(ActionEvent arg0) {
    		 		minX = minXDefault;
    		 		maxX = maxXDefault;
    		 		minY = minYDefault;
    		 		maxY = maxYDefault;
    		 		axisWiz.setAxisLabels(axisLabel, maxY, (maxY + minY)/2, 
			                                 minY, minX, (maxX + minX)/2, maxX);
    		 		background();
    		 		repaint();
    		 	}
       	 }); 
    	 
    	 setRanges.addActionListener(new ActionListener() {
 		 	public void actionPerformed(ActionEvent arg0) {
 		 		zoomSelect = true; 
 		 	}
    	 });
    	 
 		 setVisible(true);
		
	} // end of constructor
	
	public void pathSet(double[] xArrayIn, double[] yArrayIn, int arrayMax )
	{
		xArray = xArrayIn;
		yArray = yArrayIn;
		topIndex = arrayMax;
		index = 0;
		System.out.println("New path set xArray length = " + xArray.length + "Yarray length = " + yArray.length);
	}
	
	class Transaction implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
		    String actionIs = ae.getActionCommand();
		    
		    if (actionIs.equals("Start"))
		    { 
		    	
		 //   	  topIndex = mandelbrotCopy.getTopArrayIndex();
		  //  	  xArray = mandelbrotCopy.getXArray();
		 //   	  yArray = mandelbrotCopy.getYArray();
		 //   	  if (index > topIndex) index = 0;
		    	  if(topIndex < 5)
		    	  {
		    		  JOptionPane.showMessageDialog(frame, "No Path Set, Please set path by dragging on the Mandelbrot set");
		    	  }
		    	  else
		    	  {
		    		  animationStart = true;	
			    	  mandelbrotCopy.applicationRunningTrue();
			    	  c.setEnabled(true);
			    	  d.setEnabled(true);
			    	  
		    	  }    	  
		    	 
		    	    
		    } 
		    if (actionIs.equals("Stop"))
		    {
			      animationStart = false;
			      mandelbrotCopy.applicationRunningFalse();
			      c.setEnabled(false);
		    	  d.setEnabled(false);
		    }
		    if (actionIs.equals("Forward"))
		    {
		    	  forward = true;
		    	  //animationStart = true;	
		    	  mandelbrotCopy.applicationRunningTrue();
		    }
		    if (actionIs.equals("Back"))
		    {
	    		  forward = false;
	    		  //animationStart = true;	
	    		  mandelbrotCopy.applicationRunningTrue();
		    }
		    if (actionIs.equals("Faster"))
		    {
	    		  if (delay > 30)
	    		  delay -= 10;
		    }
		    if (actionIs.equals("Slower"))  
	        {
	    		  delay += 10;
	        }	  
	    }
	}
	
	class Animate implements Runnable
	{
		
		private Thread theAnimation;
		public Animate()
		{
			theAnimation = new Thread(this);
			theAnimation.start();
		}
		
		public synchronized void setC()
		{	
			System.out.println("Length of xArray = " + xArray.length + "Length of YArray = " + yArray.length);
			System.out.println("Index is " + index);
			reC = xArray[index];// Array index out of bounds exception
			imC = yArray[index];
			if(forward)
			{
				index++;
			}else
			{
				index--;
			}
			if(index > topIndex) index = 0;
			
			if(index < 0) index = topIndex;
		}

		@Override   
		public void run() {
			while( true )
			{
				try
				{
					
					if (animationStart)
					{
	//					double re = Math.round(reC*1000000);
	//					double im = Math.round(imC*1000000);
	//					String outputC = "C = " + String.valueOf(re/1000000) + " + " + String.valueOf(im/1000000) + "i";	
						
						cValue.setText("C = " + ((double) Math.round( reC*1000000))/1000000 + " + " + ((double) Math.round( imC*1000000))/1000000 + "i");
						background();
						Thread.sleep(10);
						repaint(xMargin, topOffset, W, H);
						setC();		
						mandelbrotCopy.traceC(index);
					}
								
					Thread.sleep(delay);	
				}
				catch (InterruptedException e) {}
			}	
		}	
	}
	
/*	public void update(Graphics g)
	{
		reDraw((Graphics2D) g);
		System.out.println("update invoked");
		paint(g);
	}
*/
	public void paint(Graphics g) // When 'Window' is first
	{ // shown or damaged
		super.paint(g);
		if(! animationStart)
		{
			((Graphics2D) g).setPaint(Color.BLACK);
			((Graphics2D) g).fill( new Rectangle2D.Double( xMargin, topOffset, W, H ) );			
		}
		else
		{
			reDraw((Graphics2D) g);
		}
		if (showAxis)
		{
			axisWiz.drawAxis((Graphics2D) g, xMargin, topOffset, xAxisGap, yAxisGap);
	 	}	
		if (zoomSelect && dragging)
		{
			((Graphics2D) g).setPaint(Color.WHITE);
			g.drawRect(Math.min(mouseX, xDrag), Math.min(mouseY, yDrag), Math.abs(mouseX - xDrag), Math.abs(mouseY - yDrag)); 
		}
	}
	
	public void reDraw( Graphics2D g )
	{
		g.drawImage( theAI,  xMargin,  topOffset,  this);
	}

	public void reDrawJulia(Graphics2D g) // Re draw contents
	{
	    	for(double yPixel = 0; yPixel < H; yPixel++)
	    	{
		    	for(double xPixel = 0; xPixel < W; xPixel++)
		    	{
			    	x = (minX)+(xPixel*((maxX - minX)/W));
			    	y = (maxY)-(yPixel*((maxY - minY)/H));
			    	previousColour = currentColour;		
			    	currentColour = iterator.colourPicker(reC, imC, x, y);
				    if(! previousColour.equals(currentColour) || xPixel == W - 1)
				    {
				    	g.draw(new Line2D.Double(firstChangex, yPixel, xPixel, yPixel));
				    	firstChangex = xPixel % (W - 1);
				    	g.setPaint(currentColour);
				    }				
			    }
	      	}
		}

	public int juliaIteration(double x, double y, double a, double b, int maxIterations)
	{
		double temp = 0;
		int iterations = 0;
		while(a*a + b*b < 4 && iterations <= maxIterations)
		{
			temp = (a*a) - (b*b) + x;
			b =(2*a*b) + y;
			a = temp;
			iterations++;
		}
		return iterations;
	}
	
	public boolean mouseInRange(int mouseXp, int mouseYp)
	{
		return (mouseXp <= W + xMargin && mouseYp <= H + topOffset && mouseXp >= xMargin && mouseYp >= topOffset);
	}
	
	public void background()
	{
		theAI = (BufferedImage) createImage(W, H);
		theAG = theAI.createGraphics();	
		reDrawJulia(theAG);
	}
	
	public double getRe() {return reC;}
	public double getIm() {return imC;}
}