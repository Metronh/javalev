import java.util.*;
import java.io.*;
import java.util.ArrayList;
public class SpellcheckerSuggestion{
    /*MOST of the comments are the same because I just copied and pasted from spellchecker.java but two new functions levenshtienDistance and levenshtien*/
    public static void main(String[] args) throws IOException {
        Scanner fileInput = new Scanner(System.in);                         //Getting filname and path
        System.out.print("Name and path of file: ");                        
        String fileName = fileInput.next();                                 //Storing filename and path in a String
        File dictionary = new File("Dictionary.txt");                       //Getting Dictionary
        Scanner words = new Scanner(dictionary);                            
        File text = new File(fileName);                                     //Getting the file that needs to be spell checked
        Scanner readText = new Scanner(text);                               //Scans the text
        FileReader readTextFileR = new FileReader(text);                    //Reads the text
        BufferedReader rBufferedReader = new BufferedReader(readTextFileR); //Read charater by charater
        ArrayList <String> arrDictionary = new ArrayList<String>();         //Array to store the dictionary
        
        while(words.hasNext()){
            arrDictionary.add(words.next());                                //Adding words to dictionary
        }
        
        
        ArrayList <String> userText = readTheText(rBufferedReader);         //Splits text up
        
        ArrayList <String> spellCheckedArray = spellCheckSuggestion(userText,arrDictionary); //Corrects spelling mistakes 


        newFile(spellCheckedArray,readText);    //Creates new file
        fileInput.close();
        words.close();
    }
    public static ArrayList readTheText(BufferedReader currentText) throws IOException{
        /*readTheText is a function that goes through the main text and splits it up into words and punctuation and stores them in a arraylist*/
        ArrayList <String> arrText = new ArrayList<String>();   
        StringBuilder fullTextBu = new StringBuilder();         //Creating string to store the whole text
        int pos;
        while((pos = currentText.read()) != -1){
            fullTextBu.append((char)pos);
        }
        String fullText = fullTextBu.toString();                   //converting string builder to actua string
        currentText.close();                                       //Closing the io
        
        String[] StrarrText = fullText.split(" ?(?<!\\G)((?<=[^\\p{Punct}])(?=\\p{Punct})|\\b) ?"); //Spliting the whole string up at every space and keed punctuation for later use
        for (int i = 0; i < StrarrText.length; i++) {
            arrText.add(StrarrText[i]);             //Transfering elements to an array list
        }

        return arrText;
    }

    public static void newFile(ArrayList <String> finalText,Scanner oldFile) throws IOException{
        /*This method outputs a new file with the texts and restores the spaces*/
        File finalFile = new File("spellCheckerSuggestionCorrectedText.txt");                 //Creating new file
        int counter = 0;                                                            //Counter is going to combine the punctuation and the text
        String currentword;                                                         //Reading old textfile
        while(oldFile.hasNext()){                                                   
            currentword = oldFile.next();                                           //Going through old textfile and if a word has punctuation concatinating it with the next word
            
            if(!currentword.matches("^[a-zA-Z]*$")){
                finalText.set(counter, finalText.get(counter).concat(finalText.get(counter+1)));
                finalText.remove(counter+1);
            }
            if(finalText.size() - 2 <= counter){
                break;                                                              //Prevent index error 
            }
            counter+=1;
        }
        String finalString = finalText.get(0);                                     //Puting words together in a final string with spaces between them 
        for (int i = 1; i < finalText.size(); i++) {
            finalString = finalString+" " +finalText.get(i);
        }

        PrintWriter finalFilewrighter = new PrintWriter(finalFile);               //Printing to new file
        finalFilewrighter.println(finalString);
        finalFilewrighter.close();                                                 //Closing io
    }
    
    public static ArrayList spellCheckSuggestion(ArrayList <String> text, ArrayList <String> dict){
        /*This method checks if the words are in the dictionary and if not offers suggestions and modifies the text for later printing*/
        String temp;                                                                    
        for (int i = 0; i < text.size(); i++) {
            temp = text.get(i).trim();                                                      //Getting rid of white space
            boolean notInDict = false;                                                      //Flag if in dictionary or not
            if(temp.matches("^[a-zA-Z]*$")){                                                //Checks if its punctuation
                for (int j = 0; j < dict.size(); j++) {                                     //Go through whole dictionary
                    if(temp.equalsIgnoreCase(dict.get(j))){                                 //If in dictionary breaks 
                        break;
                    }
                    if(j == dict.size()-1){
                        notInDict = true;                                                       //If not in dictionary flags up
                    }
                }
                if(notInDict){
                    System.out.println("The suggestions might take a few seconds because the recursive implementation of Levenshtein is ineffecient");
                    System.out.println("The word '"+temp+"' is not in the dictionary here are some suggestions:");
                    ArrayList <String> suggestions = levenshtien(temp.toLowerCase(), dict);     //Storing suggestions for user and makes sure its lowercase
                    System.out.println("");                                                      
                  
                    for (int j = 0; j < suggestions.size() - 1; j++) {
                        System.out.print(suggestions.get(j)+", ");                            //Outputing suggestions
                    }
                    System.out.print(suggestions.get(suggestions.size()-1)+".");               
                    System.out.println("");
                    System.out.println("To select the first one input '1', second input '2' and so on");
                    System.out.print("Input Here: ");                                       //User selects suggestion 
                    Scanner userInput = new Scanner(System.in);
                    text.set(i,suggestions.get(Integer.parseInt(userInput.next())-1));      //Puts suggestion in array
                    userInput.close();


                }
            }
        }
            
        

        return text;                //Returns final text
    }

    public static ArrayList levenshtien(String mainWord, ArrayList <String> dict){
        /*This method gets all words from dictionary with the same levenshtien distance and returns them as suggestions*/
        ArrayList <Integer> levDict = new ArrayList<Integer>();         //Storing the levenshien distance for whole dictionary
        ArrayList <String> listOfSuggetions = new ArrayList<String>();
        for (int i = 0; i < dict.size(); i++) {
            levDict.add(levenshtienDistance(mainWord, dict.get(i), mainWord.length(), dict.get(i).length()));   //putting all the levlevenshien distance in the arraylist
        }
        int smallest = 0;       //Finds the smallest levenshien distance 
        for (int i = 1; i < levDict.size(); i++) {
            if(levDict.get(smallest) > levDict.get(i)){
                smallest = i;
            }
        }
      
        ArrayList <Integer> allSmallest = new ArrayList<Integer>();
        for (int i = 0; i < levDict.size(); i++) {          //Stores all same smallest distanes index in dictionary
            if(levDict.get(smallest) == levDict.get(i)){
                allSmallest.add(i);
            }
        }
        
        for (int i = 0; i < allSmallest.size(); i++) {      //puts actual words of that previous index in dictionary to be returned
            listOfSuggetions.add(dict.get(allSmallest.get(i)));
        }
        return listOfSuggetions;
    }

    public static int levenshtienDistance(String wordOne, String wordTwo, int lengthOne, int lengthTwo){
        /*Recursive levenshtien distance based on wikipedia: https://en.wikipedia.org/wiki/Levenshtein_distance*/
        int distance;
        if(lengthOne == 0){         
            return lengthTwo;
        }
        if(lengthTwo == 0){
            return lengthOne;
        }
        if(wordOne.charAt(lengthOne - 1) == wordTwo.toLowerCase().charAt(lengthTwo - 1)){ //Base case
            distance = 0;
        }
        else{
            distance = 1;
        }
        return (Math.min(levenshtienDistance(wordOne, wordTwo, lengthOne - 1, lengthTwo),
                        Math.min(levenshtienDistance(wordOne, wordTwo, lengthOne, lengthTwo -1),
                        levenshtienDistance(wordOne, wordTwo, lengthOne -1, lengthTwo - 1))) + distance);
        }
}