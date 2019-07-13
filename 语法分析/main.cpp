#include <iostream>
#include <fstream>
#include <stack>
#include <cstdio>
#include <cstring>
using namespace std;
//��ջ
stack<int> state_stack;
stack<char> sign_stack;

//��ʱ���ķ��Ķ�ά����
char WenFa[300][300];
int length[500];         //�ķ��ĳ���
int number;              //�ķ��ĸ���
bool huancun[300];           //���뻺��
char Notend[300];            //���ս����
int size_vn=0;
char end0[300];            //�ս����
int size_vt=0;
bool firstSet[300][200];    //firstSet��
char buffer[500];
int size=0;

//��Ŀ��ת����
struct ChangeTable
{
	int begin;
	int next;
	char ch;
};
ChangeTable trans[6000];
int size_trans=0;

 //��Ŀ��
struct ProSet
{
	int num;
	int now;
	char search;
};
ProSet items[900][500];
int count=0;
int size_item[900];

 //������
struct AGTable
{
	char ch;
	int next_state;
};
AGTable action_table[900][500];
int size_act_table[900];

//�ļ�����
ifstream grammar_file;
ifstream input_file;
ofstream items_file;
ofstream action_file;
ofstream firstset_file;
ofstream procedure_file;
ofstream reduce_file;
//���ķ�
void readGrammar()
{
	char nouse,temp;
	int i,j=0;
	WenFa[0][0]='Z';
	length[0]=2;
	grammar_file>>number;
	for(i=1;i<=number;i++)
	{
		j=0;
		grammar_file>>temp;
		grammar_file>>nouse>>nouse;
		//cout<<temp<<nouse<<nouse<<endl;
		while(temp!='$')
		{
			WenFa[i][j++]=temp;
			huancun[temp]=true;
			grammar_file>>temp;
		}
		//cout<<WenFa[i]<<endl;
		length[i]=j;
	}
	WenFa[0][1]=WenFa[1][0];

	 //��ASCII��ȷ���ս�����ͷ��ս����
	for(i=32;i<64;i++)
	{
		if(huancun[i])
		{
			end0[size_vt++]=i;//�����
		}
	}
	for(i=65;i<91;i++)
	{
	if(huancun[i])
	{
		Notend[size_vn++]=i;//��д��ĸ
		}
	}
	for(i=91;i<128;i++)
	{
		if(huancun[i])
		{
		 	end0[size_vt++]=i;//��ʶ��������
		}
	}
	/*for(i=0;i<60;i++)
	{
		for(j=0;j<30;j++)
		{
			printf("%c",WenFa[i][j]);
		}
		printf("\n");
	}*/
}

//�ж��ַ��Ƿ��ڷ��ս���ַ�����
bool IsinVn(char a)
{
	for(int i=0;i<size_vn;i++)
	{
		if(Notend[i]==a)
		{
			return true;
		}
	}
	return false;
}

//����firstSet��
void getFirst()
{
	bool done=true;    //�����ܸĵĵط� �Ͳ�ͣ����
	int t,k;
	bool isempty;
	while(done)
	{
		done=false;
		for(int i=0;i<=number;i++)
		{
			t=1;
			isempty=true;
			while(isempty && t<length[i])
			{
				isempty=false;
				if(WenFa[i][t]>=65 && WenFa[i][t]<=90)    //�����һ���Ƿ��ս�����Ͱ�����firstSet��д���Լ���
				{
					for(k=32;k<=64;k++)
					{
						if(firstSet[WenFa[i][t]][k]==true && !firstSet[WenFa[i][0]][k])
						{
							firstSet[WenFa[i][0]][k]=true;
							done=true;
						}
					}
					for(k=91;k<128;k++)
					{
						if(firstSet[WenFa[i][t]][k]==true && firstSet[WenFa[i][0]][k]==false)
						{
							firstSet[WenFa[i][0]][k]=true;
							done=true;
						}
					}
				}
				else if(firstSet[WenFa[i][0]][WenFa[i][t]]==false)  //��һ���Ƿ��ս����д��firstSet��
				{
					firstSet[WenFa[i][0]][WenFa[i][t]]=true;
					done=true;
				}
			}
		}
	}
}

//��firstSet��д��firstSet.txt�ļ�
void writeFirst()
{
	for(int i=64;i<91;i++)
	{
		char ch=char(i);
		if(IsinVn(ch))
		{
			firstset_file<<"first("<<ch<<")={";
			for(int j=32;j<128;j++)
			{
				if(firstSet[i][j]==true)
				{
					firstset_file<<char(j)<<",";
				}
			}
			firstset_file<<"}"<<endl;
		}
	}
}

//�õ���ǰ������
void getSearch(ProSet temp)
{
	size=0;
	bool flag=true;
	int nownow=temp.now;
	int i;
	while(flag==true)
	{
		flag=false;
		if(nownow+1>=length[temp.num])
		{
			buffer[size++]=temp.search;
			return;
		}
		else if(WenFa[temp.num][nownow+1]<'A' || WenFa[temp.num][nownow+1]>'Z')
		{
			buffer[size++]=WenFa[temp.num][nownow+1];
			return;
		}
		else if(WenFa[temp.num][nownow+1]>='A' && WenFa[temp.num][nownow+1]<='Z')
		{
			for(i=32;i<64;i++)
			{
				if(firstSet[WenFa[temp.num][nownow+1]][i]==true)
				buffer[size++]=i;
			}
			for(i=91;i<128;i++)
			{
				if(firstSet[WenFa[temp.num][nownow+1]][i]==true)
				{
					buffer[size++] = i;
				}
			}
			if(firstSet[WenFa[temp.num][nownow+1]][64]==true)
			{
				nownow++;
				flag=true;
			}
		}
	}
}

//�жϵ�ǰ��Ŀ��Ԫ���Ƿ��ظ�
bool isInClosure(ProSet temp,int K)
{
	int i;
	for(i=0;i<size_item[K];i++)
	{
		if(items[K][i].num==temp.num && items[K][i].now==temp.now && items[K][i].search==temp.search)
		{
			return true;
		}
	}
	return false;
}

//����Ŀ����
void Closure(int K)
{
	ProSet temp;
	int i,j,k;
	for(i=0;i<size_item[K];i++)
	{
		if(WenFa[items[K][i].num][items[K][i].now]>='A' && WenFa[items[K][i].num][items[K][i].now]<='Z')
		{
			for(j=0;j<500;j++)
			{
				size=0;
				if(WenFa[j][0]==WenFa[items[K][i].num][items[K][i].now])//�Է��ս����ʼ
				{
					getSearch(items[K][i]);
					for(k=0;k<size;k++)
					{
						temp.num=j;
						temp.now=1;
						temp.search=buffer[k];
						if(!isInClosure(temp,K))
						{
							items[K][size_item[K]++]=temp;
						}
					}
				}
			}
		}
	}
	return;
}

//��ǰ��Ŀ������ǰ�ıȽ�
int isContained()
{
	int i;
	int sum=0;
	int j;
	int k;
	for(i=0;i<count;i++)
	{
		sum=0;
		if(size_item[count]==size_item[i])
		{
			for(j=0;j<size_item[count];j++)
			{
				for(k=0;k<size_item[i];k++)
				{
					if(items[i][k].num==items[count][j].num && items[i][k].now==items[count][j].now && items[i][k].search==items[count][j].search)
					{
						sum++;
						break;
					}
				}
			}
		}
		if(sum==size_item[count])
		{
			return i;
		}
	}
	return 0;
}

//������Ŀ��
void makeItem()
{
	items[0][0].num=0;
	items[0][0].now=1;
	items[0][0].search='$';
	size_item[0]=1;
	Closure(0);
	ProSet buf[100];
	int buf_size=0;
	ProSet tp;
	int i,j,k;
	int nextt_state;
	items_file<<"I0:"<<endl;
	for(i=0;i<size_item[0];i++)
	{
		items_file<<WenFa[items[0][i].num][0]<<"->"<<WenFa[items[0][i].num]+1<<" , "<<items[0][i].now<<" , "<<items[0][i].search<<endl;
	}
	items_file<<"--------------------------------------------------"<<endl;
	int index;
	int p;
	int t;
	for(index=0;index<count+1;index++)
	{
		//ɨ�����һλ���ս�������µ���Ŀ��
		for(j=0;j<size_vt;j++)
		{
			buf_size=0;
			for(p=0;p<size_item[index];p++)
			{
				if(items[index][p].now<length[items[index][p].num] && WenFa[items[index][p].num][items[index][p].now]==end0[j])
				{
					tp.num=items[index][p].num;
					tp.search=items[index][p].search;
					tp.now=items[index][p].now+1;
					buf[buf_size++]=tp;
				}
			}
			if(count==899) break;
			//����һ���µ���Ŀ��
			if(buf_size!= 0)
			{
				count++;
				for(t=0;t<buf_size;t++)
				{
					items[count][size_item[count]++]=buf[t];
				}
				Closure(count);
				nextt_state = isContained();
				if(nextt_state==0)                 //����count����Ŀ���Ƿ��ظ�
				{
					items_file<<"I"<<count<<":"<<endl;
					for(i=0;i<size_item[count];i++)
					{
						items_file<<WenFa[items[count][i].num][0]<<"->"<<WenFa[items[count][i].num]+1<<" , "<<items[count][i].now<<" , "<<items[count][i].search<<endl;
					}
					items_file<<"--------------------------------------------------"<<endl;
					trans[size_trans].begin=index;
					trans[size_trans].next=count;
					trans[size_trans++].ch=end0[j];
				}
				else
				{
					size_item[count--]=0;
					trans[size_trans].begin=index;
					trans[size_trans].next=nextt_state;
					trans[size_trans++].ch=end0[j];
				}
			}
		}
		//������Ϊ�Ƿ��ս������������Ŀ��
		for(j=0;j<size_vn;j++)
		{
			buf_size = 0;
			for(p=0;p<size_item[index];p++)
			{
				if(items[index][p].now<length[items[index][p].num] && WenFa[items[index][p].num][items[index][p].now]==Notend[j])
				{
					tp.num=items[index][p].num;
					tp.search=items[index][p].search;
					tp.now=items[index][p].now+1;
					buf[buf_size++]=tp;
				}
			}
			if(count==899) break;
			if(buf_size!=0)
			{
				count++;
				for(t=0;t<buf_size;t++)
				{
					items[count][size_item[count]++]=buf[t];
				}
				Closure(count);
				nextt_state = isContained();
				if(nextt_state==0)
				{

					items_file<<"I"<<count<<":"<<endl;
					for(i=0;i<size_item[count];i++)
					{
						items_file<<WenFa[items[count][i].num][0]<<"->"<<WenFa[items[count][i].num]+1<<" , "<<items[count][i].now<<" , "<<items[count][i].search<<endl;
					}
					items_file<<"--------------------------------------------------"<<endl;
					trans[size_trans].begin=index;
					trans[size_trans].next=count;
					trans[size_trans++].ch=Notend[j];
				}
				else
				{
					size_item[count--]=0;
					trans[size_trans].begin=index;
					trans[size_trans].next=nextt_state;
					trans[size_trans++].ch=Notend[j];
				}
			}
		}
	}
}

//����������
void getAction()
{
	int i,j,k,flag;
	int t1,t2,t;
	char tp;
	for(i=0;i<=count;i++)
	{
		for(j=0;j<size_item[i];j++)
		{
			if(items[i][j].now==length[items[i][j].num] || (items[i][j].now==1 && length[items[i][j].num]==2 && WenFa[items[i][j].num][1]=='@'))
			{
				action_table[i][size_act_table[i]].ch=items[i][j].search;
				action_table[i][size_act_table[i]++].next_state=items[i][j].num*(-1);
			}
		}
	}
	for(i=0;i<size_trans;i++)
	{
		t1=trans[i].begin;
		t2=trans[i].next;
		tp=trans[i].ch;
		action_table[t1][size_act_table[t1]].ch=tp;
		action_table[t1][size_act_table[t1]++].next_state=t2;
	}
	action_file <<"״̬       ";
	for(i = 0; i < size_vt; i++)
	{
		action_file << end0[i] <<"         ";
	}
	for (i = 0; i < size_vn; i++)
	{
		action_file << Notend[i] <<"         ";
	}
	action_file<<endl;
	for(i = 0; i <= count; i++)
    {
		action_file<<i<<"         ";
		for(k = 0;k < size_vt; k++)
		{
			flag = 0;
			for(j = 0; j < size_act_table[i]; j++)
	    	{
				if (flag==1) break;
	        	tp=action_table[i][j].ch;
	        	t=action_table[i][j].next_state;
				if(end0[k]==tp)
				{
					if(t<0)
					{
						t = 0 - t;
						if(t<10)  action_file<<"r"<<t<<"        ";
						else if(t<100)  action_file<<"r"<<t<<"       ";
						else  action_file<<"r"<<t<<"      ";
					}
					else if(t>0)
					{
						if(t<10)  action_file<<"s"<<t<<"        ";
						else if(t<100)  action_file<<"s"<<t<<"       ";
						else  action_file<<"s"<<t<<"      ";
					}
					flag = 1;
				}
				else if(j==size_act_table[i]-1) action_file<<"          ";
	    	}

		}
		for(k = 0;k < size_vn; k++)
		{
			flag = 0;
			for(j = 0; j < size_act_table[i]; j++)
	    	{
				if (flag==1) break;
	        	tp=action_table[i][j].ch;
	        	t=action_table[i][j].next_state;
				if(Notend[k]==tp)
				{
					if(t<10)  action_file<<t<<"         ";
					else if(t<100)  action_file<<t<<"        ";
					else  action_file<<t<<"       ";
					flag = 1;
				}
				else if(j==size_act_table[i]-1) action_file<<"          ";
			}
		}
		action_file << endl;
    }
}

//����ǰ״̬�ͷ���д��ջ
void writeStack(int x)
{
	stack<int> a;
	stack<char>c;
	if(x==1)    //״̬
	{
		while(state_stack.empty()==false)
		{
			a.push(state_stack.top());
			state_stack.pop();
		}
		while(a.empty()==false)
		{
			procedure_file<<a.top()<<',';
			state_stack.push(a.top());
			a.pop();
		}
	}
	if(x==2)   //����
	{
		while(sign_stack.empty()==false)
		{
			c.push(sign_stack.top());
			sign_stack.pop();
		}
		while(c.empty()==false)
		{
			procedure_file<<c.top()<<',';
			sign_stack.push(c.top());
			c.pop();
		}
	}
}

//�жϾ����Ƿ��Ǹ��ķ������
void Judge()
{
	size = 0;
	while(input_file>>buffer[size] && buffer[size++] != '$');
    int    work_sta = 0;
    int    index_buf = 0;
    bool   error=false;
    bool   done = false;
    char now_in;
    now_in=buffer[0];

    state_stack.push(0);
    sign_stack.push('$');
    procedure_file<<"״̬ջ                       ����ջ                        ���봮                     ACChangeTableION            "<<endl;
    int i,j,k,m;
    int tp,len;
    int aa;
    while(done == false && error == false)
    {
        work_sta = state_stack.top();
        writeStack(1);
        procedure_file<<"                     ";
        writeStack(2);
        procedure_file<<"                     ";
        for(i=index_buf;i<size;i++)
        {
        	procedure_file<<buffer[i];
        }
        procedure_file<<"                    ";
        error =  true;
        for(i= 0; i < size_act_table[work_sta];i++)
		{
            if(action_table[work_sta][i].ch == now_in)
            {
                error = false;
                if(action_table[work_sta][i].next_state == 0)
                {
                    procedure_file << "YES" << endl;
                    cout<<"YES"<<endl;
                    done = true;
                    break;
                }
                else if(action_table[work_sta][i].next_state > 0)   //�ƽ�
                {
                    procedure_file<<'s'<<action_table[work_sta][i].next_state<<endl;
                    state_stack.push(action_table[work_sta][i].next_state);
                    sign_stack.push(action_table[work_sta][i].ch);
                    index_buf++;
                    now_in=buffer[index_buf];
                    break;
                }
                else if(action_table[work_sta][i].next_state < 0)   //��Լ
                {
                    tp = action_table[work_sta][i].next_state*(-1);
                    procedure_file<<'r'<<tp<<endl;
                    reduce_file<<WenFa[tp][0]<<"->";
                    int outlen=1;
                    //cout<<length[tp];
                    while(outlen<=length[tp]-1)
                    {reduce_file<<WenFa[tp][outlen];outlen++;}
                    reduce_file<<endl;
                    len=length[tp]-1;
                    if(WenFa[tp][1]=='@')
                    {
                    	len--;
                    }
                    for(k = 0; k < len; k++)
                    {
                       	state_stack.pop();
                       	sign_stack.pop();
                    }
                    sign_stack.push(WenFa[tp][0]);
                    aa=state_stack.top();
                    for(m=0;m<size_act_table[aa];++m)
                    {
                        if(action_table[aa][m].ch==WenFa[tp][0])
                        {
                        	state_stack.push(action_table[aa][m].next_state);
                        }
                    }
                    break;
                }
			}
        }
    }
    if(!done)
    {
        cout << "NO" << endl;
        cout<<"����ԭ�������δ�ҵ���";
        for(i=0;i<size_act_table[work_sta];i++)
        {
        	cout<<action_table[work_sta][i].ch<<" ";
        }
        cout<<endl;
    }
}

//��ʼ������
void init()
{
	int i,j;
	for(i=0;i<500;i++)
	{
		trans[i].begin = 0;
		trans[i].next = 0;
		trans[i].ch = '\0';
		size_act_table[i]=0;
	}
	for(i=0;i<500;i++)
	{
		for(j=0;j<500;j++)
		{
			items[i][j].num = 0;
			items[i][j].now = 0;
			items[i][j].search = '\0';
			action_table[i][j].ch = '\0';
			action_table[i][j].next_state = 0;
		}
	}
	size=0;
}

void openFile()
{
//	char xx;
	grammar_file.open("�﷨����_�ķ�.txt");
	//grammar_file>>xx;
	//cout<<xx<<endl;
//	grammar_file>>xx;
//	cout<<xx<<endl;
//	grammar_file>>xx;
//	cout<<xx<<endl;
    input_file.open("output.txt");
    items_file.open("InSet.txt");
    action_file.open("������.txt");
    firstset_file.open("first��.txt");
    procedure_file.open("��������.txt");
    reduce_file.open("��Լ.txt");
}

void LR1()
{
	readGrammar();
    getFirst();
    writeFirst();
    makeItem();
    getAction();
    Judge();
}

void closeFile()
{
	grammar_file.close();
    input_file.close();
    items_file.close();
    action_file.close();
    firstset_file.close();
    procedure_file.close();
    reduce_file.close();
}

int main()
{
    init();
    openFile();
    LR1();
    closeFile();
    return 0;
}
