import java.io.*; 
import java.net.*; 
import java.util.Scanner;
import java.nio.file.*;
public class Client  
{ 
    final static int ServerPort = 9999; 
  
    public static void main(String args[]) throws UnknownHostException, IOException  
    { 
        Scanner scanner = new Scanner(System.in); 
          
        // getting localhost ip address
        InetAddress address = InetAddress.getByName("localhost"); 
          
        // establish the connection 
        Socket socket = new Socket(address, ServerPort); 
          
        // obtaining input and out streams 
        DataInputStream input = new DataInputStream(socket.getInputStream()); 
        DataOutputStream output = new DataOutputStream(socket.getOutputStream()); 
  
        // sendMessage thread 
        Thread sendMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
                while (true) { 
   
                    System.out.println("Enter \n 1.login \n 2.signup");
                    // read the message to deliver. 
                    int option = Integer.parseInt(scanner.nextLine()); 
                      
                    try { 
                        // write on the output stream 
                        if(option==1)
                        {
                        System.out.println("Enter username and password");
                        String username=scanner.nextLine();
                        String password=scanner.nextLine();
                        output.writeUTF("Login "+username+" "+password);
                        String recv[] = input.readUTF().split(" : ");
                        System.out.println(recv[1]);
                        if(recv[0].compareTo("success")==0)
                         menu1(); 
                        }

                        else if(option==2)
                        {
                            System.out.println("Enter username and password");
                            String username=scanner.nextLine();
                            String password=scanner.nextLine();
                            output.writeUTF("Signup "+username+" "+password);
                            String recv[] = input.readUTF().split(" : ");
                            System.out.println(recv[1]);
                            if(recv[0].compareTo("success")==0)
                                signup(); 
                        }
                    }
			 catch (IOException e) { 
                        e.printStackTrace(); 
                    } 
                } 
            } 

            public void signup()
            {
                try{
                    System.out.println("Enter \n 1.list all groups \n 2.logout");
                    Integer option=Integer.parseInt(scanner.nextLine());
                    if (option==1){
                    output.writeUTF("list all groups");
                    String groups[]=input.readUTF().split("\n");

                    for(int i=0;i<groups.length;i++)
                     {   //System.out.println(groups[i]);
                        groups[i]=groups[i].split("\\.")[1];
                     }   
                        join_group(groups);
                        menu1();
                    }
                    else
                    {
                        output.writeUTF("Logout ");
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
                    Integer option=Integer.parseInt(scanner.nextLine());
                    if (option==1){
                    output.writeUTF("list all groups");
                    String groups[]=input.readUTF().split("\n");

                    for(int i=0;i<groups.length;i++)
                     {   //System.out.println(groups[i]);
                        groups[i]=groups[i].split("\\.")[1];
                     }   
                        join_group(groups);
                        menu1();
                    }
                    else if(option==2)
                    {
                        output.writeUTF("list my groups");
                        String text=input.readUTF();
                        System.out.println(text);
                        String groups[]=text.split("\n");
                        System.out.println(groups.length);
                        for(int i=0;i<groups.length;i++)
                        {   System.out.println(groups[i]); 
                        groups[i]=groups[i].split("\\.")[1];
                        }
                        //show_groups(groups);
                        menu3(groups);
                    }

                    else if(option==3)
                    {
                        output.writeUTF("Logout ");
                        System.exit(0);
                    }

                    else{
                        System.out.println("Invalid choice");
                        menu1();
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
                    Integer ch=Integer.parseInt(scanner.nextLine());
                    if(ch==n+1)
                    {output.writeUTF("Logout"); System.exit(0);}
                    else if (ch>0 && ch<=n)
                    {   output.writeUTF("join "+groups[ch-1]);
                        String rec=input.readUTF();
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
                    Integer ch=Integer.parseInt(scanner.nextLine());
                    
                    if(ch==2)
                    {
                        System.out.println("Enter group name");
                        String group=scanner.nextLine();
                        output.writeUTF("leave "+group);
                        System.out.println(input.readUTF());
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
                        String group=scanner.nextLine();
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
                        String group=scanner.nextLine();
                        output.writeUTF("show log "+group);
                        String log=input.readUTF();
                        System.out.println(log);
                        menu3(groups);
                    }
                    else if(ch==5)
                    {
                        System.out.println("Enter group name");
                        String group=scanner.nextLine();
                        System.out.println("Enter file path");
                        String fil=scanner.nextLine();
                        File file = new File(fil); 
                        Scanner sc = new Scanner(file); 
                        String content="";
                        while (sc.hasNextLine()) 
                        content+=sc.nextLine()+"\n";
                        output.writeUTF("upload file "+group);
                        output.writeUTF(fil);
                        System.out.println(content);
                        output.writeUTF(content);
                        System.out.println(input.readUTF()); 
                        menu3(groups);
                    }
                    else if(ch==6)
                    {
                        System.out.println("Enter group name");
                        String group=scanner.nextLine();
                        String files[]=show_files(group).split(" ");
                        for(int i=0;i<files.length;i++)
                        files[i]=files[i].split(".")[1]; 
                        System.out.println("Enter file name");
                        String fil=scanner.nextLine();
                        output.writeUTF("give "+group);
                        output.writeUTF(fil);
                        String data=input.readUTF();
                        
                        Files.write(Paths.get("./download/"+fil), data.getBytes());
                        menu3(groups);
                    }
                    else if(ch==4)
                    {
                        System.out.println("Enter group name");
                        String group=scanner.nextLine();
                        output.writeUTF("show messages "+group);
                        System.out.println(input.readUTF());
                        menu3(groups);
                    }
                    else if(ch==3)
                    {
                        System.out.println("Enter group name");
                        String group=scanner.nextLine();
                        System.out.println("Enter a messge");
                        output.writeUTF("write "+group);
                        output.writeUTF(scanner.nextLine());
                        System.out.println(input.readUTF());
                        menu3(groups);
                    }
                    else if(ch==10)
                    {
                        menu1();
                    }
                    else if(ch==9)
                    {
                        System.out.println("Enter group name");
                        String group=scanner.nextLine();
                        String files[]=show_files(group).split("\n");
                        int len=files.length;
                        for(int i=0;i<len;i++)
                        {
                        System.out.println((i+1)+" "+files[i]);
                        }
                        System.out.println((len+1)+" Go Back");
                        System.out.println("select a file to share");
                        ch=Integer.parseInt(scanner.nextLine());
                        if(ch==len+1)
                        menu3(groups);
                        else if (ch>0 && ch<=len)
                        {   int split=-1;
                            for(int i=0,j=1;i<n;i++)
                                if(groups[i].compareTo(group)!=0)
                                System.out.println((j++)+" "+groups[i]);
                                else
                                split=i;
                            System.out.println((n)+" Go Back");
                            System.out.println("select group to share a file");
                            Integer ch1=Integer.parseInt(scanner.nextLine())-1;
                            if(ch1>=0 && ch1<n-1)
                            {output.writeUTF("share "+group);
                             output.writeUTF(files[ch-1]);
                            if(ch1<=split)
                            output.writeUTF(groups[ch1]);
                            else
                            output.writeUTF(groups[ch1-1]);    
                            System.out.println(input.readUTF());
                            menu3(groups);
                            }
                            else if(ch1==(n-1))
                            menu3(groups);
                            else
                            {System.out.println("Invalid choice"); menu3(groups);}
                            
                        }
                    }

                    else{
                        System.out.println("Invalid choice");
                        menu3(groups);
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
                    Integer ch=Integer.parseInt(scanner.nextLine());
                    if(ch==n+1)
                    output.writeUTF("Logout");
                    else if (ch>0 && ch<=n)
                    {   //output.writeUTF("select "+groups[ch]);
                        //String res[]=input.readUTF().split(":");
                        //System.out.println(res[1]);
                        menu3(groups);
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
                    output.writeUTF("show files "+group);
                    String str=input.readUTF();
                    System.out.println(str);
                    return str;
                }
                catch(Exception e)
                {e.printStackTrace();}
                return null;
            }




        }); 
          
        // readMessage thread 
        
        sendMessage.start(); 
        
    } 
} 