package semantic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JLabel;

import Entity.ACTIONTable;
import Entity.grammerTable;

import java.awt.Font;
import java.util.ArrayList;

public class SLRPanel {

	JFrame frame;
    private DefaultTableModel slrListTbModel;
    private DefaultTableModel firstListTbModel;
    private DefaultTableModel followListTbModel;
	private ArrayList<ACTIONTable> slrTable;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SLRPanel window = new SLRPanel();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws Exception 
	 */
	public SLRPanel() throws Exception {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws Exception 
	 */
	private void initialize() throws Exception {
		frame = new JFrame();
		frame.setBounds(100, 100, 1400, 1000);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		frame.setLocation((screensize.width - frameSize.width) / 2,
				(screensize.height - frameSize.height) / 2);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(200, 200, 200));
		panel.setBounds(0, 0, 1400, 1000);
		frame.getContentPane().add(panel);
		
		slrListTbModel = new DefaultTableModel(new Object[][] {},
				new String[] { "", "","","","","","","","","", "", "A", "C", "T", "I", "O", "N", 
						  	"","", "","","","","","","","","","", "","",
							"","","","G", "O", "T", "O","","",""});

		slrListTbModel.addRow(new Object[] { "",  "+", "*", "-", "(", "id", "digit", "=", "call", ")", "[", "]", ",", ";",
				">","true","false","not","and","or","if","{","else","while","do","proc","record","int","char","}","$", 
				"S", "E", "L", "F", "B","P","D","X","C","T"});
		for(int j=1;j<120;j++)
		{
			slrListTbModel.addRow(new Object[] {j, "","","","","","","","","", "", "", "", "", "", "", "", 
				  	"","", "","","","","","","","","","","", "",
					"","","","", "", "", "","","",""});
		}
		JTable slrListTb = new JTable();
		slrListTb.setBackground(new Color(255, 255, 255));
		slrListTb.setFillsViewportHeight(true);
		slrListTb.setModel(slrListTbModel);
		// errorListTbModel.addRow(new Object[] { "����", "���","����ԭ��" ,"value"});
		RowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(slrListTbModel);
		panel.setLayout(null);
		slrListTb.setRowSorter(sorter);
		JScrollPane slrSP = new JScrollPane();
		slrSP.setViewportView(slrListTb);
		slrSP.setBounds(40, 460, 1300, 470);
		panel.add(slrSP);
		
		JLabel lblDfaTable = new JLabel("SLR������");
		lblDfaTable.setFont(new Font("����", Font.BOLD, 55));
		lblDfaTable.setBounds(526, 370, 350, 110);
		panel.add(lblDfaTable);
		grammerTable[] grammerTable = new readDFATable().Wenfa();
		slrTable=new Main().getSLRTable(grammerTable);
		ActionTest(slrTable);
		
// first�����
		firstListTbModel = new DefaultTableModel(new Object[][] {}, new String[] {"����", "FIRST��" });
		String[][] firstGroup = new Main().getFirstDroup(grammerTable);
		for (int i = 0; i < firstGroup.length; i++) {
			for (int j = 0; j < 2; j++) {
				System.out.print(firstGroup[i][j] + "  ");
			}
			firstListTbModel.addRow(new Object[] { firstGroup[i][0],firstGroup[i][1] });
			System.out.println();
		}
		RowSorter<DefaultTableModel> sorter1 = new TableRowSorter<DefaultTableModel>(firstListTbModel);
// Follow���
		followListTbModel = new DefaultTableModel(new Object[][] {},new String[] { "���ս��", "Follow��" });
		String[][] followGroup = new Main().getFollowGroup();
		for (int i = 0; i < followGroup.length; i++) {
			for (int j = 0; j < 2; j++) {
				System.out.print(followGroup[i][j] + "  ");
			}
			followListTbModel.addRow(new Object[] { followGroup[i][0],followGroup[i][1] });
			System.out.println();
		}
		RowSorter<DefaultTableModel> sorter4 = new TableRowSorter<DefaultTableModel>(followListTbModel);
		JTable firstListTb = new JTable();
		firstListTb.setBackground(new Color(255, 255, 255));
		firstListTb.setFillsViewportHeight(true);
		firstListTb.setModel(firstListTbModel);
		firstListTb.setRowSorter(sorter1);
		JScrollPane firstSP = new JScrollPane();
		firstSP.setBounds(40, 100, 640, 280);
		panel.add(firstSP);
		firstSP.setViewportView(firstListTb);
		
		JTable followListTb = new JTable();
		followListTb.setBackground(new Color(255, 255, 255));
		followListTb.setFillsViewportHeight(true);
		followListTb.setModel(followListTbModel);
		followListTb.setRowSorter(sorter4);
		JScrollPane followSP = new JScrollPane();
		followSP.setBounds(700, 100, 640, 280);
		panel.add(followSP);
		followSP.setViewportView(followListTb);
		
		JLabel lblFirstTable = new JLabel("First����");
		lblFirstTable.setFont(new Font("����", Font.BOLD, 55));
		lblFirstTable.setBounds(210, 20, 340, 80);
		panel.add(lblFirstTable);
		
		JLabel lblFollowTable = new JLabel("Follow����");
		lblFollowTable.setFont(new Font("����", Font.BOLD, 55));
		lblFollowTable.setBounds(860, 20, 460, 80);
		panel.add(lblFollowTable);
	}
	public void ActionTest(ArrayList<ACTIONTable> actionTable){
		for (int i = 0; i < actionTable.size(); i++) {
			System.out.print("״̬: " + actionTable.get(i).getState());
			System.out.print("  ���� :" + actionTable.get(i).getInput());
			String[] str1 =actionTable.get(i).getAction();
			StringBuffer sb1 = new StringBuffer();
			for (int y = 0; y < str1.length; y++) {
				if(!str1[y].equals("h"))
				{
					sb1.append(str1[y]);
				}
				
			}
			String s1 = sb1.toString();
			System.out.println("  ת��:" + s1);
			System.out.println("  ����   " + actionTable.get(i).getState());
			System.out.println("  ����   " + getColumn(actionTable.get(i).getInput()));
			slrListTbModel.setValueAt(s1,actionTable.get(i).getState(), getColumn(actionTable.get(i).getInput()));
			System.out.println();
		}
	}
	public int getColumn(String str){
		switch (str){
		case "+":
			return 1;
		case "*":
			return 2;
		case "-":
			return 3;
		case "(":
			return 4;
		case "id":
			return 5;
		case "digit":
			return 6;
		case "=":
			return 7;
		case "call":
			return 8;
		case ")":
			return 9;
		case "[":
			return 10;
		case "]":
			return 11;
		case ",":
			return 12;
		case ";":
			return 13;
		case ">":
			return 14;	
		case "true":
			return 15;
		case "false":
			return 16;
		case "not":
			return 17;
		case "and":
			return 18;
		case "or":
			return 19;
		case "if":
			return 20;
		case "{":
			return 21;
		case "else":
			return 22;
		case "while":
			return 23;
		case "do":
			return 24;
		case "proc":
			return 25;
		case "record":
			return 26;
		case "int":
			return 27;
		case "char":
			return 28;
		case "}":
			return 29;
		case "$":
			return 30;
		case "S":
			return 31;
		case "E":
			return 32;
		case "L":
			return 33;
		case "F":
			return 34;
		case "B":
			return 35;
		case "P":
			return 36;
		case "D":
			return 37;
		case "X":
			return 38;
		case "C":
			return 39;
		case "T":
			return 40;
		default:
			return 0;
		}
		//return -1;
	}
}
