/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

import java.util.HashSet;

/**
 *
 * @author Prashu
 */
public class Token {
    
    /*private int rowCounter = 1;
    private int colCounter = 0;
    private boolean chCol = false;
    private static Token token = null;*/
    
    int process;
    HashSet<Integer> processSet = new HashSet<>();
    int count;
    String tokenMovement = "down";

    public String getTokenMovement() {
        return tokenMovement;
    }

    public void setTokenMovement(String tokenMovement) {
        this.tokenMovement = tokenMovement;
    }
    public Token()
    {
        
    }
    public Token(Token t)
    {
        this.count = t.count;
        this.process = t.process;
    }
    
    /*public static Token getToken(Token t)
    {
        return new Token
    }*/
    public Token(int process, HashSet<Integer> hash, int count, String tokenMovement) {
        this.process = process;
        this.processSet = hash;
        this.count = count;
        this.tokenMovement = tokenMovement;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public HashSet<Integer> getProcessSet() {
        return processSet;
    }

    public void setProcessSet(HashSet<Integer> hash) {
        this.processSet = hash;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    public String toString()
    {
        return process+" "+count + " "+ tokenMovement+" "+processSet;
    }
   
}

