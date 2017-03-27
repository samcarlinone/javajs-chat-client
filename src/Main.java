import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url = "https://javajs-chat.herokuapp.com/"; //"http://localhost";
        NodeCommunicator comm = new NodeCommunicator(url);

        try(Scanner scnr = new Scanner(System.in)) {
            System.out.println("Input username: ");
            String user = scnr.nextLine();

            HashMap<String, String> inMap = new HashMap<>();
            inMap.put("user", user);

            comm.getMessage(comm.mapToJSON(inMap));

            while(true) {
                String input = scnr.nextLine();

                if(input.equals("exit"))
                    break;

                inMap = new HashMap<>();
                inMap.put("msg", input);
                inMap.put("user", user);

                String msg = comm.getMessage(comm.mapToJSON(inMap));

                if(msg.equals("Error"))
                    break;

                ArrayList<HashMap<String, String>> result = comm.JSONToMap(msg);

                for(HashMap<String, String> map : result) {
                    switch(map.get("type")) {
                        case "msg":
                            System.out.println("[" + map.get("user") + "] > " + map.get("msg") + "\n");
                            break;
                        case "disconnect":
                            System.out.println("[" + map.get("user") + "] has disconnected");
                    }
                }
            }
        }
    }
}
