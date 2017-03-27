import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by CARLINSE1 on 3/27/2017.
 */
public class NodeCommunicator {
    private String url;


    public NodeCommunicator(String url) {
        this.url = url;
    }

    /**
     * Sends a get request with given data, returns result or "Error"
     * @param data JSON encoded data
     * @return JSON response or "Error"
     */
    public String getMessage(String data) {
        try {
            URLConnection connection = new URL(url + "?data=" + URLEncoder.encode(data, "UTF-8")).openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            InputStream response = connection.getInputStream();

            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();
                return responseBody;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";
    }

    /**
     * Converts a simple Map to JSON
     * @param map <String, String> map
     * @return Encoded JSON
     */
    public String mapToJSON(HashMap<String, String> map) {
        StringBuilder result = new StringBuilder();

        result.append("{");

        for(HashMap.Entry<String, String> e : map.entrySet()) {
            result.append("\"" + e.getKey() + "\":\"" + e.getValue() +"\",");
        }

        result.deleteCharAt(result.length()-1);
        result.append("}");

        return result.toString();
    }

    /**
     * Converts JSON to an array of objects !!!Only supports objects with string values, invalid JSON will cause unexpected results!!!
     * @param data JSON formatted object or array of objects
     * @return ArrayList of HashMaps of String key and Object value
     */
    public ArrayList<HashMap<String, String>> JSONToMap(String data) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        if(data.charAt(0) == '[')
            data = data.substring(1, data.length()-2);

        for(int i=0; i<data.length(); i++) {
            if(data.charAt(i) == '{') {
                HashMap<String, String> obj = new HashMap<>();
                i = parseObject(data, i, obj);
                result.add(obj);
            }
        }

        return result;
    }

    /**
     * Parses an object into a HashMap
     * @param data
     * @param index
     * @param out
     * @return
     */
    private int parseObject(String data, int index, HashMap<String, String> out) {
        StringBuilder key = new StringBuilder();
        StringBuilder val = new StringBuilder();

        for(int i=index; i<data.length(); i++) {
            switch(data.charAt(i)) {
                case '}':
                    return i+1;
                case '{':
                case ',':
                    i = parseString(data, i+1, key);
                    break;
                case ':':
                    i = parseString(data, i+1, val);
                    out.put(key.toString(), val.toString());
            }
        }

        return data.length();
    }

    /**
     * Parse a string out of a given string
     * @param data the overall data string
     * @param index where to begin parsing the string
     * @param out a StringBuilder that can receive the resulting string
     * @return the ending index after closing double quote
     */
    private int parseString(String data, int index, StringBuilder out) {
        int valueStart = index;

        for(int i=index; i<data.length(); i++) {
            if(data.charAt(i) == '"') {
                //First actual value
                if(valueStart == index) {
                    valueStart = i+1;
                } else {
                    out.setLength(0);
                    out.append(data.substring(valueStart, i));
                    return i;
                }
            }

            //Skip the character after a control sequence
            if(data.charAt(i) == '\\')
                i++;
        }

        return data.length();
    }
}
