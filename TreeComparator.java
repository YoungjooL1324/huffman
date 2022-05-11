//Youngjoo Lee
//CS 10 Prof. Li
//PS 3

import java.util.Comparator;

public class TreeComparator<e> implements Comparator<BinaryTree<BTdata>> { //why do we need to still state e here.

    /**
     * Compares two tree objects based on their int value charFrequency
     * @param treeOne A binary Tree object of BTA data type
     * @param treeTwo A binary Tree object of BTA data type
     * @return an integer based on the result
     */
    public int compare(BinaryTree<BTdata> treeOne, BinaryTree<BTdata> treeTwo){
        int result = treeOne.data.charFrequency - treeTwo.data.charFrequency; //checks treeOne with treeTwo, not vice versa/

        if (result < 0){  //treeOne.data.charFrequency < treeTwo.data.charFrequency
            return -1;
        }
        else if (result == 0){  //treeOne.data.charFrequency == treeTwo.data.charFrequency
            return 0;
        }
        return 1; //treeOne.data.charFrequency > treeTwo.data.charFrequency


    }

}
