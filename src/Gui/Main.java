package Gui;

import java.awt.EventQueue;

public class Main 
{
	public static void main(String[] args) 
	{
		final String inputFile;
		if (args.length < 1 || args[1] == null || args[1].isEmpty())
		{	
			inputFile = "in.txt";
			System.err.println("No input file provided - "
					+ "switching to the default one \"" + inputFile + "\"");
		}
		else	
		{
			inputFile = args[0];
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Core(inputFile);
			}
		});
	}
}
