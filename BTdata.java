//Youngjoo Lee
//CS 10 Prof. Li
//PS 3
public class BTdata {

    Character treeChar;
    int charFrequency;

    /**
     * Constructor method of BinaryTree data type
     * @param treechar char value of the tree
     * @param charFrequency value indicating frequency of the char in the read file
     */
    public BTdata(Character treechar, int charFrequency){
        this.treeChar = treechar;
        this.charFrequency = charFrequency;
    }

    /**
     * gets the treeChar instance variable
     * @return treeChar instance variable
     */
    public Character getTreeChar(){
        return treeChar;
    }

    /**
     * gets the charFrequency instance variable
     * @return charFrequency instance variable
     */
    public int getCharFrequency(){
        return charFrequency;
    }

    @Override
    /**
     * returns string representation of the tree's values
     */
    public String toString(){
        return "Character: " + getTreeChar() + " charFrequency: " + getCharFrequency();
    }
}
