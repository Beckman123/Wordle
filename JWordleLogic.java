import java.awt.Color;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.io.*;

//Handles the backend logic for the JWordle game, evaluating player guesses and maintaining
//the state of the game
public class JWordleLogic{
   
   
   
   //Number of words in the provided words.txt file
   private static final int WORDS_IN_FILE = 5758;
   
   //Use for generating random numbers!
   private static final Random rand = new Random();
   
   //Dimensions of the game grid in the game window
   public static final int MAX_ROWS = 6;
   public static final int MAX_COLS = 5;
   
   //Character codes for the enter and backspace key press
   public static final char ENTER_KEY = KeyEvent.VK_ENTER;
   public static final char BACKSPACE_KEY = KeyEvent.VK_BACK_SPACE;
   
   //The null character value (used to represent an "empty" value for a spot on the game grid)
   public static final char NULL_CHAR = 0;
   
   //Various Color Values
   private static final Color CORRECT_COLOR = new Color(53, 209, 42); //(Green)
   private static final Color WRONG_PLACE_COLOR = new Color(235, 216, 52); //(Yellow)
   private static final Color WRONG_COLOR = Color.DARK_GRAY; //(Dark Gray [obviously])
   private static final Color DEFAULT_KEYBOARD_COLOR = new Color(160, 163, 168); //(Light Gray)
   
   //Name of file containing all the five letter words
   private static final String WORDS_FILENAME = "words.txt";
   
   //Secret word used when the game is running in debug mode
   private static final char[] DEBUG_SECRET_WORD = {'B', 'A', 'N', 'A', 'L'};      
   
   
   //...Feel free to add more final variables of your own!
      
            
   
   public static final boolean warmUp = false;
   
   
   //******************   NON-FINAL GLOBAL VARIABLES   ******************
   //********  YOU CANNOT ADD ANY ADDITIONAL NON-FINAL GLOBALS!  ******** 
   
   
   //Array storing all words read out of the file
   private static String[] words = wordsArray(WORDS_FILENAME);
   public static String[] wordsArray(String file){
      try{
      Scanner input = new Scanner(new File(file));
      String words[] = new String[WORDS_IN_FILE];
      for (int i =0;i<WORDS_IN_FILE;i++){
         words[i] = input.next();
      }
      input.close();
      return words;
      }
      catch(FileNotFoundException e){
         System.out.println("File Not Found");
      }

   return null;
   }
   
   //The current row/col where the user left off typing
   private static int currentRow, currentCol;
      
   
   //*******************************************************************
   
   
   
   
   
   //This function gets called ONCE when the game is very first launched
   //before the user has the opportunity to do anything.
   //
   //Should return the randomly chosen "secret word" the player needs to guess
   //as a char array
   public static void warmUpFunc(boolean warmUp){
      if (warmUp){
         JWordleGUI.setGridLetter(0,0,'C');
         JWordleGUI.setGridColor(0,0,CORRECT_COLOR);
         JWordleGUI.setGridLetter(1,3,'O');
         JWordleGUI.setGridColor(1,3,WRONG_COLOR);
         JWordleGUI.setGridLetter(3,4,'S');
         JWordleGUI.setGridLetter(5,4,'C');
         JWordleGUI.setGridColor(5,4,WRONG_PLACE_COLOR);
         JWordleGUI.setKeyColor('U',CORRECT_COLOR);
         JWordleGUI.setKeyColor('C',WRONG_COLOR);
      }
   }

   

   public static char[] initGame(){//chooses which word is picked
      warmUpFunc(warmUp);
      if (GameLauncher.DEBUG_USE_HARDCODED_WORD){
         return DEBUG_SECRET_WORD;
      }
      int randWord = rand.nextInt(5758);
      String chosenWord = words[randWord];
      char[] charArray = chosenWord.toCharArray();
      return charArray;
   }
   
               
   
   
   //This function gets called everytime the user types a valid key on the
   //keyboard (alphabetic character, enter, or backspace) or clicks one of the
   //keys on the graphical keyboard interface.
   //
   //The key pressed is passed in as a char value.
   public static void keyPressed(char key){
       //implement me!
       if (warmUp){
         if (key=='W'){
         JWordleGUI.wiggleGrid(3);
         }
       }
      else{
         //System.out.println("Row: " + currentRow + " Column: " + currentCol);
         if(key == ENTER_KEY){
            enterKey();
         }
         else if(key == BACKSPACE_KEY){
            if (currentCol>0){
            backSpace();
            }
         }
         else{
            if(currentCol <=4){
            JWordleGUI.setGridLetter(currentRow, currentCol, key);
            currentCol++;
            }
         }
      }
      //System.out.println("keyPressed called! key (int value) = '" + ((int)key) + "'");
   }

   
   public static void enterKey(){//this enter key evaluates if the enter key is valid or not then continues onto another fucntion
      boolean correctAmount = true;
      char [] secretWord  = JWordleGUI.getSecretWord();
      for(int column = 0; column<MAX_COLS; column++){
         char letter = JWordleGUI.getGridLetter(currentRow,column);
         if (letter==NULL_CHAR){
            correctAmount = false;
         }
      }
      if (correctAmount){
         boolean validGuess = false;
         char [] currentGuessArr = currentGuess();
         String currentGuess = charArrToString(currentGuessArr);
         for (int i =0; i<WORDS_IN_FILE; i++){
            if(currentGuess.equals(words[i].toUpperCase())){
               validGuess = true;
            }
         }
         if (GameLauncher.DEBUG_ALLOW_ANY_GUESS){
            enterKeyContinued(secretWord,currentGuessArr);
         }
         else if(validGuess){
            enterKeyContinued(secretWord,currentGuessArr);
         }
         else{
            JWordleGUI.wiggleGrid(currentRow);
         }
      }
      else{
         JWordleGUI.wiggleGrid(currentRow);
      }

   }

   public static void enterKeyContinued(char [] secretWord,char[] currentGuessArr){//does all computing for enter given that the input is valid
      currentCol = 0;
      String currentGuessStr = charArrToString(currentGuessArr);
      String secretWordStr = charArrToString(secretWord);
      for (int i = 0; i<currentGuessArr.length;i++){//checks for correct place
         if (secretWord[i]==currentGuessArr[i]){
            JWordleGUI.setGridColor(currentRow, i, CORRECT_COLOR);
            JWordleGUI.setKeyColor(currentGuessArr[i], CORRECT_COLOR);
            secretWord[i]='0';
            currentGuessArr[i]='0';
         }  
      }  
      for (int i = 0; i<currentGuessArr.length;i++){//checks for wrong place
         for (int z =0; z<secretWord.length;z++){
            if (currentGuessArr[i]==secretWord[z]&& secretWord[z]!= '0'&&currentGuessArr[i]!='0'){
               JWordleGUI.setGridColor(currentRow, i, WRONG_PLACE_COLOR);
               if (JWordleGUI.getKeyColor(currentGuessArr[i])!= CORRECT_COLOR){
                  JWordleGUI.setKeyColor(currentGuessArr[i], WRONG_PLACE_COLOR);
               }
               secretWord[z]='0';
               currentGuessArr[i]='0';
            }
         }
      }
      for (int i = 0; i<currentGuessArr.length;i++){ //checks for bad letter
         if (currentGuessArr[i]!='0'){
            JWordleGUI.setGridColor(currentRow, i, WRONG_COLOR);
            if (JWordleGUI.getKeyColor(currentGuessArr[i])!= CORRECT_COLOR&&JWordleGUI.getKeyColor(currentGuessArr[i])!= WRONG_PLACE_COLOR){
               JWordleGUI.setKeyColor(currentGuessArr[i], WRONG_COLOR);
            }
         }  
      }  
      if (currentGuessStr.equals(secretWordStr)){//game end
         JWordleGUI.endGame(true);
      }
      if(currentGuessStr.equals(secretWordStr)==false && currentRow == MAX_ROWS-1){//game end, lose
         JWordleGUI.endGame(false);
      }
      if (currentRow < MAX_ROWS){
         currentRow++;
      }
   }

   public static void backSpace(){//backspace function
      JWordleGUI.setGridLetter(currentRow, currentCol-1,NULL_CHAR);
      currentCol--;
   }
   
   public static char [] currentGuess(){//helper function that gets the current five letter input before enter is pressed
      char [] builtArray = new char [5];
      for (int i = 0;i<MAX_COLS;i++){
         builtArray[i] = JWordleGUI.getGridLetter(currentRow,i);
      }
      return builtArray;
   }

   public static String charArrToString(char [] array){//turns a five length char array into a string
      String fixedString = String.valueOf(array[0])+String.valueOf(array[1])+String.valueOf(array[2])+String.valueOf(array[3])+String.valueOf(array[4]);
      return fixedString;
   }

}
