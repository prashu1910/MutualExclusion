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
    int id;
    Url(String name, String host, int port, int id)
    {
        this.host = host;
        this.name = name;
        this.port = port;
        this.id = id;
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
    
    public int getId()
    {
        return id;
    }
    @Override
    public String toString()
    {
        return name + " " + host + " " + " " + port + " " + id;
    }
}
public class NameTable {
    private final ArrayList<Url> url = new ArrayList<>();
    
    int search(String s) {
        for (int i = 0; i < url.size(); i++)
            if (url.get(i).getName().equals(s)) return i;
        return -1;
    }
    
    int search(int id)
    {
        for (int i = 0; i < url.size(); i++)
            if (url.get(i).getId() == id) return i;
        return -1;
    }
    int insert(String s, String hostName, int portNumber, int id) {
        int oldIndex = search(s); // is it already there
        if ((oldIndex == -1)) { 
            Url u = new Url(s,hostName,portNumber, id);
            url.add(u);
            return 1;
        } else // already there, or table full
            return 0;
    }
    
    int remove(int id)
    {
        int oldIndex = search(id);
        if(oldIndex != -1)
        {
            url.remove(oldIndex);
            return 1;
        }
        return 0;
    }
    
    int getPort(int index) {
        return url.get(index).getPort();
    }
    String getHostName(int index) {
        return url.get(index).getHost();
    }
    
    public ArrayList<Url> getAllData()
    {
        return url;
    }
}
