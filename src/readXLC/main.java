package readXLC;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class main {
	
	static String pathString;
	public static void createUI() {
		JFrame frame = new JFrame("Vodi Language Tool 1.13");
		
		final JLabel label = new JLabel("Status: no file choosen");
		//frame.getContentPane().add(label);

		JButton button = new JButton("Select Folder");
		JButton processButton = new JButton("RUN");
		//button.setBounds(0, 0, 100, 40);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 120, 200);
		
		panel.add(label);
		panel.add(button);
		panel.add(processButton);

		frame.getContentPane().add(panel);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		//frame.setSize(320, 160);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser("D:\\LANGUAGE");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showDialog(null, "Open");

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            label.setText(fc.getSelectedFile().getAbsolutePath());
		            pathString = fc.getSelectedFile().getAbsolutePath();
		        } else {
		            label.setText(fc.getSelectedFile().getAbsolutePath());
		            pathString = fc.getSelectedFile().getAbsolutePath();
		        }
			}
		});
		
		processButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				label.setText("Running..");
				try {
					sVodiHelper vodiExtend = new sVodiHelper(pathString+"\\");
					vodiExtend.process();
					label.setText("Done");
				} catch (IOException e) {
					e.printStackTrace();
					label.setText("Error");
				}
			}
		});
	}

	public static void main(String[] args) throws IOException {
		//createUI();
		sVodiHelper.printKey("D:\\LANGUAGE\\2.2\\text.txt");
	}
}
