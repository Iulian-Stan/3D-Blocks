package Gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import Logic.Agent;

@SuppressWarnings("serial")
public class Core extends JFrame implements ActionListener
{
	private WorldPanel _worldPanel;
	private ControlPanel _controlPanel;
	private Agent _agent;

	public Core(String file)
	{
		try {
			Map<Character, Character> beliefs = new HashMap<Character, Character>(), 
					desires = new HashMap<Character, Character>();
			
			ReadData(file, beliefs, desires);
			
			_worldPanel = new WorldPanel(beliefs);
			_controlPanel = new ControlPanel(this, beliefs.keySet().toArray(new Character[0]));
			_agent = new Agent(this, beliefs, desires);

			setLayout(new BorderLayout());

			add(_worldPanel, BorderLayout.CENTER);
			add(_controlPanel, BorderLayout.WEST);

			setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			pack();
			setSize(1000, 500);
			setVisible(true);	

			(new Thread(_agent)).start();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}			
	}

	private void ReadData(String file, Map<Character, Character> beliefs, Map<Character, Character> desires) throws Exception 
	{
		BufferedReader br = null;
		String line = null;
		boolean inBeliefs = true;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null)
			{
				if (line.trim().isEmpty())
					inBeliefs = false;
				Character aux = null;
				for (int i = 0; i < line.length(); ++i)
				{
					if (!Character.isLetterOrDigit(line.charAt(i)))
						continue;
					if (inBeliefs)
					{
						if (beliefs.containsKey(line.charAt(i)))
							throw new Exception("Inconsistent condition: " + line.charAt(i) + " appears twice in beliefs.");
						beliefs.put(line.charAt(i), aux);
					}
					else
					{
						if (!beliefs.containsKey(line.charAt(i)))
							throw new Exception("Inconsistent condition: " + line.charAt(i) + " does not apear in innitial config.");
						if (desires.containsKey(line.charAt(i)))
							throw new Exception("Inconsistent condition: " + line.charAt(i) + " appears twice in desires.");
						desires.put(line.charAt(i), aux);
					}
					aux = line.charAt(i);
				}
			}
		} catch (IOException e) {
			throw new Exception("Could not read config file. " + e.getMessage());
		}
		finally {
			br.close();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		switch (arg0.getActionCommand())
		{
		case "up" :
			_worldPanel.Up((Character) arg0.getSource());
			break;
		case "down" :
			_worldPanel.Down((Character) arg0.getSource());
			break;
		case "left" :
			_worldPanel.Left((Character) arg0.getSource());
			break;
		case "right" :
			_worldPanel.Right((Character) arg0.getSource());
			break;
		case "move" :
			_worldPanel.Move((String) arg0.getSource());
			break;
		case "run" :
			_worldPanel.GetConfiguration(_agent.GetBeliefs());
			_agent.Run();
			break;
		case "stop" :
			_agent.Stop();
			break;
		case "get beliefs" :			
			_worldPanel.DispalyConfiguration(_agent.GetMemory());
			break;
		case "get desires" :
			_worldPanel.DispalyConfiguration(_agent.GetDesires());
			break;
		case "set beliefs" :
			_worldPanel.GetConfiguration(_agent.GetMemory());
			break;
		case "set desires" :
			_worldPanel.GetConfiguration(_agent.GetDesires());
			break;
		}
	}

	public void GetConfiguration(Map<Character, Character> beliefs)
	{
		_worldPanel.GetConfiguration(beliefs);
	}
}
