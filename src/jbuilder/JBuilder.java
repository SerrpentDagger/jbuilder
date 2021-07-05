package jbuilder;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class JBuilder extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	/////////////
	
	private final HashMap<JButton, Runnable> buttons = new HashMap<JButton, Runnable>();
	public final LayoutManager layout;
	public static int gap = 10;
	
	private AtomicBoolean go = new AtomicBoolean(false);
	
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
	
	public JComboBox<String> addDropMenu(String[] choices)
	{
		JComboBox<String> menu;
		this.add(menu = new JComboBox<String>(choices));
		return menu;
	}
	
	public JTextArea addTextArea()
	{
		JTextArea area = new JTextArea();
		this.add(area);
		return area;
	}
	public JBuilder addTextArea(JTextArea area)
	{
		add(area);
		return this;
	}
	
	public JButton addGetButton(String text, Runnable onClick)
	{
		JButton b = new JButton(text);
		b.addActionListener(this);
		add(b);
		
		buttons.put(b, onClick);
		return b;
	}
	
	public JButton addGetToggleButton(String t1, String t2, Runnable onT1, Runnable onT2)
	{
		JButton[] b = new JButton[1];
		b[0] = addGetButton(t1, () ->
		{
			if (b[0].getText().equals(t1))
			{
				b[0].setText(t2);
				onT1.run();
			}
			else
			{
				b[0].setText(t1);
				onT2.run();
			}
		});
		
		return b[0];
	}
	
	public JBuilder addButton(String text, Runnable onClick)
	{
		addGetButton(text, onClick);
		return this;
	}
	
	public JBuilder go()
	{
		go.set(true);
		return this;
	}
	
	public JBuilder waitFor()
	{
		go.set(false);
		try
		{
			while (!go.get())
				Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
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
	
	public static JBuilder error(String error)
	{
		return info("Error: ", error);
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
	
	public static JBuilder youSure(String message, Runnable onSure)
	{
		return yesNo(message, onSure, () -> {});
	}
	
	public static JBuilder yesNo(String message, Runnable onYes, Runnable onNo)
	{
		JBuilder b = new JBuilder(new GridLayout(2, 2, gap, gap));
		return b.setClose(DISPOSE_ON_CLOSE)
				.addLabel(message).addLabel("")
				.addButton("Yes", () -> b.thenDispose(onYes))
				.addButton("No", () -> b.thenDispose(onNo));
	}
	
	public static JBuilder eitherOr(String message, String buttonOne, String buttonTwo, Runnable onOne, Runnable onTwo)
	{
		JBuilder b = new JBuilder(new GridLayout(2, 2, gap, gap));
		return b.setClose(DISPOSE_ON_CLOSE)
				.addLabel(message).addLabel("")
				.addButton(buttonOne, onOne)
				.addButton(buttonTwo, onTwo);
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
