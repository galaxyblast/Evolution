package net.galaxyblast.evolution;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class Evolution extends JFrame
{
	private Board canvas = new Board();
	
	public static final boolean debugMode = true;
	
	public Evolution()
	{
		initUI();
	}

	private void initUI()
	{
		add(canvas);
		setSize(1200, 1000);
		setTitle("Evolution");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
	}

	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				Evolution ex = new Evolution();
				ex.setVisible(true);
			}
		});
	}
}
