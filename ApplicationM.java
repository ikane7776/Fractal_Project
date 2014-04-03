import java.awt.AWTException;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.util.Date;
import java.awt.Color;
import java.awt.Font;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.io.File;
import java.io.IOException;
public class ApplicationM extends JFrame {
	private static final int H = 800; // Height of window
	private static final int W = 900; // Width of window
	private Color previousColour = new Color(00, 00, 00);  
	private Color currentColour = new Color(00, 00, 00);
	private Color backgroundColour = new Color(240, 240, 240);
	private double firstChangex = 0;
	private double x, y;
	private double minXDefault = -2.5;
	private double maxXDefault =  1.0;
	private double minYDefault = -1.5;
	private double maxYDefault = 1.5;			
	private double minX = minXDefault;
	private double maxX = maxXDefault;
	private double minY = minYDefault;  
	private double maxY = maxYDefault;
	private int maxIterations = 70;
	private int xMargin = 70;
	private int yMargin = 60;
	private int topOffset = 150;
	private int rightMargin = 30;
	private int mouseX, mouseY;
  	private final JTextField theRealOutput = new JTextField(); 
  	private final JTextField theImOutput = new JTextField(); 
  	private JLabel label1 = new JLabel(" x =");
  	private JLabel label2 = new JLabel(" y =");
  	private JLabel label3 = new JLabel(" c = x + iy");
  	private JLabel label4 = new JLabel("Drag mouse with button down to set path for the Julia set animation");
  	public double[] xArray, yArray;
	public int[] xPixelArray, yPixelArray; 
	public ArrayList<Double> xArrayList, yArrayList;
	public ArrayList<Integer> xPixelArrayList, yPixelArrayList; 
	public int topArrayIndex = 0;
	private boolean mouseDown =  false;
	private Color[] mandelbrotColours = {new Color(36, 59, 120), new Color(52, 90, 180), new Color(158, 27, 224), 
			                             new Color(45, 27, 156), new Color(0, 180, 180),new Color(146, 255, 100)};
    private int[] mandelbrotCutOffs = {1,7,13,19,30,33};  
    private BufferedImage theAI, theAIToSave;
	private Graphics2D theAG, theAGToSave;
	private boolean animationRunning = false;
	private int animationIndex, animationX, animationY;
	private boolean showAxis = false;
	private String outputC = "";
	private Iterator iterator;
	private JMenuBar menubar = new JMenuBar();
	private JMenu file, zoom, axes;
	private JMenuItem save, exit, setRanges, normal, show, hide;
	private AxisWiz axisWiz;
	private JLabel[] axisLabel = {new JLabel(String.valueOf(maxY)), new JLabel(String.valueOf((maxY + minY)/2)), 
            new JLabel(String.valueOf(minY)), new JLabel(String.valueOf(minX)),
            new JLabel(String.valueOf((maxX + minX)/2)), new JLabel(String.valueOf(maxX))};  
	private int xAxisGap = 8;
	private int yAxisGap = 8;
	boolean zoomSelect = false;
	private int mouseXZoom,mouseYZoom;
	private double xZoom, yZoom;
	private int xDrag, yDrag;
	private int xDragPrev = 0;
	private int yDragPrev = 0;
	private boolean dragging = false;
	private int dragCounter = 0;
	private double xPressed, yPressed;
	private ApplicationJ copyOfJulia;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public ApplicationM() {
		
		setSize(W + xMargin + rightMargin, H + yMargin + topOffset ); // Size of drawing area
		getContentPane().setBackground( backgroundColour );
		setDefaultCloseOperation(EXIT_ON_CLOSE);    
		setTitle("Mandelbrot set");
		setResizable(false); // Do not allow user to re-size because pixels represent mathematical points
		addMouseListener(new MouseAdapter() 			
	    {
	        public void mousePressed (MouseEvent e)
	        {	        		           	
	//           index = 0;
	           mouseDown = true;
	           mouseX = e.getX();
	           mouseY = e.getY();
	           if(!zoomSelect)
	           {
	        	   xArrayList = new ArrayList<Double>();
	        	   yArrayList = new ArrayList<Double>();
	        	   xPixelArrayList = new ArrayList<Integer>();
	        	   yPixelArrayList = new ArrayList<Integer>();
	        	   x = (minX)+((mouseX - xMargin)*((maxX - minX)/W));
		           y = (maxY)-((mouseY - topOffset)*((maxY - minY)/H));
			           
			        if(mouseInRange(mouseX, mouseY))
			        {	           
			        	double re = Math.round(x*1000000);
			   		    double im = Math.round(y*1000000);   
			   	        theRealOutput.setText(String.valueOf(re/1000000));
			   	        theImOutput.setText(String.valueOf(im/1000000)); 
		        	}			        
	           }
	           else
	           { 
	               if (mouseInRange(mouseX, mouseY))
	               {
	                   xPressed = ((minX)+((mouseX - xMargin)*((maxX - minX)/W)));
	                   yPressed = ((maxY)-((mouseY - topOffset)*((maxY - minY)/H)));
	               }
                   dragging = false;
	               dragCounter = 0;
	             {
	        } }};
	           
	        
	        public void mouseReleased (MouseEvent e)
	        {
	        	xArray = new double[xArrayList.size()];
        		yArray = new double[yArrayList.size()];
        		xPixelArray = new int[xPixelArrayList.size()];
        		yPixelArray = new int[yPixelArrayList.size()];

        		
        		for(int i = 0; i < xArrayList.size(); i++)
        		{
        			xArray[i] = xArrayList.get(i);
        			yArray[i] = yArrayList.get(i);
        			
        			xPixelArray[i] = xPixelArrayList.get(i);
        			yPixelArray[i] = yPixelArrayList.get(i);

        		}
        		copyOfJulia.pathSet(xArray,yArray,xArrayList.size() - 1);
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
			 		zoomSelect = false;
			 		axisWiz.setAxisLabels(axisLabel, maxY, (maxY + minY)/2, 
    		 				                 minY, minX, (maxX + minX)/2, maxX);
			 		background();
			 		repaint();
			 		for(int i = 0; i < topArrayIndex; i++)
			 		{
			 			System.out.println("pixel array being filled and i is " + i);
			 			xPixelArray[i] = (int) Math.round((xArray[i] - minX)*(W/(maxX - minX))) + xMargin; // EXCEPTION index out of bounds
			 			yPixelArray[i] = (int) Math.round((maxY - yArray[i])*(H/(maxY - minY))) + topOffset;	
			 			
			 		}					
	        	}
//	        	else if(!zoomSelect)
//	        	{		    
//	        		index = 0;
	        		
//	        	}
			 	dragging = false;   
			 	mouseDown = false;
			 	dragCounter = 0;
		    } 		    	    	
	    });
	        
		addMouseMotionListener(new MouseMotionAdapter() 
	    {
	        public void mouseDragged (MouseEvent e)
	        {
	        	if (zoomSelect)
	        	{
	        		xDrag = e.getX();
	            	yDrag = e.getY();
	            	if (mouseInRange(xDrag, yDrag))
		        	{
	            	   dragCounter ++;
	                   dragging = true;
	                   
	                   if (dragCounter % 8 == 0) 
	                   {
                	     int xRepaintRange = Math.max(Math.abs(xDrag - xDragPrev),Math.max(Math.abs(mouseX - xDrag), Math.abs(mouseX - xDragPrev)));
		                 int yRepaintRange = Math.max(Math.abs(yDrag - yDragPrev),Math.max(Math.abs(mouseY - yDrag), Math.abs(mouseY - yDragPrev)));
                		 repaint(Math.min(Math.min(mouseX, xDrag),xDragPrev) - 2,  Math.min(Math.min(mouseY, yDrag),yDragPrev) - 2, xRepaintRange + 4, yRepaintRange + 4);
                         xDragPrev = xDrag;
	        	         yDragPrev = yDrag;
		        	   }
	        	    }
	            	else
	            	{
	            		dragging = false;
	            	} 
	        	}
	        	else if(animationRunning)
	        	{
	        		JOptionPane frame = new JOptionPane();
	        		JOptionPane.showMessageDialog(frame, "Animation already running, please stop the current animation before setting another path");
	        	}
	        	else if(mouseDown && mouseInRange(mouseX, mouseY))
	            {
	        		 int xIn = e.getX();
	        		 int yIn = e.getY();
	        	  	 xArrayList.add((minX)+((xIn - xMargin)*((maxX - minX)/W)));
	        	  	 yArrayList.add((maxY)-((yIn - topOffset)*((maxY - minY)/H)));

        	    	 xPixelArrayList.add(xIn);
        	    	 yPixelArrayList.add(yIn);

	        	     double re = Math.round((minX)+((xIn - xMargin)*((maxX - minX)/W))*1000000);
			   		 double im = Math.round((maxY)-((yIn - topOffset)*((maxY - minY)/H))*1000000);
			   	     theRealOutput.setText(String.valueOf(re/1000000));
			   	     theImOutput.setText(String.valueOf(im/1000000));         	     
		             topArrayIndex = xArrayList.size();
	            }	           
	        }
	    });
		
		//
		// Code to add widgets to the frame
		//
		 Container cp = getContentPane();
		 cp.setLayout(null);
		 theRealOutput.setBounds(670,60,80,30);
		 cp.add(theRealOutput);	 
		 theImOutput.setBounds(840,60,80,30);
		 cp.add(theImOutput);
		 setVisible(true);
		 label3.setBounds(630,20,100,30);
		 label3.setFont(new Font("Serif", Font.PLAIN, 20));
		 cp.add(label3);
		 label4.setBounds(50,20,500,30);
		 label4.setFont(new Font("Serif", Font.PLAIN, 18));
		 cp.add(label4);
		 label1.setBounds(630,60,50,30);
		 label1.setFont(new Font("Serif", Font.PLAIN, 20));
		 cp.add(label1);
		 label2.setBounds(800,60,50,30);
		 label2.setFont(new Font("Serif", Font.PLAIN, 20));
		 cp.add(label2);
		 // Initialise the instance of Iterator and draw the Mandelbrot set as the background image
		 iterator = new Iterator(maxIterations, mandelbrotColours, mandelbrotCutOffs);
		 background();		

		 axisWiz = new AxisWiz(W + 1, H + 1);
		 
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
   		 		theAIToSave = (BufferedImage) createImage(W + 130, H + 130);
   		 		theAGToSave  = theAIToSave.createGraphics();
   		 		theAGToSave.drawImage(theAI, 80, 50, null);
   		 	    theAGToSave.drawString(outputC, 10,  20);

			    plotPoint(theAGToSave, 80 - xMargin + xPixelArray[animationIndex], 50 - topOffset + yPixelArray[animationIndex]);

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
 					ImageIO.write(theAIToSave, "png", new File("Mandelbrotimage" + processDate(new Date(), dateFormat) +".png"));
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
 		 		for(int i = 0; i < topArrayIndex; i++)
		 		{
		 			xPixelArray[i] = (int) Math.round((xArray[i] - minX)*(W/(maxX - minX))) + xMargin;
		 			yPixelArray[i] = (int) Math.round((maxY - yArray[i])*(H/(maxY - minY))) + topOffset;	
		 		}
 		 		
 		 		repaint();
 		 	} 	
    	 }); 
 	 
    	 setRanges.addActionListener(new ActionListener() {
		 	public void actionPerformed(ActionEvent arg0) {
		 		zoomSelect = true; 
		 	}
    	 }); 
     	 setVisible(true);
	     repaint();
		
	} // end of constructor method
	
	public void heresJulia(ApplicationJ julia)
	{
		copyOfJulia = julia;
	}

	public void paint(Graphics g) // When 'Window' is first
	{ 							  // shown or damaged
		super.paint(g);
	    reDraw((Graphics2D) g);
	    
	    if(showAxis)
		{
			axisWiz.drawAxis((Graphics2D) g, xMargin, topOffset, xAxisGap, yAxisGap);
	 	}	
	    if(zoomSelect && dragging)
		{
			((Graphics2D) g).setPaint(Color.WHITE);  
			g.drawRect(Math.min(mouseX, xDrag), Math.min(mouseY, yDrag), Math.abs(mouseX - xDrag), Math.abs(mouseY - yDrag));
		}
	}
	
	public void reDraw(Graphics2D g)
	{
		g.drawImage( theAI,  xMargin,  topOffset,  this);
		if (animationRunning)
		{
			plotPoint(g, animationX, animationY);

			Font font = new Font("Monospaced",Font.PLAIN,20);
			g.setFont(font);
			g.setPaint(new Color(255, 255, 255));
			g.drawString(outputC, xMargin + 10, topOffset + 20);
		}
	}
	
	public void plotPoint(Graphics2D g, int x, int y)
	{
		if(mouseInRange(x, y))
		{
			g.setPaint(new Color(255, 255, 255));
			g.drawLine(x-3, y, x + 3, y);
			g.drawLine(x, y - 3, x, y + 3);	
		}	
	}
	
	public void reDrawMandelbrot(Graphics2D g) // Re draw contents
	{	
		for(double yPixel = 0; yPixel < H; yPixel++)
		{
			for(double xPixel = 0; xPixel < W; xPixel++)
			{
				x = (minX)+(xPixel*((maxX - minX)/W));
				y = (maxY)-(yPixel*((maxY - minY)/H));		
				previousColour = currentColour;	
				currentColour = iterator.colourPicker(x, y, 0.0, 0.0);
				if(! previousColour.equals(currentColour) || xPixel == W - 1)
				{
					g.draw(new Line2D.Double(firstChangex , yPixel , xPixel , yPixel ));
					firstChangex = xPixel % (W - 1);
					g.setPaint(currentColour);
				}				
			}
		}
	}
	
	public boolean mouseInRange(int mouseXp, int mouseYp)
	{
		return (mouseXp <= W + xMargin && mouseYp <= H + topOffset && mouseXp >= xMargin && mouseYp >= topOffset);
	}
	
	public void displayC(double reC, double imC)
	{
		 double re = Math.round(reC*1000000);
		 double im = Math.round(imC*1000000);
		 outputC = "C = " + String.valueOf(re/1000000) + " + " + String.valueOf(im/1000000) + "i";	 
	}
	
	public void traceC(int i)
	{	
		//displayC(xArray[i], yArray[i]);
		repaint(animationX - 3, animationY - 3, 7, 7);
		System.out.println(i);
		animationIndex = i;
		int prevAnimationX, prevAnimationY;
		prevAnimationX = animationX;
		prevAnimationY = animationY;
		animationX = xPixelArray[animationIndex];
		animationY = yPixelArray[animationIndex];

	//	repaint(prevAnimationX - 3, prevAnimationY - 3, 7, 7);
		repaint(animationX - 3, animationY - 3, 7, 7);
		
		//repaint(xMargin, topOffset, 300, 25);
	}
	
	public void background()
	{
		theAI = (BufferedImage) createImage(W + 1, H + 1);
		theAG = theAI.createGraphics();		
		reDrawMandelbrot(theAG);
	}
	
	public String processDate(Date inputDate, DateFormat format)
	{
		String dateString = format.format(inputDate);
		dateString = dateString.replace(':', ' ');
		dateString = dateString.replace('/', ' ');
		return dateString;
	}	
	
	public void applicationRunningTrue()
	{
		animationRunning = true;	
	}
	
	public void applicationRunningFalse()
	{
		animationRunning = false;	
	}
	
	public String getReCString()
	{
		return theRealOutput.getText();
	}
	
	public String getImCString()
	{
		return theImOutput.getText();  
	}
}