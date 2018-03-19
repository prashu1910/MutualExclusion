/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectivity;

import java.util.ArrayList;

/**
 *
 * @author Prashu
 */
class Url
{
    String name;
    String host;
    int port;
    Url(String name, String host, int port)
    {
        this.host = host;
        this.name = name;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }    
}
public class NameTable {
    private final ArrayList<Url> url = new ArrayList<>();
    
    int search(String s) {
        for (int i = 0; i < url.size(); i++)
            if (url.get(i).getName().equals(s)) return i;
        return -1;
    }
    int insert(String s, String hostName, int portNumber) {
        int oldIndex = search(s); // is it already there
        if ((oldIndex == -1)) { 
            url.add(new Url(s,hostName,portNumber));
            return 1;
        } else // already there, or table full
            return 0;
    }
    int getPort(int index) {
        return url.get(index).getPort();
    }
    String getHostName(int index) {
        return url.get(index).getHost();
    }
}
