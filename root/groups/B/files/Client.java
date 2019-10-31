import java.io.*; 
import java.net.*; 
import java.util.Scanner;
import java.nio.file.*;
public class Client  
{ 
    final static int ServerPort = 1234; 
  
    public static void main(String args[]) throws UnknownHostException, IOException  
    { 
        Scanner scn = new Scanner(System.in); 
          
        // getting localhost ip 
        InetAddress ip = InetAddress.getByName("localhost"); 
          
        // establish the connection 
        Socket s = new Socket(ip, ServerPort); 
          
        // obtaining input and out streams 
        DataInputStream dis = new DataInputStream(s.getInputStream()); 
        DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
  
        // sendMessage thread 
        Thread sendMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
                while (true) { 
   
                    System.out.println("Enter \n 1.login \n 2.signup");
                    // read the message to deliver. 
                    int msg = Integer.parseInt(scn.nextLine()); 
                      
                    try { 
                        // write on the output stream 
                        if(msg==1)
                        {
                        System.out.println("Enter username and password");
                        String username=scn.nextLine();
                        String password=scn.nextLine();
                        dos.writeUTF("Login "+username+" "+password);
                        String reply[] = dis.readUTF().split(" : ");
                        System.out.println(reply[1]);
                        if(reply[0].compareTo("success")==0)
                         menu1(); 
                        }
                        else if(msg==2)
                        {
                            System.out.println("Enter username and password");
                            String username=scn.nextLine();
                            String password=scn.nextLine();
                            dos.writeUTF("Signup "+username+" "+password);
                            String reply[] = dis.readUTF().split(" : ");
                            System.out.println(reply[1]);
                            if(reply[0].compareTo("success")==0)
                                signup(); 
                        }
                        
                        
                    } catch (IOException e) { 
                        e.printStackTrace(); 
                    } 
                } 
            } 

            public void signup()
            {
                try{
                    System.out.println("Enter \n 1.list all groups \n 2.logout");
                    Integer msg=Integer.parseInt(scn.nextLine());
                    if (msg==1){
                    dos.writeUTF("list all groups");
                    String groups[]=dis.readUTF().split("\n");

                    for(int i=0;i<groups.length;i++)
                     {   //System.out.println(groups[i]);
                        groups[i]=groups[i].split("\\.")[1];
                     }   
                        join_group(groups);
                        menu1();
                    }
                    else
                    {
                        dos.writeUTF("Logout ");
                        System.exit(0);
                    }
                    
                }

                catch(Exception e)
                {e.printStackTrace();}

            }

            public void menu1()
            {
                try{
                    System.out.println("Enter \n 1.list all groups \n 2.list your groups \n 3.logout");
                    Integer msg=Integer.parseInt(scn.nextLine());
                    if (msg==1){
                    dos.writeUTF("list all groups");
                    String groups[]=dis.readUTF().split("\n");

                    for(int i=0;i<groups.length;i++)
                     {   //System.out.println(groups[i]);
                        groups[i]=groups[i].split("\\.")[1];
                     }   
                        join_group(groups);
                        menu1();
                    }
                    else if(msg==2)
                    {
                        dos.writeUTF("list my groups");
                        String text=dis.readUTF();
                        System.out.println(text);
                        String groups[]=text.split("\n");
                        System.out.println(groups.length);
                        for(int i=0;i<groups.length;i++)
                        {  // System.out.println(groups[i]); 
                        groups[i]=groups[i].split("\\.")[1];
                        }
                        //show_groups(groups);
                        menu3(groups);
                    }
                    else
                    {
                        dos.writeUTF("Logout ");
                        System.exit(0);
                    }
                }

                catch(Exception e)
                {e.printStackTrace();}

            }

            public void join_group(String groups[])
            {
                try{
                    int n=groups.length;
                    System.out.println("Select a number to join a group");
                    for(int i=0;i<n;i++)
                    System.out.println((i+1)+" "+groups[i]);
                    System.out.println((n+1)+" logout");
                    System.out.println((n+2)+" Go Back");
                    Integer ch=Integer.parseInt(scn.nextLine());
                    if(ch==n+1)
                    {dos.writeUTF("Logout"); System.exit(0);}
                    else if (ch>0 && ch<=n)
                    {   dos.writeUTF("join "+groups[ch-1]);
                        String rec=dis.readUTF();
                        String res[]=rec.split(":");
                        System.out.println(rec);
                        if(res[0].compareTo("success")==0)
                            System.out.println(res[1]);
                        else
                        {        
                            System.out.println(res[1]);
                            System.out.println("try again");
                            join_group(groups);
                        }   
                        
                    }
                    else if(ch==n+2)
                    menu1();
                    else
                    {
                        System.out.println("invalid choice, try again");
                        join_group(groups);
                    }
                }
                catch(Exception e)
                {e.printStackTrace();}
            }

            public void menu3(String groups[])
            {
                try{
                    int n=groups.length;
                    System.out.println("1.Group List");
                    for(int i=0;i<n;i++)
                    System.out.println(groups[i]);
                    System.out.println("2.leave group");
                    System.out.println("3.Send Messsage");
                    System.out.println("4.show all messages");
                    System.out.println("5.Upload");
                    System.out.println("6.Download");
                    System.out.println("7.Show all files");
                    System.out.println("8.Show log");
                    System.out.println("9.Share file");
                    System.out.println("10. GO back");
                    Integer ch=Integer.parseInt(scn.nextLine());
                    
                    if(ch==2)
                    {
                        System.out.println("Enter group name");
                        String group=scn.nextLine();
                        dos.writeUTF("leave "+group);
                        System.out.println(dis.readUTF().split(":")[1]);
                        String n_groups[]=new String[n-1];
                        for(int i=0,j=0;i<n;i++)
                        {
                            if(groups[i].compareTo(group)!=0)
                            {n_groups[j]=groups[i]; j++;}
                        }
                        menu3(n_groups);
                    }
                    else if(ch==7)
                    {
                        System.out.println("Enter group name");
                        String group=scn.nextLine();
                        String files[]=show_files(group).split("\n");
                        for(int i=0;i<files.length;i++)
                        {
                          System.out.println((i+1)+" "+files[i]);
                        }
                        menu3(groups);
                    }
                    else if(ch==8)
                    {
                        System.out.println("Enter group name");
                        String group=scn.nextLine();
                        dos.writeUTF("show log "+group);
                        String log=dis.readUTF();
                        System.out.println(log);
                        menu3(groups);
                    }
                    else if(ch==5)
                    {
                        System.out.println("Enter group name");
                        String group=scn.nextLine();
                        System.out.println("Enter file path");
                        String fil=scn.nextLine();
                        File file = new File(fil); 
                        Scanner sc = new Scanner(file); 
                        String content="";
                        while (sc.hasNextLine()) 
                        content+=sc.nextLine()+"\n";
                        dos.writeUTF("upload file "+group);
                        dos.writeUTF(fil);
                        System.out.println(content);
                        dos.writeUTF(content);
                        System.out.println(dis.readUTF()); 
                        menu3(groups);
                    }
                    else if(ch==6)
                    {
                        System.out.println("Enter group name");
                        String group=scn.nextLine();
                        String files[]=show_files(group).split(" ");
                        for(int i=0;i<files.length;i++)
                        files[i]=files[i].split(".")[1]; 
                        System.out.println("Enter file name");
                        String fil=scn.nextLine();
                        dos.writeUTF("give "+group);
                        dos.writeUTF(fil);
                        String data=dis.readUTF();
                        
                        Files.write(Paths.get("./download/"+fil), data.getBytes());
                        menu3(groups);
                    }
                    else if(ch==4)
                    {
                        System.out.println("Enter group name");
                        String group=scn.nextLine();
                        dos.writeUTF("show messages "+group);
                        System.out.println(dis.readUTF());
                        menu3(groups);
                    }
                    else if(ch==3)
                    {
                        System.out.println("Enter group name");
                        String group=scn.nextLine();
                        System.out.println("Enter a messge");
                        dos.writeUTF("write "+group);
                        dos.writeUTF(scn.nextLine());
                        System.out.println(dis.readUTF());
                        menu3(groups);
                    }
                    else if(ch==10)
                    {
                        menu1();
                        /*
                        String files[]=show_files(group).split("\n");
                        int len=files.length;
                        for(int i=0;i<len;i++)
                        {
                        System.out.println((i+1)+" "+files[i]);
                        }
                        System.out.println((len+1)+" Go Back");
                        System.out.println("select a file to share");
                        ch=Integer.parseInt(scn.nextLine());
                        if(ch==len+1)
                        menu2(group,groups);
                        else if (ch>0 && ch<=len)
                        {   int split=-1;
                            for(int i=0,j=1;i<n;i++)
                                if(groups[i].compareTo(group)!=0)
                                System.out.println((j++)+" "+groups[i]);
                                else
                                split=i;
                            System.out.println((n)+" Go Back");
                            System.out.println("select group to share a file");
                            Integer ch1=Integer.parseInt(scn.nextLine())-1;
                            if(ch1>=0 && ch1<n-1)
                            {dos.writeUTF("share "+group);
                            dos.writeUTF(files[ch-1]);
                            if(ch1<split)
                            dos.writeUTF(groups[ch1]);
                            else
                            dos.writeUTF(groups[ch1-1]);    
                            System.out.println(dis.readUTF());
                            menu2(group,groups);
                            }
                            else if(ch1==(n+1))
                            menu2(group,groups);
                            else
                            {System.out.println("Invalid choice"); menu2(group,groups);}
                            
                        }
                        */
                    }


                }
                catch(Exception e)
                {e.printStackTrace();}
            }


            public void show_groups(String groups[])
            {
                try{
                    int n=groups.length;
                    System.out.println("select a group to enter");
                    for(int i=0;i<n;i++)
                    System.out.println((i+1)+" "+groups[i]);
                    System.out.println((n+1)+" logout");
                    System.out.println((n+2)+" GO back");
                    Integer ch=Integer.parseInt(scn.nextLine());
                    if(ch==n+1)
                    dos.writeUTF("Logout");
                    else if (ch>0 && ch<=n)
                    {   //dos.writeUTF("select "+groups[ch]);
                        //String res[]=dis.readUTF().split(":");
                        //System.out.println(res[1]);
                        menu2(groups[ch-1],groups);
                    }
                    else if(ch==n+2)
                    menu1();
                    else
                    {System.out.println("Invalid choice.. try again.."); show_groups(groups);}
                }
                catch(Exception e)
                {e.printStackTrace();}
            }

            public String show_files(String group)
            {
                try{
                    dos.writeUTF("show files "+group);
                    String str=dis.readUTF();
                    System.out.println(str);
                    return str;
                }
                catch(Exception e)
                {e.printStackTrace();}
                return null;
            }


            public void menu2(String group,String groups[])
            {
                try{
                    int n=groups.length;


                    System.out.println("Enter");
                    System.out.println("1.Leave group");
                    System.out.println("2.Display all Files");
                    System.out.println("3.Show Log");
                    System.out.println("4.Upload File");
                    System.out.println("5.Download File");
                    System.out.println("6.Show all messages");
                    System.out.println("7.write a message");
                    System.out.println("8.Go Back");
                    System.out.println("9.Share a File to another group");

                    Integer ch=Integer.parseInt(scn.nextLine());
                    if(ch==1)
                    {
                        dos.writeUTF("leave "+group);
                        System.out.println(dis.readUTF().split(":")[1]);
                        String n_groups[]=new String[n-1];
                        for(int i=0,j=0;i<n;i++)
                        {
                            if(groups[i].compareTo(group)!=0)
                            {n_groups[j]=groups[i]; j++;}
                        }
                        show_groups(n_groups);
                    }
                    else if(ch==8)
                        show_groups(groups);
                    else if(ch==2)
                    {
                        String files[]=show_files(group).split("\n");
                        for(int i=0;i<files.length;i++)
                        {
                          System.out.println((i+1)+" "+files[i]);
                        }
                        menu2(group,groups);
                    }
                    else if(ch==3)
                    {
                        dos.writeUTF("show log "+group);
                        String log=dis.readUTF();
                        System.out.println(log);
                        menu2(group,groups);
                    }
                    else if(ch==4)
                    {
                        System.out.println("Enter file path");
                        String fil=scn.nextLine();
                        File file = new File(fil); 
                        Scanner sc = new Scanner(file); 
                        String content="";
                        while (sc.hasNextLine()) 
                        content+=sc.nextLine()+"\n";
                        dos.writeUTF("upload file "+group);
                        dos.writeUTF(fil);
                        System.out.println(content);
                        dos.writeUTF(content);
                        System.out.println(dis.readUTF()); 
                        menu2(group,groups);
                    }
                    else if(ch==5)
                    {
                        String files[]=show_files(group).split(" ");
                        for(int i=0;i<files.length;i++)
                        files[i]=files[i].split(".")[1]; 
                        System.out.println("Enter file name");
                        String fil=scn.nextLine();
                        dos.writeUTF("give "+group);
                        dos.writeUTF(fil);
                        String data=dis.readUTF();
                        
                        Files.write(Paths.get("./download/"+fil), data.getBytes());
                        menu2(group,groups);
                    }
                    else if(ch==6)
                    {
                        dos.writeUTF("show messages "+group);
                        System.out.println(dis.readUTF());
                        menu2(group,groups);
                    }
                    else if(ch==7)
                    {
                        System.out.println("Enter a messge");
                        dos.writeUTF("write "+group);
                        dos.writeUTF(scn.nextLine());
                        System.out.println(dis.readUTF());
                        menu2(group,groups);
                    }
                    else 
                    {
                        String files[]=show_files(group).split("\n");
                        int len=files.length;
                        for(int i=0;i<len;i++)
                        {
                        System.out.println((i+1)+" "+files[i]);
                        }
                        System.out.println((len+1)+" Go Back");
                        System.out.println("select a file to share");
                        ch=Integer.parseInt(scn.nextLine());
                        if(ch==len+1)
                        menu2(group,groups);
                        else if (ch>0 && ch<=len)
                        {   int split=-1;
                            for(int i=0,j=1;i<n;i++)
                                if(groups[i].compareTo(group)!=0)
                                System.out.println((j++)+" "+groups[i]);
                                else
                                split=i;
                            System.out.println((n)+" Go Back");
                            System.out.println("select group to share a file");
                            Integer ch1=Integer.parseInt(scn.nextLine())-1;
                            if(ch1>=0 && ch1<n-1)
                            {dos.writeUTF("share "+group);
                            dos.writeUTF(files[ch-1]);
                            if(ch1<split)
                            dos.writeUTF(groups[ch1]);
                            else
                            dos.writeUTF(groups[ch1-1]);    
                            System.out.println(dis.readUTF());
                            menu2(group,groups);
                            }
                            else if(ch1==(n+1))
                            menu2(group,groups);
                            else
                            {System.out.println("Invalid choice"); menu2(group,groups);}
                            
                        }
                    }
                }
                catch(Exception e)
                {e.printStackTrace();}
            }



        }); 
          
        // readMessage thread 
        
        sendMessage.start(); 
        
    } 
} 
