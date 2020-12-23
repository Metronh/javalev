import java.util.*;                                                                     
import java.io.*;
import java.util.ArrayList;
public class spellChecker{
    public static void main(String[] args) throws IOException {
        /*please open the whole folder with the dictionary*/
        Scanner fileInput = new Scanner(System.in);                             //Getting filname and path
        System.out.print("Name and path of file: ");                            
        String fileName = fileInput.next();                                     //Storing filename and path in a String
        File dictionary = new File("Dictionary.txt");                           //Getting the dictionary file
        Scanner words = new Scanner(dictionary);                                //Getting Dictionary
        File text = new File(fileName);                                         //Getting the file that needs to be spell checked
        Scanner readText = new Scanner(text);                                   //Scanning the file that needs to be spell checked
        FileReader readTextFileR = new FileReader(text);                        //Reading the file that needs to be spellchecked 
        BufferedReader rBufferedReader = new BufferedReader(readTextFileR);     //Getting element by element of the file that needs to be spell checked
        ArrayList <String> arrDictionary = new ArrayList<String>();             //Array list to store the dictionary
        
        while(words.hasNext()){
            arrDictionary.add(words.next());                                    //Putting elements of the dictionary in the arraylist for easier searching 
        }
        
        PrintWriter globalWriter = new PrintWriter(new FileOutputStream(dictionary));                //Creating a printwriter for when user wants to add element to the dictionary
        
        ArrayList <String> userText = readTheText(rBufferedReader);                     //Calling function to store the elements of the main text to be Stored
        
        ArrayList <String> spellCheckedArray = spellCheck(userText, arrDictionary, globalWriter); //Storing the spell checked array
            
        Collections.sort(arrDictionary,String.CASE_INSENSITIVE_ORDER);    //Sorting the dictionary in alphabetical order 
        for (int k = 0; k < arrDictionary.size(); k++) {                    
            globalWriter.println(arrDictionary.get(k));                   //Printing the new dictionary to the dictionary file
        }
        globalWriter.close();                                             //Closing editing of the file
        newFile(spellCheckedArray,readText);                              //Creating new file that is spell checked
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

    public static ArrayList spellCheck(ArrayList <String> text, ArrayList <String> dict, PrintWriter localPrint){
        String temp;                                                                        //String to store the current word
        for (int i = 0; i < text.size(); i++) {                                             //Iterating through the text
            temp = text.get(i).trim();                                                      //Getting rid of white spaces
            if(temp.matches("^[a-zA-Z]*$")){                                                //Checks if the current word is a WORD
                for (int j = 0; j < dict.size(); j++) {                                     //Goes through dictionary array and checks if word is there

                    if(temp.equalsIgnoreCase(dict.get(j))){
                        break;                                                              //If word is there it breaks out of the loop 
                    }
                    if((j+1) == dict.size()){                                               //If it is at the end of loop it begins asks if the user wants to input or add to the dictionary
                        Scanner userInput = new Scanner(System.in);                         //For user inputs
                        
                        System.out.println("");
                        System.out.print("If you want to replace the word '" + temp +"' type 'replace' or if you want to add it to the dictionary type 'add'");
                        System.out.println("");

                        String user_Input = userInput.next();
                        
                        if(user_Input.equalsIgnoreCase("replace")){                         //If user wants to replace
                            userInput = new Scanner(System.in);                             //New scanner old variable
                            System.out.println("");                                          
                            System.out.println("Please enter the word you want to use: ");  //Asking for word you want to put in
                            user_Input = userInput.next();                                  //Replacing the word 
                            text.set(i,user_Input);                                         //Replacing the word in the array
                            i = -1;                                                         //reseting the loop
                            break;                                                          //Starting again
                        }

                        if(user_Input.equalsIgnoreCase("add")){                             //If user wants to add
                    
                            dict.add(temp);                                                 //Adding it to the dictionary arraylist
                        
                            System.out.println("'"+temp+"' has been added to the dictionary");
                            break;
                            
                        }
                    }
                }
            }
        }


        return text;
    }

    public static void newFile(ArrayList <String> finalText,Scanner oldFile) throws IOException{
        /*This method outputs a new file with the texts and restores the spaces*/
        File finalFile = new File("spellCheckerCorrectedText.txt");                 //Creating new file
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
}