//Launches the JWordle game
public class GameLauncher{
   
   
   //Controls if the secret word is displayed in the game window a(true) or not (false)
   public static final boolean DEBUG_SHOW_SECRET_WORD = false;    
   
   //Controls if the hard coded word is used as the secret word (true) or a random
   //word read from the text file (false)
   public static final boolean DEBUG_USE_HARDCODED_WORD = false;      
   
   //Controls if the player can guess any 5 letter string (true) or if the guess must
   //be a word read from the text file (false)
   public static final boolean DEBUG_ALLOW_ANY_GUESS = false;    
   
   
   
   public static void main(String[] args){
      JWordleGUI.launchGame();
   }
   
   
   
   
   
}