/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;
import util.Util;
/**
 *
 * @author Prashu
 */
public class Topology {
    public static void readNeighbors(int myId, int N,
                                     LinkedList<Integer> neighbors) {
        Util.println("Reading topology");
        try {
            BufferedReader dIn = new BufferedReader(
				new FileReader("D:/sync1/tp" + myId+".txt"));
            StringTokenizer st = new StringTokenizer(dIn.readLine());
            while (st.hasMoreTokens()) {
                int neighbor = Integer.parseInt(st.nextToken());
                neighbors.add(neighbor);
            }
        } catch (FileNotFoundException e) {
            Util.println("No topology found");
            for (int j = 0; j < N; j++)
                if (j != myId) neighbors.add(j);
        } catch (IOException e) {
            System.err.println(e);
        }
        Util.println(neighbors.toString());
    }
}
