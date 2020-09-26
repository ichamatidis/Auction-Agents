package auction;


import jade.core.AID;
import java.util.Queue;
import java.util.LinkedList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import auction.Auctioner;

class AuctionerGUI extends JFrame {	

	private Auctioner myAgent;
	private JFrame frame;
	
	public JTextField textField;//Add to auction
	public JTextField textField_1;//Active item
	
	public JTextArea textArea;//Sold Box
	private JTextArea textArea_1;//For Auction box
	private JButton btnNewButton;
	
	public Queue<String> items = new LinkedList<>();
	public Queue<String> prizes = new LinkedList<>();
	
	public Queue<String> auc_item = new LinkedList<>();
	public Queue<String> auc_price = new LinkedList<>();
	
    AuctionerGUI(Auctioner a) {
		myAgent = a;
		initialize();
		addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    myAgent.doDelete();
                }
            } );
	}
    
    public void initialize() {
		frame = new JFrame("English Auction");
		frame.setDefaultLookAndFeelDecorated(true);
		frame.setBounds(100, 100, 579, 473);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textArea = new JTextArea();
		textArea.setBounds(292, 50, 235, 283);
		textArea.setEditable(false);
		frame.getContentPane().add(textArea);
		
		textArea_1 = new JTextArea();
		textArea_1.setBounds(32, 50, 235, 97);
		textArea_1.setEditable(false);
		frame.getContentPane().add(textArea_1);
		
		JLabel lblNewLabel = new JLabel("Items for Auction");
		lblNewLabel.setBounds(32, 31, 101, 14);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Sold items");
		lblNewLabel_1.setBounds(296, 31, 101, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Add item to auction");
		lblNewLabel_2.setBounds(32, 292, 120, 14);
		frame.getContentPane().add(lblNewLabel_2);
		
		textField = new JTextField();
		textField.setBounds(32, 313, 235, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Add item");
		btnNewButton.setBounds(32, 348, 89, 23);
		btnNewButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
			    ButtonPressed();
			  } 
			} );
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblNewLabel_3 = new JLabel("Active item in auction");
		lblNewLabel_3.setBounds(32, 158, 123, 14);
		frame.getContentPane().add(lblNewLabel_3);
		
		textField_1 = new JTextField();
		textField_1.setBounds(32, 183, 235, 20);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Current Highest Bid");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_4.setBounds(32, 214, 123, 14);
		frame.getContentPane().add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("0 $");
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel_5.setBounds(32, 239, 101, 42);
		frame.getContentPane().add(lblNewLabel_5);	

		setResizable(false);
		frame.setVisible(true);
		
    }
    public void ButtonPressed() {
    	String item = textField.getText();
    	if(!item.isEmpty() )
    		addItem(item,"0");
    	textField.setText("");
    }
    public void addItem(String item, String price) {
    	textArea_1.append(item+"\n");
    	items.add(item);
    	prizes.add(price);
    }
    public void removeItem() {
    	String item = items.remove();
    	textField_1.setText(item);
    	auc_item.add(item);
    	auc_price.add(prizes.remove());
    }
    public void sell_item(int max_bider, int max_bid) {	
    	String sold = auc_item.remove()+" "+(max_bid)+"$ ";
    	auc_price.remove();
    	textArea.append(sold+"to bider"+Integer.toString(max_bider)+"\n");
    	if(items.isEmpty()) textField_1.setText("");
    }
}
