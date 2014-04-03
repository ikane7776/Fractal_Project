import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JInternalFrame;


class Main {
	public static void main(String args[]) 
	{ 
		 ApplicationM mandelbrot = (new ApplicationM()); // Start application
		 ApplicationJ julia = (new ApplicationJ(mandelbrot)); 
		 ApplicationJOrbit juliaOrbit = (new ApplicationJOrbit(mandelbrot,julia));
		 mandelbrot.heresJulia(julia);
	} 
}  


