package entities;


import java.io.Serializable;

public class Position implements Serializable{
    int lineNum;
    int wordNum;

    @Override
    public String toString(){
        return "(" + String.valueOf(lineNum) + "," + String.valueOf(wordNum) + ")";
    }

    public Position(int lineNum, int wordNum){
        this.lineNum = lineNum;
        this.wordNum = wordNum;
    }
}
