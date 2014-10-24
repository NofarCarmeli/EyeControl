package com.example.eyecontrol;

import java.util.HashMap;

/*
 * This file is used to set the definitions of what the different menus do
 * 
 * Here you can edit maps from menus to their names
 * 
 * Here you can also edit maps
 * from sequences of gestures (starting with the mode number)
 * to:
 * 1 - A string describing the action (will be read in the headphone)
 * 2 - The type of action (starting with 'Action.Type.' and then one of:
 *     POWER, MODE, ALARM, SPEAK, LANGUAGE, DISPLAY, CHARACTER, ERASE, READ, CLEAR
 * 3 - The CHARACTER action should be followed be the character
 *     The MODE action should be followed be the new mode number
 *     Other actions should not provide a third argument
 *     
 * changing the sound files is under res/raw directory of the project
 */

import java.util.Map;

public class Definitions {
	
	public int time_between_gestures = 2; // in seconds
	public char first_menu = '1';
	public char first_language = 'h'; // must be 'e' or 'h'
	public String eng_no_text = "Nothing to read";
	public String heb_no_text = "לא נכתב כלום";
	public String alarm_seq = "BB";
	
	public Map <String, Action> seq_map;
	public Map <Character, String> menu_map;
	
	Definitions() {
		
		seq_map = new HashMap<String, Action>(75);
		menu_map = new HashMap<Character, String>(3);
		
		// menu names
		
		menu_map.put('0', "Rest");
		menu_map.put('1', "Main");
		menu_map.put('2', "Hebrew");
		menu_map.put('3', "English");
		menu_map.put('4', "Signs");
		
		// gesture translation
		
		seq_map.put("0BB", new Action ("", "", Action.Type.ALARM));
		seq_map.put("0BD", new Action ("main menu", "תפריט ראשי", Action.Type.MODE, '1'));
		
		seq_map.put("1UU", new Action ("hebrew menu", "תפריט עברית", Action.Type.MODE, '2'));
		seq_map.put("1UL", new Action ("english menu", "תפריט אנגלית", Action.Type.MODE, '3'));
		seq_map.put("1UR", new Action ("signs menu", "תפריט סימנים", Action.Type.MODE, '4'));
		seq_map.put("1UD", new Action ("power off", "כיבוי", Action.Type.POWER));
		seq_map.put("1UB", new Action ("", "", Action.Type.READ));
		seq_map.put("1LU", new Action ("", "", Action.Type.SPEAK, '3'));
		seq_map.put("1LL", new Action ("", "", Action.Type.SPEAK, '9'));
		seq_map.put("1LR", new Action ("", "", Action.Type.SPEAK, '4'));
		seq_map.put("1LD", new Action ("", "", Action.Type.SPEAK, '6'));
		seq_map.put("1LB", new Action ("", "", Action.Type.SPEAK, '0'));
		seq_map.put("1RU", new Action ("", "", Action.Type.SPEAK, '1'));
		seq_map.put("1RL", new Action ("", "", Action.Type.SPEAK, '7'));
		seq_map.put("1RR", new Action ("", "", Action.Type.SPEAK, '2'));
		seq_map.put("1RD", new Action ("", "", Action.Type.SPEAK, '8'));
		seq_map.put("1RB", new Action ("עברית", "english", Action.Type.LANGUAGE));
		//seq_map.put("1DU", new Action ("", "", Action.Type.READ));
		//seq_map.put("1DL", new Action ("", "", Action.Type.READ));
		//seq_map.put("1DR", new Action ("", "", Action.Type.READ));
		//seq_map.put("1DD", new Action ("", "", Action.Type.READ));
		//seq_map.put("1DB", new Action ("", "", Action.Type.READ));
		seq_map.put("1BU", new Action ("change display", "שינוי תצוגה", Action.Type.DISPLAY));
		//seq_map.put("1BL", new Action ("", "", Action.Type.READ));
		seq_map.put("1BR", new Action ("clear", "מחיקה", Action.Type.CLEAR));
		seq_map.put("1BD", new Action ("rest mode", "מצב מנוחה", Action.Type.MODE, '0'));
		seq_map.put("1BB", new Action ("", "", Action.Type.ALARM));
		
		seq_map.put("2UU", new Action ("ה", "ה", Action.Type.CHARACTER, 'ה'));
		seq_map.put("2UL", new Action ("ד", "ד", Action.Type.CHARACTER, 'ד'));
		seq_map.put("2UR", new Action ("ג", "ג", Action.Type.CHARACTER, 'ג'));
		seq_map.put("2UD", new Action ("ב", "ב", Action.Type.CHARACTER, 'ב'));
		seq_map.put("2UB", new Action ("א", "א", Action.Type.CHARACTER, 'א'));
		seq_map.put("2LU", new Action ("ס", "ס", Action.Type.CHARACTER, 'ס'));
		seq_map.put("2LL", new Action ("נ", "נ", Action.Type.CHARACTER, 'נ'));
		seq_map.put("2LR", new Action ("מ", "מ", Action.Type.CHARACTER, 'מ'));
		seq_map.put("2LD", new Action ("ל", "ל", Action.Type.CHARACTER, 'ל'));
		seq_map.put("2LB", new Action ("כ", "כ", Action.Type.CHARACTER, 'כ'));
		seq_map.put("2RU", new Action ("י", "י", Action.Type.CHARACTER, 'י'));
		seq_map.put("2RL", new Action ("ט", "ט", Action.Type.CHARACTER, 'ט'));
		seq_map.put("2RR", new Action ("ח", "ח", Action.Type.CHARACTER, 'ח'));
		seq_map.put("2RD", new Action ("ז", "ז", Action.Type.CHARACTER, 'ז'));
		seq_map.put("2RB", new Action ("ו", "ו", Action.Type.CHARACTER, 'ו'));
		seq_map.put("2DU", new Action ("ר", "ר", Action.Type.CHARACTER, 'ר'));
		seq_map.put("2DL", new Action ("ק", "ק", Action.Type.CHARACTER, 'ק'));
		seq_map.put("2DR", new Action ("צ", "צ", Action.Type.CHARACTER, 'צ'));
		seq_map.put("2DD", new Action ("פ", "פ", Action.Type.CHARACTER, 'פ'));
		seq_map.put("2DB", new Action ("ע", "ע", Action.Type.CHARACTER, 'ע'));
		seq_map.put("2BU", new Action ("ת", "ת", Action.Type.CHARACTER, 'ת'));
		seq_map.put("2BL", new Action ("ש", "ש", Action.Type.CHARACTER, 'ש'));
		seq_map.put("2BR", new Action ("back space", "מחק", Action.Type.ERASE));
		seq_map.put("2BD", new Action ("space", "רווח", Action.Type.CHARACTER, ' '));
		seq_map.put("2BB", new Action ("main menu", "תפריט ראשי", Action.Type.MODE, '1'));
		
		seq_map.put("3UU", new Action ("a", "a", Action.Type.CHARACTER, 'a'));
		seq_map.put("3UL", new Action ("b", "b", Action.Type.CHARACTER, 'b'));
		seq_map.put("3UR", new Action ("c", "c", Action.Type.CHARACTER, 'c'));
		seq_map.put("3UD", new Action ("d", "d", Action.Type.CHARACTER, 'd'));
		seq_map.put("3UB", new Action ("e", "e", Action.Type.CHARACTER, 'e'));
		seq_map.put("3LU", new Action ("f", "f", Action.Type.CHARACTER, 'f'));
		seq_map.put("3LL", new Action ("g", "g", Action.Type.CHARACTER, 'g'));
		seq_map.put("3LR", new Action ("h", "h", Action.Type.CHARACTER, 'h'));
		seq_map.put("3LD", new Action ("i", "i", Action.Type.CHARACTER, 'i'));
		seq_map.put("3LB", new Action ("l", "l", Action.Type.CHARACTER, 'l'));
		seq_map.put("3RU", new Action ("m", "m", Action.Type.CHARACTER, 'm'));
		seq_map.put("3RL", new Action ("n", "n", Action.Type.CHARACTER, 'n'));
		seq_map.put("3RR", new Action ("o", "o", Action.Type.CHARACTER, 'o'));
		seq_map.put("3RD", new Action ("p", "p", Action.Type.CHARACTER, 'p'));
		seq_map.put("3RB", new Action ("r", "r", Action.Type.CHARACTER, 'r'));
		seq_map.put("3DU", new Action ("s", "s", Action.Type.CHARACTER, 's'));
		seq_map.put("3DL", new Action ("t", "t", Action.Type.CHARACTER, 't'));
		seq_map.put("3DR", new Action ("u", "u", Action.Type.CHARACTER, 'u'));
		seq_map.put("3DD", new Action ("v", "v", Action.Type.CHARACTER, 'v'));
		seq_map.put("3DB", new Action ("w", "w", Action.Type.CHARACTER, 'w'));
		seq_map.put("3BU", new Action ("k", "k", Action.Type.CHARACTER, 'k'));
		seq_map.put("3BL", new Action ("y", "y", Action.Type.CHARACTER, 'y'));
		seq_map.put("3BR", new Action ("back space", "מחק", Action.Type.ERASE));
		seq_map.put("3BD", new Action ("space", "רווח", Action.Type.CHARACTER, ' '));
		seq_map.put("3BB", new Action ("main menu", "תפריט ראשי", Action.Type.MODE, '1'));
		
		seq_map.put("4UU", new Action ("zero", "0", Action.Type.CHARACTER, '0'));
		seq_map.put("4UL", new Action ("one", "1", Action.Type.CHARACTER, '1'));
		seq_map.put("4UR", new Action ("two", "2", Action.Type.CHARACTER, '2'));
		seq_map.put("4UD", new Action ("three", "3", Action.Type.CHARACTER, '3'));
		seq_map.put("4UB", new Action ("four", "4", Action.Type.CHARACTER, '4'));
		seq_map.put("4LU", new Action ("five", "5", Action.Type.CHARACTER, '5'));
		seq_map.put("4LL", new Action ("six", "6", Action.Type.CHARACTER, '6'));
		seq_map.put("4LR", new Action ("seven", "7", Action.Type.CHARACTER, '7'));
		seq_map.put("4LD", new Action ("eight", "8", Action.Type.CHARACTER, '8'));
		seq_map.put("4LB", new Action ("nine", "9", Action.Type.CHARACTER, '9'));
		seq_map.put("4RU", new Action ("j", "j", Action.Type.CHARACTER, 'j'));
		seq_map.put("4RL", new Action ("q", "q", Action.Type.CHARACTER, 'q'));
		seq_map.put("4RR", new Action ("x", "x", Action.Type.CHARACTER, 'x'));
		seq_map.put("4RD", new Action ("z", "z", Action.Type.CHARACTER, 'z'));
		//seq_map.put("4RB", new Action ("", "", Action.Type.READ));
		seq_map.put("4DU", new Action ("dot", "נקודה", Action.Type.CHARACTER, '.'));
		seq_map.put("4DL", new Action ("comma", "פסיק", Action.Type.CHARACTER, ','));
		seq_map.put("4DR", new Action ("question mark", "סימן שאלה", Action.Type.CHARACTER, '?'));
		seq_map.put("4DD", new Action ("exclamation mark", "סימן קריאה", Action.Type.CHARACTER, '!'));
		//seq_map.put("4DB", new Action ("", "", Action.Type.READ));
		//seq_map.put("4BU", new Action ("", "", Action.Type.READ));
		seq_map.put("4BL", new Action ("power off", "כיבוי", Action.Type.POWER));
		seq_map.put("4BR", new Action ("back space", "מחק", Action.Type.ERASE));
		seq_map.put("4BD", new Action ("space", "רווח", Action.Type.CHARACTER, ' '));
		seq_map.put("4BB", new Action ("main menu", "תפריט ראשי", Action.Type.MODE, '1'));
    	
	}

}
