package Gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JFrame;

import Logic.Agent;

@SuppressWarnings("serial")
public class Core extends JFrame implements ActionListener{

	private UniversePanel universe = new UniversePanel();
	private ControlPanel control = new ControlPanel(this, universe.getLabels());
	private Agent agent = new Agent(this);

	public Core()
	{
		setLayout(new BorderLayout());
		
		add(universe, BorderLayout.CENTER);
		add(control, BorderLayout.WEST);
		
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		pack();
		setSize(1000, 500);
		setVisible(true);	
		
		(new Thread(agent)).start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		switch (arg0.getActionCommand())
		{
		case "up" :
			universe.Up((Character) arg0.getSource());
			break;
		case "down" :
			universe.Down((Character) arg0.getSource());
			break;
		case "left" :
			universe.Left((Character) arg0.getSource());
			break;
		case "right" :
			universe.Right((Character) arg0.getSource());
			break;
		case "run" :
			universe.Restore();
			agent.InitBeliefs();
			agent.Run();
			break;
		case "stop" :
			agent.Stop();
			break;
		case "pick" :
			universe.PickUp((String) arg0.getSource());
			break;
		case "put" :
			universe.PutDown((String) arg0.getSource());
			break;
		case "belief" :
			agent.InitBeliefs();
			break;
		case "desire" :
			agent.InitDesires();
			break;
		case "reset" :
			universe.Restore();
			agent.InitBeliefs();
			break;
		case "memorize" :
			universe.Memorize();
		break;
		}
	}
	
	public void getBeliefs(HashSet<Logic.Block> beliefs, Logic.Block inHand)
	{
		universe.getBeliefs(beliefs, inHand);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Core();
			}
		});
	}
}
