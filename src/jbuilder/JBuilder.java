package jbuilder;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class JBuilder extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	/////////////
	
	private final HashMap<JButton, Runnable> buttons = new HashMap<JButton, Runnable>();
	public final LayoutManager layout;
	public static int gap = 10;
	
	public JBuilder(LayoutManager style)
	{
		layout = style;
	}
	
	public JBuilder setClose(int close)
	{
		this.setDefaultCloseOperation(close);
		return this;
	}
	
	public JBuilder addLabel(String text)
	{
		this.add(new JLabel(text));
		return this;
	}
	
	public JTextField addTextField()
	{
		JTextField field;
		this.add(field = new JTextField());
		return field;
	}
	
	public JBuilder addButton(String text, Runnable onClick)
	{
		JButton b = new JButton(text);
		b.addActionListener(this);
		add(b);
		
		buttons.put(b, onClick);
		return this;
	}
	
	public JBuilder showFrame()
	{
		this.setLayout(layout);
		this.pack();
		this.setVisible(true);
		return this;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Runnable run = buttons.get(arg0.getSource());
		run.run();
	}
	
	////////////////////////////////////
	
	private void thenDispose(Runnable run)
	{
		run.run();
		dispose();
	}
	
	public static JBuilder keyValInfo(String[] keys, String[] vals)
	{
		if (keys.length != vals.length)
			throw new IllegalArgumentException("Keys and vals must be of equal length.");
		JBuilder b = new JBuilder(new GridLayout(keys.length, 2, gap, gap));
		for (int i = 0; i < keys.length; i++)
			b.addLabel(keys[i]).addLabel(vals[i]);
		return b;
	}
	
	public static JBuilder pairsInfo(String[][] pairs)
	{
		JBuilder b = new JBuilder(new GridLayout(pairs.length, 2, gap, gap));
		for (String[] pair : pairs)
			b.addLabel(pair[0]).addLabel(pair[1]);
		return b;
	}
	
	public static JBuilder info(String message)
	{
		JBuilder b = new JBuilder(flow());
		return b.setClose(DISPOSE_ON_CLOSE)
				.addLabel(message);
	}
	
	public static JBuilder info(String... message)
	{
		JBuilder b = new JBuilder(new GridLayout(message.length, 1, gap, gap));
		for (String str : message)
			b.addLabel(str);
		return b;
	}
	
	public static JBuilder confirm(String message, Runnable onConfirm)
	{
		JBuilder b = new JBuilder(new GridLayout(2, 1, gap, gap));
		return b.setClose(DISPOSE_ON_CLOSE)
				.addLabel(message)
				.addButton("Confirm", () -> b.thenDispose(onConfirm));
	}
	
	public static JBuilder yesNo(String message, Runnable onYes, Runnable onNo)
	{
		JBuilder b = new JBuilder(new GridLayout(2, 1, gap, gap));
		return b.setClose(DISPOSE_ON_CLOSE)
				.addLabel(message)
				.addButton("Yes", () -> b.thenDispose(onYes))
				.addButton("No", () -> b.thenDispose(onNo));
	}
	
	public static FlowLayout flow()
	{
		FlowLayout layout = new FlowLayout();
		layout.setHgap(gap);
		layout.setVgap(gap);
		layout.setAlignment(FlowLayout.CENTER);
		return layout;
	}
}
