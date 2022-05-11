//Youngjoo Lee
//CS 10 Prof. Li
//PS 3

import java.util.*;
import java.io.*;

public class huffman { //Would we need to declare (K, V) Or not because we know the type.

    Map<Character, Integer> freqTable;
    Map<Character, String> charCode;
    PriorityQueue<BinaryTree<BTdata>> freqQueue;


    public huffman(){
        freqTable = new TreeMap<>();
        freqQueue = new PriorityQueue<>(new TreeComparator<>()); //instantiate the priority queue with the TreeComparator object
        charCode = new TreeMap<>();
    }

    /**
     * Generates the frequency table storing information in a TreeMap object. Charactes as keys and frequencies as values
     * @param pathName the path  to the file
     */
    public void generTable(String pathName){
        try{
            BufferedReader input = new BufferedReader(new FileReader(pathName));
            int curr = input.read();

            while(curr != -1) { //until we reach the end of the file
                mapUpdater((char) curr); //continue to update the map
                curr = input.read();
            }
        }
        catch(FileNotFoundException e){
            System.out.println("The file is empty");
        } catch(IOException e){
            System.out.println("You got an error: " + e);
        }
    }

    /**
     * Helper function for generTable() that with the current character checks if is already in table and acts accordingly
     * @param current the current character being checked
     */
    public void mapUpdater(char current) {
        if(!freqTable.containsKey(current)){
            freqTable.put(current, 1);
        }
        else{
            freqTable.put(current, freqTable.get(current) + 1); //freqTable.get(current)  gets us current value which we then can update by one
        }
    }

    /**
     * Adds binary trees for each character in the freqTable into the freqQueue priority Queue object
     */
    public void queueCreate(){
        for(Character current: freqTable.keySet()){ //utilizes the keySet() method of Maps to get us a set of the key Characters to iterate through

            BinaryTree<BTdata> currentTree = new BinaryTree<BTdata>(new BTdata(current, freqTable.get(current)));//Create new tree for each character in table

            freqQueue.add(currentTree); //add the tree to the queue
        }
    }

    /**
     * Takes all the trees in freqQueue, and until there is only one tree left in the queue, removes the first two,
     * creates a new tree with the two removed trees as its child and addds that new tree to the queue.
     * Thereby reducing by one tree each iteration
     */
    public void treeCreation(){

        while (freqQueue.size() > 1){ //only want one tree in the end
            BinaryTree<BTdata> T1 = freqQueue.remove(); //remove first two trees off the queue
            BinaryTree<BTdata> T2 = freqQueue.remove();

            BinaryTree<BTdata> T = new BinaryTree<>(new BTdata(null, T1.data.charFrequency + T2.data.charFrequency), T1, T2);
            //new tree with a frequency that equals sum of T1 + T2 frequencies

            freqQueue.add(T);
        }

    }

    /**
     * Construct map  of character and its code through single traversal of the tree. Pre=order traversal
     * @param tree the current Binary Tree being worked on
     * @param soFar
     */
    public void codeRetrieval(BinaryTree<BTdata> tree, String soFar){ //HOW DOES THE STRING KNOW TO REVERT BACK BY ITSELF AFTER REACHING LEAF?

        if(tree == null) return; //edge case of the tree being null
        if(tree.isLeaf()){ //base case
            charCode.put(tree.data.treeChar, soFar); //the path is finished so add the character and its info to the map
        }
        else{
            if (tree.hasLeft()) { //goes through left sub tree
                BinaryTree<BTdata> succesor = tree.getLeft();
                codeRetrieval(succesor, soFar + "0"); //recursive call with updated String tracker
            }
            if (tree.hasRight()){ //goes through right sub tree
                BinaryTree<BTdata> successor = tree.getRight();
                codeRetrieval(successor, soFar + "1");
            }
        }
    }

    /**
     * Repeatedly read the next character in text file, look up its code word in the code map, then write the sequence of 0's and 1's
     * in that code word as bits to the output file.
     * @param filePath path to the input text file that we will read from
     * @param compressName the path to the compressed file that we will write to.
     */
    public void compression(String filePath, String compressName)  { //WHAT DOES HOW MANY OF THOSE BITS ARE USEFUL AND HOW MANY ARE GARBAGE MEAN?

        BufferedBitWriter bitOutput = null; //creates the file which will have the ouptput
        BufferedReader input = null;
        try{
            input = new BufferedReader(new FileReader(filePath));
            bitOutput = new BufferedBitWriter(compressName);
        } catch (FileNotFoundException e){
            System.out.println("You got the exception: " + e);
        }
        try{
            int curr = input.read();

            while (curr != -1) {
                String currCode = charCode.get((char) curr); //gets the string code related to the character
                for(char digit: currCode.toCharArray()){ //loop through the String
                    if (digit == '0'){
                        bitOutput.writeBit(false);
                        //System.out.print(digit); //testing to check accuracy of binary file
                    }
                    else{
                        bitOutput.writeBit(true); //digit == '0'. assumed if program runs correctly
                        //System.out.print(digit);
                    }
                }
                curr = input.read();
            }
        }
        catch (IOException e){
            System.out.println("you got an exception from input.read(): " + e);
        }
        finally{
            try{
                bitOutput.close(); //close the file. IS THIS THE RIGHT PLACE TO PUT IT?
                input.close();
            }
            catch (IOException e){
                System.out.println("you got an exception from input.read(): " + e);
            }
        }
    }

    /**
     * Takes the compressed file, and reads through all the bits, going down the tree accordingly until a leaf node is reached.
     * Then access its character value, and write that into the decompressed Output file. Then reset back to the root node
     * @param inPath the path to the compressedInput file that we will read from
     * @param outPath path to the decompressedOutput file that we will write to
     */
    public void deCompression(String inPath, String outPath){
        BufferedBitReader bitInput = null;
        BufferedWriter decompressOutput = null; //initialize variables

        try{
            bitInput = new BufferedBitReader(inPath); //declare them
            decompressOutput = new BufferedWriter(new FileWriter(outPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BinaryTree<BTdata> currNode = freqQueue.peek(); //start at the root
        if (currNode == null) return;  //accounting for empty file
        try{
            while(bitInput.hasNext()) {
                if (currNode.isLeaf()) { //not getting to here the last time

                    char nodeChar = currNode.data.treeChar; //get the character out of the current node's data
                    decompressOutput.write(nodeChar); //write to the output
                    currNode = freqQueue.peek();//reset to root
                }
                boolean bit = bitInput.readBit();

                if (!bit) currNode = currNode.getLeft(); //if bit == false -> 0 then go left down the tree

                else if (bit) currNode = currNode.getRight(); // if bit == true -> 1 then go right down the tree
            }
            char nodeChar = currNode.data.treeChar; //accounts for the last node. IS THERE A MORE EFFICIENT WAY TO DO SO?
            decompressOutput.write(nodeChar);

        } catch (IOException e){
            System.out.println("You got: " + e);
        }
        finally{

            try {
                bitInput.close();
                decompressOutput.close();
            } catch (IOException e){
                System.out.println("file cannot close");
            }
        }
    }

    /**
     * Test method
     */
    public static void test(String inputFile, String compressFile, String deCompressFile){
        huffman tester = new huffman();

        try {
            tester.generTable(inputFile);
            tester.queueCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (BinaryTree<BTdata> tree : hello.freqQueue){
//            System.out.println(tree.data); //seems like it is still sorted by lexographic, not by frequency
//        }
        tester.treeCreation(); //this works! Output for the data of the single tree --> Character: mull, charFrequency: 11 and has alll proper subtree components
        System.out.println(tester.freqQueue.peek());
        tester.codeRetrieval(tester.freqQueue.peek(),"");
        System.out.println(tester.charCode);
        tester.compression(inputFile, compressFile);
        tester.deCompression(compressFile, deCompressFile);
    }

    public static void main(String[] args){
        //unbalanced Tree
        test("inputs/uniqueTree.txt", "outputs/uniqueTree_compressed.txt", "outputs/uniqueTree_decompressed.txt");
        //only single letter multiple times
        test("inputs/singleMultiple.txt", "outputs/singleMultiple_compressed.txt", "outputs/singleMultiple_decompressed.txt");
        //just a single letter
        test("inputs/single.txt", "outputs/single_compressed.txt", "outputs/single_decompressed.txt");
        //empty file
        test("inputs/empty.txt", "outputs/empty_compressed.txt", "outputs/empty_decompressed.txt");
        //generic
        test("inputs/hello.txt", "outputs/hello_compressed.txt", "outputs/hello_decompressed.txt");
        //Constitution
        test("inputs/USConstitution.txt", "outputs/USConstitution_compressed.txt", "outputs/USConstitution_decompressed.txt");
        //War and peace
        test("inputs/WarAndPeace.txt", "outputs/WarAndPeace_compressed.txt", "outputs/WarnAndPeace_decompressed.txt");
    }
}
