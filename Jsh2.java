
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Command{
    private boolean valid ;//if command is valid
    private final String type;//cd !! !<> ls
    private final String arg;
    private final String originalCommand;//usr input

    private Command(boolean valid,String type, String arg, String originalCommand){
        this.valid = valid;
        this.type = type;
        this.arg = arg;
        this.originalCommand = originalCommand;
    }

    //interface
    public String getType(){return type;}
//    public String toString(){return type + arg;}
    public String getArg(){return arg;}
    public boolean isVaild(){return valid;}
    public String getOriginalCommand() {
        return originalCommand;
    }

    public static Command parseCommand(String originalCommand) {
        String arg = null;
        String type = null;
        boolean valid = true;

        assert originalCommand != "";
        String[] strings = originalCommand.split(" ");
        //System.out.println("strings.length = " + strings.length);

        try{
            type = strings[0];
            if(strings.length==1){
                //single cd is also valid
                if(type.equals("cd")&& type.equals("ls") && type.equals("!!") && type.equals("history"))valid = false;
            }
            else if(strings.length==2){
                //only cd and ! have argument
                if(type.equals("cd") && type.equals("!"))valid = false;
                arg = strings[1];
            }
            //if(type != "cd"&& type != "ls" && type != "!!" && type != "history" && type !="!")valid = false;
            else if(strings.length > 2)valid = false;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return new Command(valid,type,arg,originalCommand);
    }
}

public class Jsh2 {
    public static void main(String[] args)throws IOException{
//        String xixi = System.getProperty("user.dir");
//        System.out.println("usr dir: " +  xixi);
//        assert xixi!=null;

        File currentFile = new File(System.getProperty("user.dir"));
//        String ______currentDir = currentFile.getAbsolutePath().replace(File.separatorChar, '/');
//        System.out.println(______currentDir);
        String commandLine;// cmdline input by usr
        BufferedReader console  = new BufferedReader(new InputStreamReader(System.in));

        List<String> history = new ArrayList<>();//store the recent cmdline
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(currentFile);

        //init
        //File currentDir = new File("");
//        System.out.println("Sep: "+File.separatorChar);
//        System.out.println("Path Sep: "+File.pathSeparator);

        while (true){
            System.out.print("jsh>");
            commandLine = console.readLine().replace(File.separatorChar, '/');
            //escape the space
            int contextStart = 0;
            for(;commandLine.charAt(contextStart)==' ';contextStart++);
            //usr entered a return
            if(commandLine.equals(""))continue;
            if(contextStart != 0)commandLine = commandLine.substring(contextStart);

            Command cmd = Command.parseCommand(commandLine);
//            System.out.println(cmd.getType() + cmd.getArg());

            history.add(commandLine);

            if(!cmd.isVaild()){
                //
                System.out.println("invalid cmd!");
                continue;
            }

            if(cmd.getType().equals("!!")){
                if(history.size() == 1){
                    System.out.println("no history at all");
                    continue;
                }
                String lastCmd = history.get(history.size() - 2);
                //replace
                cmd = Command.parseCommand(lastCmd);
                //replace too
                history.set(history.size() - 1, lastCmd);
            }
            else if(cmd.getType().equals("!")){
                int index = Integer.parseInt(cmd.getArg());
                String realCmd = history.get(index - 1);
                //replace too

                cmd = Command.parseCommand(realCmd);
                history.set(history.size() - 1, realCmd);
            }

            if(!cmd.isVaild()){
                System.out.println("invalid history command!");
                continue;
            }
            //cd ls history
            switch (cmd.getType()){
                case "history":
                    for(int i = 1; i<history.size(); i++){
                        System.out.println(i + " "+ history.get(i - 1));
                    }
                    break;
                case "ls":
                    pb.command("ls");
                    Process process = pb.start();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );
                    String line;
                    while((line = br.readLine())!=null)System.out.println(line);
                    break;
                case "cd":
//                    String
                    System.out.println("case cd");
                    String usrHome = System.getProperty("user.dir");
                    assert usrHome != null;
                    //cd has no arg, we go to user.dir, where we start
                    String destDir = cmd.getArg();

                    if(destDir != null){
                        //we use the tailed '/' to represent relative path
                        if(destDir.endsWith("/") && !destDir.equals("/")){
                            //if(destDir.equals("/"))
                            if(destDir.startsWith("./"))destDir = destDir.substring(2);
                            destDir = currentFile.getAbsolutePath() + "/" + destDir.substring(0, destDir.length() - 1);
                        }
                        else if(destDir.equals("..")){
                            destDir = currentFile.getAbsolutePath().replace(File.separatorChar,'/');
                            assert destDir.length() > 0;
                            int lastSep = destDir.length() - 1;
                            for(;destDir.charAt(lastSep)!='/';lastSep--);
                            destDir = destDir.substring(0, lastSep);
                            System.out.println(destDir);
                        }
                        else{
                            //absolute path
                            //do nothing
                        }
                    }
                    else destDir = usrHome;

                    //now we have the dest dir, represented by absolute path
                    File destFile = new File(destDir);
                    if(!destFile.exists()){
                        System.out.println("no such dir!");
                        continue;
                    }
                    currentFile = destFile;
                    pb.directory(currentFile);
                    System.out.println("now we are in " + destFile.getAbsolutePath().replace(File.separatorChar,'/'));

                    //pb.start();
                    continue;
                    default:


            }


        }
    }

}