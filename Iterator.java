import java.awt.Color;

public class Iterator {
	int max;
	Color[] colours;
	int[] cutoffs;

	public Iterator(int maxIterations, Color[] newColours, int[] newCutoffs) {
		max = maxIterations;
		colours = newColours;
		cutoffs = newCutoffs;

	}

	public int juliaIteration(double reC, double imC, double reZ, double imZ, int maxIterations) {
		double temp = 0;
		int iterations = 0;
		while (reZ * reZ + imZ * imZ < 4 && iterations <= maxIterations) {
			temp = (reZ * reZ) - (imZ * imZ) + reC;
			imZ = (2 * reZ * imZ) + imC;
			reZ = temp;
			iterations++;
		}
		return iterations;
	}

	public Color colourPicker(double reC, double imC, double reZ, double imZ)
	{
		Color currentColour;
		int iterations = juliaIteration(reC, imC, reZ, imZ, max);
		if (iterations > max)
			currentColour = new Color(00, 00, 00);
		else 
		{
			int iMax = Math.min(colours.length, cutoffs.length);
			int i = 0;
			while (iterations >= cutoffs[i] && i < iMax - 1) 
			{
				i++;
			}
			currentColour = colours[i];
		}
		return currentColour;
	}

}
