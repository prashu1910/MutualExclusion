/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

/**
 *
 * @author Prashu
 */
public class Token {
    
    private int rowCounter = 1;
    private int colCounter = 0;
    private boolean chCol = false;
    private static Token token = null;
    
    
    private Token()
    {
        
    }
    
    public int getRowCounter() {
        return rowCounter;
    }

    public void setRowCounter(int rowCounter) {
        this.rowCounter = rowCounter;
    }

    public int getColCounter() {
        return colCounter;
    }

    public void setColCounter(int colCounter) {
        this.colCounter = colCounter;
    }

    public boolean isChCol() {
        return chCol;
    }

    public void setChCol(boolean chCol) {
        this.chCol = chCol;
    }
    public static Token getToken()
    {
        if(token == null)
        {
            token = new Token();
        }
        return token;
    }
}

