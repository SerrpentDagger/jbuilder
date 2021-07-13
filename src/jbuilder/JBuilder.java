package jbuilder;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class JBuilder extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	/////////////
	
	private final HashMap<JButton, Runnable> buttons = new HashMap<JButton, Runnable>();
	public final LayoutManager layout;
	public static int gap = 10;
	public static int spaces = 2;
	
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
		this.add(new JLabel(spaces(text)));
		return this;
	}
	
	public JLabel addGetLabel(String text)
	{
		JLabel l = new JLabel(spaces(text));
		add(l);
		return l;
	}
	
	public JTextField addTextField()
	{
		JTextField field;
		this.add(field = new JTextField());
		return field;
	}
	
	public JTextArea addTextArea(int x, int y, boolean editable)
	{
		JTextArea area = new JTextArea(x, y);
		area.setEditable(editable);
		this.add(new JScrollPane(area));
		return area;
	}
	
	public JBuilder addScrollPane(Component c)
	{
		add(new JScrollPane(c));
		return this;
	}
	
	public JComboBox<String> addDropMenu(String[] choices)
	{
		int n = 0;
		for (int i = 0; i < choices.length; i++)
			if (choices[i] == null)
				n++;
		
		String[] valid = new String[choices.length - n];
		int v = 0;
		for (int i = 0; i < choices.length; i++)
			if (choices[i] != null)
				valid[v++] = choices[i];
		
		JComboBox<String> menu;
		this.add(menu = new JComboBox<String>(valid));
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
			else if (b[0].getText().equals(t2))
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
	
	public JBuilder closeGo()
	{
		JBuilder th = this;
		th.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e) { th.go(); }
		});
		return th;
	}
	
	public JBuilder onClose(Runnable onClose)
	{
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e) { onClose.run(); }
		});
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
	
	public String spaces(String message)
	{
		String out = message;
		for (int i = 0; i < spaces; i++)
			out = " " + out + " ";
		return out;
	}
	
	private Runnable thenDispose(Runnable run)
	{
		return () ->
		{
			run.run();
			dispose();
		};
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
	
	public static JBuilder warning(String warn)
	{
		return info("Warning: ", warn);
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
				.addButton("Confirm", b.thenDispose(onConfirm));
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
				.addButton("Yes", b.thenDispose(onYes))
				.addButton("No", b.thenDispose(onNo));
	}
	
	public static JBuilder eitherOr(String message, String buttonOne, String buttonTwo, Runnable onOne, Runnable onTwo)
	{
		JBuilder b = new JBuilder(new GridLayout(2, 2, gap, gap));
		return b.setClose(DISPOSE_ON_CLOSE)
				.addLabel(message).addLabel("")
				.addButton(buttonOne, b.thenDispose(onOne))
				.addButton(buttonTwo, b.thenDispose(onTwo));
	}
	
	public static JBuilder confirmOrKeepOption(String message, Runnable onConfirm, Runnable onKeep, Object checkNull)
	{
		if (checkNull == null)
			return JBuilder.confirm(message, onConfirm);
		else
			return JBuilder.choices(message, new String[] { "Confirm", "Keep Current" }, new Runnable[] { onConfirm, onKeep });
	}
	
	public static JBuilder choices(String message, String[] names, Runnable[] runs)
	{
		if (names.length != runs.length)
			throw new IllegalStateException("Cannot create a choices menu with arrays of unequal length.");
		JBuilder b = new JBuilder(new GridLayout(names.length + 1, 1, gap, gap));
		b.setClose(DISPOSE_ON_CLOSE);
		b.addLabel(message);
		for (int i = 0; i < names.length; i++)
			b.addButton(names[i], b.thenDispose(runs[i]));
		return b;
	}
	
	public static <T> T[] dataRequest(String message, String[] labels, Function<String, T> parser, T[] array, Consumer<T[]> onGot)
	{
		JBuilder build = new JBuilder(new GridLayout(labels.length + 2, 2, gap, gap));
		build.addLabel(message).addLabel("");
		JTextField[] fields = new JTextField[labels.length];
		for (int i = 0; i < labels.length; i++)
		{
			build.addLabel(labels[i]);
			fields[i] = build.addTextField();
			fields[i].setText(array[i].toString());
		}
		build.addButton("Confirm", () ->
		{
			boolean valid = true;
			for (int i = 0; i < fields.length; i++)
			{
				valid = true;
				String str = fields[i].getText();
				T t = parser.apply(str);
				if (t == null)
				{
					JBuilder.error("Invalid input value: " + str + " for field: " + labels[i]).showFrame();
					valid = false;
					break;
				}
				else
				{
					array[i] = t;
				}
			}
			if (valid)
			{
				build.go();
				build.dispose();
				onGot.accept(array);
			}
		}).showFrame();
		return array;
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
