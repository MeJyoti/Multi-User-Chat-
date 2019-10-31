import java.io.*; 
import java.util.*; 
import java.net.*; 
import java.nio.file.*;  
// Server class 
public class Server  
{ 
  
    // Vector to store active clients 
    static Vector<ClientHandler> activeClient = new Vector<>(); 
      
    // counter for clients 
    static int i = 0; 
  
    public static void main(String[] args) throws IOException  
    { 
        // server is listening on port 9999 
        ServerSocket server = new ServerSocket(9999); 
          
        Socket socket; 
          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            // Accept the incoming request 
            //System.out.println("socket "+server);
            socket = server.accept(); 
  
            System.out.println("New client request received : " + socket); 
              
            // obtain input and output streams 
            DataInputStream input = new DataInputStream(socket.getInputStream()); 
            DataOutputStream output = new DataOutputStream(socket.getOutputStream()); 
              
            System.out.println("Creating a new handler for this client..."); 
  
            // Create a new handler object for handling this request. 
            ClientHandler handler = new ClientHandler(socket,"client " + i, input, output); 
  
            // Create a new Thread with this object. 
            Thread thread = new Thread(handler); 
              
            System.out.println("Adding this client to active client list"); 
  
            // add this client to active clients list 
            activeClient.add(handler); 
  
            // start the thread. 
            thread.start(); 
  
            // increment i for new client. 
            // i is used for naming only, and can be replaced 
            // by any naming scheme 
            i++; 
  
        } 
    } 
} 
  
// ClientHandler class 
class ClientHandler implements Runnable  
{ 
    
    private String name; 
    final DataInputStream input; 
    final DataOutputStream output; 
    Socket socket; 
    boolean isLoggedIn; 
    String username;  
    // constructor 
    public ClientHandler(Socket socket, String name, 
                            DataInputStream input, DataOutputStream output) { 
        this.input = input; 
        this.output = output; 
        this.name = name; 
        this.socket = socket; 
        this.isLoggedIn=true; 
        username="";
    } 
  
    @Override
    public void run() { 
  
        String received; 
        while (true)  
        { 
            try
            { 
                // receive the string 
                received = input.readUTF();   
                System.out.println(received); 
                  
                 
                // break the string into message and recipient part 
                String stringToken[]= received.split(" ");
                
                if(stringToken[0].compareTo("Login")==0)
                {
                    String username=stringToken[1];
                    String password=stringToken[2];
                    FileReader reader=new FileReader("login.properties");  
                    Properties properties=new Properties();
                    properties.load(reader);
                    String check=properties.getProperty(username);
                    if(check!=null && password.compareTo(check)==0)
                    {    output.writeUTF("success : login successfull"); this.username=username;}
                    else
                       output.writeUTF("Error : login unsuccessful" );
                    reader.close();
                    
                }
                else if(stringToken[0].compareTo("Signup")==0)
                {
                    String username=stringToken[1];
                    String password=stringToken[2];
                    Properties properties=new Properties();
                    properties.load(new FileInputStream("login.properties"));
                    
                    if(properties.get(username)==null){
                    properties.setProperty(username,password);
                    properties.store(new FileOutputStream("login.properties"),"");
                    output.writeUTF("success : signup successfull");
                    }
                    else
                    {properties.store(new FileOutputStream("login.properties"),"");
                    output.writeUTF("Error : signup unsuccessfull");}
                                    }
                else if(stringToken[0].compareTo("Logout")==0)
                {
                    this.isLoggedIn=false; 
                    this.socket.close(); 
                    Thread.currentThread().stop();
                    this.input.close(); 
                    this.output.close();     
                }
                else if(stringToken[0].compareTo("show")==0 && stringToken[1].compareTo("files")==0)
                {
                        String group=get_group(stringToken,2);
                        String res=run_command(new String[]{"ls","./root/groups/"+group+"/files"});
                        output.writeUTF(res);
                }
                else if(stringToken[0].compareTo("show")==0 && stringToken[1].compareTo("log")==0)
                {
                    String group=get_group(stringToken,2);
                    String res=run_command(new String[]{"cat","./root/groups/"+group+"/log.txt"});
                    output.writeUTF(res);
                }
                else if(stringToken[0].compareTo("show")==0 && stringToken[1].compareTo("messages")==0)
                {
                    String group=get_group(stringToken,2);
                    String res=run_command(new String[]{"cat","./root/groups/"+group+"/messages.txt"});
                    output.writeUTF(res);
                }
                else if(stringToken[0].compareTo("list")==0 && stringToken[1].compareTo("all")==0)
                {
                    String res=run_command(new String[]{"ls","./root/groups"});
                    String groups[]=res.split("\n");
                    String str="";                 
                    for(int i=0;i<groups.length;i++)
                    str+=(i+1)+"."+groups[i]+"\n";
                    System.out.println(str);
                    output.writeUTF(str);
                }
                else if(stringToken[0].compareTo("join")==0)
                {
                    String group=get_group(stringToken,1);
                    String res[]=run_command(new String[]{"cat","./root/groups/"+group+"/members.txt"}).split("\n");
                    boolean flag=false;
                    for(int i=0;i<res.length;i++)
                    {
                        if(res[i].compareTo(username)==0)
                        {
                            flag=true;
                            output.writeUTF("you are already member of that group");
                            break;
                        }
                    }
                    if(!flag)
                    {    append(username+"\n","./root/groups/"+group+"/members.txt");
                         output.writeUTF("success: joining successful");
                    }
                    
                }
                else if(stringToken[0].compareTo("write")==0)
                {
                    String group=get_group(stringToken,1);
                    String line=input.readUTF();
                    append(username+" : "+line+"\n","./root/groups/"+group+"/messages.txt");
                    output.writeUTF("success: writing successful");
                }
                else if(stringToken[0].compareTo("list")==0 && stringToken[1].compareTo("my")==0)
                {
                    String[] groups=run_command(new String[]{"ls","./root/groups"}).split("\n");
                    String mygroups="";
                    for(int i=0,k=1;i<groups.length;i++)
                    {
                        String[] members=run_command(new String[]{"cat","./root/groups/"+groups[i]+"/members.txt"}).split("\n");
                        //System.out.println(groups[i]);
                        for(int j=0;j<members.length;j++)
                        {
                            //System.out.println(members[j] +" "+members[j].compareTo(username));
                            if(members[j].compareTo(username)==0)
                            {
                                mygroups+=k+"."+groups[i]+"\n";
                                k++;
                                break;
                            }
                        }

                    }
                    //System.out.println(mygroups);
                        output.writeUTF(mygroups);

                }
                else if(stringToken[0].compareTo("leave")==0)
                {
                    String group=get_group(stringToken,1);
                    String[] members=run_command(new String[]{"cat","./root/groups/"+group+"/members.txt"}).split("\n");
                    remove_user(username,members,"./root/groups/"+group+"/members.txt");
                    output.writeUTF("Left group successfully");
                }
                else if(stringToken[0].compareTo("upload")==0)
                {
                    String group=get_group(stringToken,2);
                    String path[]=input.readUTF().split("/");
                    String filename=path[path.length-1];
                    String data=input.readUTF();
                    
                    //FileWriter fw=new FileWriter(new File("./root/groups/"+group+"/files/"+filename));
                    //fw.write(data);
                    Files.write(Paths.get("./root/groups/"+group+"/files/"+filename), data.getBytes());
                    append(username+" uploaded "+filename+" on "+Calendar.getInstance().getTime().toString()+"\n","./root/groups/"+group+"/log.txt"); 
                    output.writeUTF("File uploaded successfully");
                }
                else if(stringToken[0].compareTo("give")==0)
                {
                    String group=get_group(stringToken,1);
                    String filename=input.readUTF();
                    //System.out.println(filename[1]);
                    String data=run_command(new String[]{"cat","./root/groups/"+group+"/files/"+filename});
                    output.writeUTF(data);
                    append(username+" downloaded "+filename+" on "+Calendar.getInstance().getTime().toString()+"\n","./root/groups/"+group+"/log.txt");
                }
                else if(stringToken[0].compareTo("share")==0)
                {
                    String group=get_group(stringToken,1);
                    String filename=input.readUTF();
                   // System.out.println(filename[0]+" "+filename[1]);
                    String group1=input.readUTF();
                    //System.out.println(group1);
                    String files[]=run_command(new String[]{"ls","./root/groups/"+group1+"/files"}).split("\n");
                    boolean bl=false;
                    for(int i=0;i<files.length;i++)
                    {
                        //System.out.println(files[i]);
                        if(filename.compareTo(files[i])==0)
                        {
                            bl=true;
                            break;
                        }
                    }
                    if(bl)
                        output.writeUTF("file already exists");
                    else
                    {
                        String data=run_command(new String[]{"cat","./root/groups/"+group+"/files/"+filename});
                        append(data,"./root/groups/"+group1+"/files/"+filename);
                        append(username+" shared "+filename+" on "+Calendar.getInstance().getTime().toString()+"\n","./root/groups/"+group1+"/log.txt");
                        output.writeUTF("success:share successfull"); 
                    }
                    
                }


                 
            } catch (IOException e) { 
                  
                e.printStackTrace(); 
            } 
              
        } 

    }

    public void remove_user(String username,String []members,String path)
    {
        try { 
  
            // Open given file in append mode. 
            BufferedWriter out = new BufferedWriter( 
                   new FileWriter(path, false)); 
            for(String stringToken:members)
            if(stringToken.compareTo(username)!=0)        
                out.write(stringToken); 
            out.close(); 
        } 
        catch (IOException e) { 
            System.out.println("exception occoured" + e); 
        }
    }



    public String get_group(String stringToken[],int j)
    {
        String group="";
        for(int i=j;i<stringToken.length;i++)
            if(i==stringToken.length-1)
                group+=stringToken[i];
            else 
                group+=stringToken[i]+" ";
        return group;
    }

    
    public String run_command(String stringToken[])
    {
        String res="";
        try {
            
            // run the Unix "ps -ef" command
                // using the Runtime exec method:
                Process p = Runtime.getRuntime().exec(stringToken);
                
                
                BufferedReader stdInput = new BufferedReader(new 
                     InputStreamReader(p.getInputStream()));
    
                BufferedReader stdError = new BufferedReader(new 
                     InputStreamReader(p.getErrorStream()));
    
                // read the output from the command
                //System.out.println("Here is the standard output of the command:\n");
                String s=null;
                while ((s = stdInput.readLine()) != null) {
                    res=res+s+"\n";
                }
                
                // read any errors from the attempted command
                
            }
            catch (IOException e) {
                System.out.println("exception happened - here's what I know: ");
                e.printStackTrace();
                System.exit(-1);
            }
            return res;
    }

    public void append(String stringToken,String path)
    {
        try { 
  
            // Open given file in append mode. 
            BufferedWriter out = new BufferedWriter( 
                   new FileWriter(path, true)); 
            out.write(stringToken); 
            out.close(); 
        } 
        catch (IOException e) { 
            System.out.println("exception occoured" + e); 
        } 

    }

} 