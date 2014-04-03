import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JLabel;


public class AxisWiz {
	
	int W, H;
	public AxisWiz (int width, int height)
	{
		W = width;
		H = height;
	}   
	
	public JLabel[] setAxisLabels(JLabel[] axisLabelArray , double a, double b, double c, double d, double e, double f)
	{
		axisLabelArray[0].setText(axisLabelNumber(a));
		axisLabelArray[1].setText(axisLabelNumber(b));
		axisLabelArray[2].setText(axisLabelNumber(c));
		axisLabelArray[3].setText(axisLabelNumber(d));
		axisLabelArray[4].setText(axisLabelNumber(e));
		axisLabelArray[5].setText(axisLabelNumber(f));
		return axisLabelArray;
	}
	
	public void setVisibility(JLabel[] axisLabel, boolean value)
	{
		for(int i = 0; i < axisLabel.length; i++)
		{
			axisLabel[i].setVisible(value);
		}
	}
	
	public String axisLabelNumber(double inputNumber)
	{
	    double number = inputNumber;	
		double multiplier;
		int count = 0;
		double numberRounded;
		String output = "";
		if (Math.abs(number) < 1)
		{
			multiplier = 10;
		}
		else
		{
			multiplier = 0.1;
		}
		if (number != 0)
		{
		    while (Math.abs(number) < 1 || Math.abs(number) >= 10)
		    {
		    	number = number * multiplier;
		    	count++;
	    	}
		}
		if (multiplier == 10)
		{
			count = -1*count;
		}
		numberRounded = Math.round(number * 1000);
		number = numberRounded/1000;
		output = String.valueOf(number);
		if (count != 0) 
		{
			output = output + "E" + String.valueOf(count);
		}
		int len = output.length();
		for (int i = 0; i < 8 - len; i++)
		{
			output = "  " + output;
		}
		output = " " + output;
		return output;
	}
	
	
	public void placeAxisLables(JFrame frame, JLabel[] axisLabelArray, int xMargin, int topOffset)
	{
	   int adjustment = - 72;
	   axisLabelArray[0].setBounds(xMargin - 71, topOffset + adjustment, 100, 50);   
       axisLabelArray[1].setBounds(xMargin - 71, topOffset + adjustment + H/2, 100, 50);     
       axisLabelArray[2].setBounds(xMargin - 71, topOffset + adjustment + H, 100, 50);    
       axisLabelArray[3].setBounds(xMargin - 36, topOffset + adjustment + H + 25, 100, 50);      
       axisLabelArray[4].setBounds(xMargin + W/2 - 36, topOffset + adjustment + H + 25, 100, 50);
       axisLabelArray[5].setBounds(xMargin + W - 36, topOffset + adjustment + H + 25, 100, 50);
	 
       for (int ix = 0; ix < axisLabelArray.length; ix++)
       {
   	       axisLabelArray[ix].setFont(new Font("Serif", Font.PLAIN, 12));   
           frame.getContentPane().add(axisLabelArray[ix]);
           axisLabelArray[ix].setVisible(false);
       }
	}
	
	public void drawAxis(Graphics2D g, int xMarginp, int topOffsetp, int xAxisGapp, int yAxisGapp)
	{
		((Graphics2D) g).setPaint(Color.BLACK);
		// draw axes
 		g.drawLine(xMarginp - xAxisGapp, topOffsetp, xMarginp - xAxisGapp, topOffsetp + H + yAxisGapp);
 	 	g.drawLine(xMarginp - xAxisGapp, topOffsetp + H  + yAxisGapp, xMarginp + W - 1, topOffsetp + H + yAxisGapp);
 	 	// draw tick marks at start and end of X axis
 	 	g.drawLine(xMarginp, topOffsetp + H + yAxisGapp, xMarginp, topOffsetp + H + yAxisGapp + 8);
 	 	g.drawLine(xMarginp + W - 1, topOffsetp + H + yAxisGapp, xMarginp + W - 1, topOffsetp + H + yAxisGapp + 8);
 	 	// draw tick mark at mid point of X axis
 	 	int midX = (W - 1)/2;
 	 	if (midX*2 != W - 1) {g.drawLine(xMarginp + midX + 1, topOffsetp + H + yAxisGapp, xMarginp + midX + 1, topOffsetp + H + yAxisGapp + 7);}
 	 	g.drawLine(xMarginp + midX, topOffsetp + H + yAxisGapp, xMarginp + midX, topOffsetp + H + yAxisGapp + 7);
 	 	// draw tick marks at start and end of Y axis
 	 	g.drawLine(xMarginp - yAxisGapp - 8, topOffsetp, xMarginp - yAxisGapp, topOffsetp);
 	 	g.drawLine(xMarginp - yAxisGapp - 8, topOffsetp + H - 1, xMarginp - yAxisGapp, topOffsetp + H - 1);
 	 	// draw tick mark at mid point of Y axis
 	 	int midY = (H - 1)/2;
 	 	if (midY*2 != H - 1) {g.drawLine(xMarginp - yAxisGapp - 7, topOffsetp + midY + 1, xMarginp - yAxisGapp, topOffsetp + midY + 1);}
 	 	g.drawLine(xMarginp - yAxisGapp - 7, topOffsetp + midY, xMarginp - yAxisGapp, topOffsetp + midY);
 	 	// draw small tick marks on X axis
 	 	int tenthOfW = (W - 1)/10;
 	 	if (tenthOfW*10 == W - 1)
 	 	{ 
 	 		for (int incW = tenthOfW; incW < W; incW +=tenthOfW)
 	 		{
 	 			g.drawLine(xMarginp + incW, topOffsetp + H + yAxisGapp, xMarginp + incW, topOffsetp + H + yAxisGapp + 4);
 	 		}
 	 	}
 	 	// Draw small tick marks on Y axis
 	 	int tenthOfH = (H - 1)/10;
 	 	if (tenthOfH*10 == H - 1)
 	 	{ 
 	 		for (int incH = tenthOfH; incH < H; incH +=tenthOfH)
 	 		{
 	 			g.drawLine(xMarginp - yAxisGapp - 5, topOffsetp + incH, xMarginp - yAxisGapp, topOffsetp + incH);
 	 		}
 	 	}
	}

}
