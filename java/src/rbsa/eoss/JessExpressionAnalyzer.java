/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

import java.util.ArrayList;

/**
 *
 * @author bang
 */
public class JessExpressionAnalyzer {
    
    private static JessExpressionAnalyzer instance = null;
    
    
    public static JessExpressionAnalyzer getInstance()
    {
        if( instance == null ) 
        {
            instance = new JessExpressionAnalyzer();
        }
        return instance;
    }
    
    /**
     * This function checks if the input string contains a parenthesis
     * 
     * @param inputString
     * @return boolean
     */
    public boolean checkParen(String inputString){
        return inputString.contains("(");
    }
    
    
    
    /**
     * This function returns the indices of the parenthesis in a string
     * @param inputString
     * @param n: This function looks for nth appearance of the parenthesis
     * @return int[]: Integer array containing the indices of the parentheses within a string
     */
    public int[] locateParen(String inputString,int n){ // locate nth parentheses
        
        int level = 0;
        int nth = 0;
        int leng = inputString.length();
        int[] parenLoc = new int[2];

        for (int i = 0; i<leng ;i++){
            char ch = inputString.charAt(i);
            if(ch == '('){
                level++;
                if (level == 1) nth++;
                if ((nth == n) && (level == 1))  parenLoc[0] = i;
            }
            if(ch == ')' ){
                level--;
            }
            if((level == 0) && (nth == n)) {
                parenLoc[1] = i;
                break;
            }
        }
        return parenLoc;
    }
    
    
    public ArrayList<Integer> locateNestedParen(String inputString,int focusLevel){ // locate all parentheses at specified level
        
        int level = 0;
        int nth = 0;
        int leng = inputString.length();
        ArrayList<Integer> parenLoc = new ArrayList<>();

        for (int i = 0; i<leng ;i++){
            if(inputString.charAt(i) == '('){
                level++;
                if (level == focusLevel)  parenLoc.add(i);
            }
            if(inputString.charAt(i) == ')' ){
                level--;
                if (level == focusLevel) parenLoc.add(i);
            }
        }
        return parenLoc;
    }
    
    
    public String getInsideParen(String inputString,int nth ,int level){
        
        if (checkParen(inputString) == false) return inputString; 
        int[] loc = locateParen(inputString, nth);
        String insideParen = inputString.substring(loc[0]+1, loc[1]);
        if (level == 1){
            return insideParen;
        } else {
            return getInsideParen(insideParen,1 ,level-1);
        }
    }
    
    
    public String getInsideParen(String inputString,int level){
        
        if (checkParen(inputString) == false) return inputString; 
        int[] loc = locateParen(inputString, 1);
        String insideParen = inputString.substring(loc[0]+1, loc[1]);
        if (level == 1){
            return insideParen;
        } else {
            return getInsideParen(insideParen,level-1);
        }
    }
    
    
    
    public int getNestedParenLevel(String inputString){
        int leng = inputString.length();
        int cnt = 0;
        int level = 0;
        int maxLevel = 0;
        
        for (int i = 0;i<leng;i++){
            if(inputString.charAt(i) == '('){
                level++;
                if (level > maxLevel) maxLevel = level;
            }
            if(inputString.charAt(i) == ')' ){
                level--;
            }
        }
        return maxLevel;
    }
    
    
    /**
     * This function counts the number of slots in an expression.
     * @param inputString
     * @return 
     */
    public int getNumOfSlots(String inputString){
        int leng = inputString.length();
        int cnt = 0;
        int level = 0;
        for (int i = 0;i<leng;i++){
            if(inputString.charAt(i) == '('){
                level++;
                if (level == 1) cnt++;
            }
            if(inputString.charAt(i) == ')' ){
                level--;
            }
        }
        return cnt;
    }
    
    
    
    
    /**
     * This function checks if the given expression is if-then statement.
     * @param inputExpression
     * @return boolean
     */
    public boolean checkIF_THEN(String inputExpression){
        String expression = getInsideParen(inputExpression,1);
        expression = collapseAllParenIntoSymbol(expression);
        return (expression.contains("if")) && (expression.contains("then"));
    }
    
    
    
    /**
     * This function replaces the contents of all parentheses with a character 'X'.
     * This is used to analyze the outermost structure of the given expression (by removing all nexted structure).
     * @param inputExpression
     * @return 
     */
    public String collapseAllParenIntoSymbol(String inputExpression){
        
        // If the given expression doesn't contain any parenthesis, return
        if (checkParen(inputExpression) == false) return inputExpression; 
        
        
        int num = getNumOfSlots(inputExpression);
        String expression = inputExpression;
        
        for (int i = 0;i<num;i++){
            int[] loc = locateParen(expression,i+1);
            String s1 = expression.substring(0, loc[0]+1);
            String s2 = expression.substring(loc[1]);
            String symbol = "";
            for (int j = 0;j< loc[1]-loc[0]-1 ;j++) symbol = symbol.concat("X");
            expression = s1 + symbol + s2;
        }
        return expression;
    }
    
    
    
}
