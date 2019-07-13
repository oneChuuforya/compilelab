package com.analyze;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
//import com.ui.*;

public class Latex
{
	private String text;
	/*private JTable jtable1=new javax.swing.JTable();
	private JTable jtable2=new javax.swing.JTable();
	private JTable jtable3=new javax.swing.JTable();*/
	public static int symbol_pos = 0;//记录符号表位置
	public static Map<String, Integer> symbol = new HashMap<String, Integer>();//符号表HashMap

	public Latex(String text, JTable jtable1, JTable jtable2, JTable jtable3)
	{
		this.text = text;
		/*this.jtable1 = jtable1;
		this.jtable2 = jtable2;
		this.jtable3 = jtable3;*/
	}
	public static void main(String args[]) throws IOException {
		javax.swing.JTable jTable1;
		javax.swing.JTable jTable2;
		javax.swing.JTable jTable3;
		jTable1 = new javax.swing.JTable();
		jTable2 = new javax.swing.JTable();
		jTable3 = new javax.swing.JTable();
		String program = readFile("D:\\作业\\编译原理\\HIT_Compiler_Experiment-master\\HIT_Compiler_Experiment-master\\测试样例.txt");
		Latex latex = new Latex(program, jTable1, jTable2, jTable3);		
		latex.analyze();
    }
	 public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
	        InputStream is = new FileInputStream(filePath);
	        String line; // 用来保存每行读取的内容
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        line = reader.readLine(); // 读取第一行
	        while (line != null) { // 如果 line 为空说明读完了
	            buffer.append(line); // 将读到的内容添加到 buffer 中
	            buffer.append("\n"); // 添加换行符
	            line = reader.readLine(); // 读取下一行
	        }
	        reader.close();
	        is.close();
	    }
	    public static String readFile(String filePath) throws IOException {
	        StringBuffer sb = new StringBuffer();
	        readToBuffer(sb, filePath);
	        return sb.toString();
	    }
	@SuppressWarnings("serial")
	public void analyze()
	{
		 JTable jtable1=new JTable(); 
		 JTable jtable2=new JTable();
		 JTable jtable3=new JTable();
		 jtable1.setModel(new javax.swing.table.DefaultTableModel(
					new Object[][] {
					}, new String[] { "TOKEN", "类别", "种别码", "行号" }) {
				boolean[] canEdit = new boolean[] { false, false, false, false };

				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return canEdit[columnIndex];
				}
			});
		 jtable2.setModel(new javax.swing.table.DefaultTableModel(
					new Object[][] {
					}, new String[] { "Error at Line", "说明" }) {
				boolean[] canEdit = new boolean[] { false, false };

				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return canEdit[columnIndex];
				}
			});
		 jtable3.setModel(new javax.swing.table.DefaultTableModel(
					new Object[][] {
					}, new String[] { "符号表", "POS" }) {
				boolean[] canEdit = new boolean[] { false, false };

				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return canEdit[columnIndex];
				}
			});
		String[] texts = text.split("\n");
		symbol.clear();
		symbol_pos = 0;
		for(int m = 0; m < texts.length; m++)
		{
			String str = texts[m];
			if(str.equals(""))
				continue;
			else {
				//将字符串转化为字符串数组
				char[] strline = str.toCharArray();
				for(int i = 0; i < strline.length; i++) 
				{
				    //遍历strline中的每个字符
					char ch = strline[i];
					//初始化token字符串为空
					String token = "";

                    //判断标识和关键字
					if (isAlpha(ch)) // 判断关键字和标识符  
                    {  
                        do {  
                            token += ch;  
                            i++;  
                            if(i >= strline.length) break;  
                            ch = strline[i];  
                        } while (ch != '\0' && (isAlpha(ch) || isDigit(ch)));  

                        --i; //由于指针加1,指针回退  
                        //是关键字 
                        if (isMatchKeyword(token.toString()))   
                        {  
                            DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                            tableModel.addRow(new Object[] {token, "关键字", "100", m+1});
                            jtable1.invalidate();
                        }
                        //是标识符
                        else {
                        	//如果符号表为空或符号表中不包含当前token，则加入
                        	if (symbol.isEmpty() || (!symbol.isEmpty() && !symbol.containsKey(token))) 
                        	{  
                                symbol.put(token, symbol_pos);   
                                DefaultTableModel tableModel3 = (DefaultTableModel) jtable3.getModel();
                                tableModel3.addRow(new Object[] {token, symbol_pos});
                                jtable3.invalidate();
                                symbol_pos++;
                            }
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {token, "标识符", "200", m+1});
                            jtable1.invalidate();
                        }
                        token = "";
                    }
					//判断数字常量
					else if(isDigit(ch))
					{
						//初始化进入1状态
						int state = 1;
						//声明计数变量
						int k;
                        Boolean isfloat = false;  
                        while ( (ch != '\0') && (isDigit(ch) || ch == '.' || ch == 'e' || ch == '-'))
                        {
                        	if (ch == '.' || ch == 'e')  
                              isfloat = true;
                        	  
                            for (k = 0; k <= 6; k++) 
                            {  
                                char tmpstr[] = digitDFA[state].toCharArray();  
                                if (ch != '#' && 1 == in_digitDFA(ch, tmpstr[k])) 
                                {  
                                    token += ch;  
                                    state = k;  
                                    break;  
                                }  
                            }
                            if (k > 6) break;
                            //遍历符号先前移动
                            i++;
                            if(i>=strline.length) break;  
                            ch = strline[i]; 
                        }
                        Boolean haveMistake = false;  
                        
                        if (state == 2 || state == 4 || state == 5) 
                        {  
                            haveMistake = true;  
                        } 
                        
                        else//1,3,6  
                        {  
                            if ((!isOp(ch) || ch == '.') && !isDigit(ch))  
                                haveMistake = true;  
                        }  
                        
                        //错误处理 
                        if (haveMistake)  
                        {  
                        	//一直到“可分割”的字符结束  
                        	while (ch != '\0' && ch != ',' && ch != ';' && ch != ' ')
                            {  
                                token += ch;  
                                i++;
                                if(i >= strline.length) break;  
                                ch = strline[i];  
                            }  
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token + " 确认无符号常数输入正确"});
                            jtable2.invalidate();
                        }
                        else 
                        {  
                            if (isfloat) 
                            {  
                            	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {token, "浮点型常量", "300", m+1});
                                jtable1.invalidate();    
                            } 
                            else
                            {  
                            	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {token, "整型常量", "301", m+1});
                                jtable1.invalidate();   
                            }  
                        }
                        i--;
                        token = "";
                    //判断数字常量结束     
                    }
					//识别字符常量
					else if(ch == '\'')
					{
						//初始化状态为0
						int state = 0;				        
                        //token加上’
                        token += ch;
                        
                        while (state != 3) {  
                            i++;
                            if(i >= strline.length) break;
                            ch = strline[i]; 
                            for (int k = 0; k < 4; k++) 
                            {  
                                char tmpstr[] = charDFA[state].toCharArray();  
                                if (in_charDFA(ch, tmpstr[k])) 
                                {            
                                    token += ch;
                                    state = k;  
                                    break;  
                                }  
                            }  
                        }
                        if (state != 3) {  
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token + " 字符常量引号未封闭"});
                            jtable2.invalidate();
                            i--;  
                        } 
                        else 
                        {  
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {token, "字符常量", "302", m+1});
                            jtable1.invalidate(); 
                        }     
                        token = "";
					//识别字符常量
					}
					//识别字符串常量
					else if (ch == '"')
					{
						String string = "";  
                        string += ch;  

                        int state = 0;  
                        Boolean haveMistake = false;
						
                        while (state != 3 ) {  
                            i++; 
                            
                            if(i>=strline.length-1) {  
                                haveMistake = true;  
                                break;  
                            }  
                              
                            ch = strline[i]; 
                            
                            if (ch == '\0') {  
                                haveMistake = true;  
                                break;  
                            }
                            
                            for (int k = 0; k < 4; k++) {  
                                char tmpstr[] = stringDFA[state].toCharArray();  
                                if (in_stringDFA(ch, tmpstr[k])) {  
                                    string += ch;  
                                    if (k == 2 && state == 1) {  
                                        if (isEsSt(ch)) //是转义字符  
                                            token = token + '\\' + ch;  
                                        else  
                                            token += ch;  
                                    } else if (k != 3 && k != 1)  
                                        token += ch;  
                                    state = k;  
                                    break;  
                                }  
                            }  
                        }
                        if (haveMistake) {  
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, string + " 字符串常量引号未封闭"});
                            jtable2.invalidate();  
                            --i;  
                        } else {  
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {token, "字符串常量", "303", m+1});
                            jtable1.invalidate();  
                        }  
                        token = "";		
					//识别字符串常量	
					}
					//运算符和界符
					else if (isOp(ch))   
                    {  
						token += ch; 
						//后面可以用一个"="
                        if (isPlusEqu(ch))  
                        {  
                            i++;
                            if(i>=strline.length) break;  
                            ch = strline[i];  
                            if (ch == '=')  
                                token += ch;  
                            else 
                            {  
                            	//后面可以用一个和自己一样的
                            	if (isPlusSame(strline[i - 1]) && ch == strline[i - 1])  
                                    token += ch;  
                                else  
                                    --i;   
                            }  
                        }
                        //判断是否为界符
                        if(token.length() == 1)
                        {
                        	char signal = token.charAt(0);
                        	boolean isbound = false;
                        	for(int bound = 0; bound < boundary.length; bound++)
                        	{
                        		if(signal == boundary[bound])
                        		{
                        			DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                    tableModel1.addRow(new Object[] {token, "界符", "304", m+1});
                                    jtable1.invalidate();
                                    isbound = true;
                                    break;
                        		}
                        	}
                        	if(!isbound)
                        	{
                        		DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {token, "运算符", "305", m+1});
                                jtable1.invalidate();
                        	}
                        }
                        else
                        {
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {token, "运算符", "305", m+1});
                            jtable1.invalidate();
                        }
                        
                        token = "";	
					//识别运算符	
                    }
					//识别注释//
					else if (ch == '/')
					{
						token += ch;  
                        i++;
                        if(i>=strline.length) break;  
                        ch = strline[i];
                        
						//不是多行注释及单行注释
                        if (ch != '*' && ch != '/')   
                        {  
                            if (ch == '=')  
                                token += ch; // /=  
                            else 
                            {  
                                --i; // 指针回退 // /  
                            }  
                            DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {token, "运算符", "305", m+1});
                            jtable1.invalidate();    
                            token = "";  
                        }
                        // 注释可能是‘//’也可能是‘/*’
                        else 
                        {
                        	Boolean haveMistake = false;
                        	int State = 0;
                        	if (ch == '*') 
                        	{  
                        		// ch == '*'
                        		token += ch;  
                                int state = 2;  

                                while (state != 4) 
                                {  
                                    i++;
                                    if(i>=strline.length) break;  
                                    ch = strline[i];
                                    
                                    if (ch == '\0') {  
                                        haveMistake = true;  
                                        break;  
                                    }  
                                    for (int k = 2; k <= 4; k++) {  
                                        char tmpstr[] = noteDFA[state].toCharArray();  
                                        if (in_noteDFA(ch, tmpstr[k], state)) {  
                                            token += ch;  
                                            state = k;  
                                            break;  
                                            
                                        }  
                                    }  
                                }
                                State = state;
                            //if '*'
                            }
                        	else if(ch == '/')
                        	{
                        		//单行注释读取所有字符
                        		int index = str.lastIndexOf("//");  
                                
                                String tmpstr = str.substring(index);  
                                int tmpint = tmpstr.length();  
                                for(int k=0;k<tmpint;k++)                                     
                                  i++;    
                                token = tmpstr;
                        	}
                        	if(haveMistake || State != 4)
                        	{
                        		DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                                tableModel2.addRow(new Object[] {m+1, "注释未封闭"});
                                jtable2.invalidate();  
                                --i;
                        	}
                        	else
                        	{
                        		DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {token, "注释", "306", m+1});
                                jtable1.invalidate();
                        	}
                        	token = "";
                        //为注释	
                        }	
					//识别注释	
					}
					//不合法字符
					else
                    {  
                        if(ch != ' ' && ch != '\t' && ch != '\0' && ch != '\n' && ch != '\r')  
                        {  
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, "存在不合法字符"});
                            jtable2.invalidate();
                            System.out.println(ch);
                        }  
                    }
				//遍历strline中的每个字符	
				}
			//该行文本不为空
			}
	    //遍历每行文本
		}
	//analyze()
		int i,j;
		System.out.println("TOKEN\t类别\t种别码\t行号");
		for(i=0;i<jtable1.getRowCount();i++)
			{
			for(j=0;j<jtable1.getColumnCount();j++)
			{System.out.print(jtable1.getValueAt(i, j)+"\t");}
			 System.out.println(" ");
	        }
		System.out.println(" ");
		System.out.println("错误行数\t错误原因");
		for(i=0;i<jtable2.getRowCount();i++)
		{
		for(j=0;j<jtable2.getColumnCount();j++)
		{System.out.print(jtable2.getValueAt(i, j)+"\t");}
		 System.out.println(" ");
        }
		System.out.println(" ");
		System.out.println("符号表\tPOS");
		for(i=0;i<jtable3.getRowCount();i++)
		{
		for(j=0;j<jtable3.getColumnCount();j++)
		{System.out.print(jtable3.getValueAt(i, j)+"\t");}
		 System.out.println(" ");
        }
    }

	//判断字母及下划线
	public static Boolean isAlpha(char ch)
	{
	    return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_');
	}

	public static Boolean isDigit(char ch)
	{  
        return (ch >= '0' && ch <= '9');  
    }
	//判断是否是运算符 
	public static Boolean isOp(char ch) 
    {  
        for (int i = 0; i < operator.length; i++)  
            if (ch == operator[i]) {  
                return true;  
            }  
        return false;  
    }

	public static Boolean isMatchKeyword(String str) {  
        Boolean flag = false;  
        for (int i = 0; i < keywords.length; i++) {  
            if (str.equals(keywords[i])) {  
                flag = true;  
                break;  
            }  
        }  
        return flag;  
    }
    //关键字
	public static String keywords[] = { "auto", "double", "int", "struct",  
        "break", "else", "long", "switch", "case", "enum", "register",  
        "typedef", "char", "extern", "return", "union", "const", "float",  
        "short", "unsigned", "continue", "for", "signed", "void",  
        "default", "goto", "sizeof", "volatile", "do", "if", "while",  
        "static", "main", "String"};
	 public static char operator[] = { '+', '-', '*', '=', '<', '>', '&', '|', '~',  
         '^', '!', '(', ')', '[', ']', '{', '}', '%', ';', ',', '#', '.' };
	public static char boundary[] = { ',', ';', '[', ']', '(', ')', '.', '{', '}'};
	//运算符后可加等于
	public static Boolean isPlusEqu(char ch)   
    {  
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '='  
                || ch == '>' || ch == '<' || ch == '&' || ch == '|'  
                || ch == '^';  
    }
	//可以连续两个运算符一样
	public static Boolean isPlusSame(char ch)   
    {  
        return ch == '+' || ch == '-' || ch == '&' || ch == '|';  
    }
	//a代表任意字符，b代表除\和'之外的字符
	public static String stringDFA[] = { 
		"#\\b#", 
		"##a#", 
		"#\\b\"", 
		"####" };
	public static Boolean in_stringDFA(char ch, char key) {  
        if (key == 'a')  
            return true;  
        if (key == '\\')  
            return ch == key;  
        if (key == '"')  
            return ch == key;  
        if (key == 'b')  
            return ch != '\\' && ch != '"';  
        return false;  
    }
	//a代表任意字符，b代表除\和'之外的字符
	public static String charDFA[] = { 
		"#\\b#", 
		"##a#", 
		"###\'", 
		"####" }; 
	//判断是否是转义字符
	public static Boolean isEsSt(char ch) {  
        return ch == 'a' || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r'  
                || ch == 't' || ch == 'v' || ch == '?' || ch == '0';  
    }
	public static Boolean in_charDFA(char ch, char key) {  
        if (key == 'a')  
            return true;  
        if (key == '\\')  
            return ch == key;  
        if (key == '\'')  
            return ch == key;  
        if (key == 'b')  
            return ch != '\\' && ch != '\'';  
        return false;  
    }
    //DFA of digit
	public static String digitDFA[] = { 
		"#d#####", 
		"#d.#e##", 
		"###d###", 
		"###de##",  
        "#####-d", 
        "######d", 
        "######d" };
	//判断输入符号是否符合状态机
	public static int in_digitDFA(char ch, char test) 
	{  
        if (test == 'd') {  
            if (isDigit(ch))  
                return 1;  
            else  
                return 0;  
        }  
        else
        {
        	if (ch == test)
        		return 1;
        	else
        		return 0;
        }
    }
	//多行注释DFA
	public static String noteDFA[] = { 
		"#####", 
		"##*##", 
		"##c*#", 
		"##c*/", 
		"#####" };
	
	public static Boolean in_noteDFA(char ch, char nD, int s) {  
        if (s == 2) {  
            if (nD == 'c') 
            {  
                if (ch != '*') return true;  
                else return false;  
            }  
        }  
        if (s == 3) {  
            if (nD == 'c') {  
                if (ch != '*' && ch != '/') return true;  
                else return false;  
            }  
        }  
        return (ch == nD) ? true : false;  
    }
}