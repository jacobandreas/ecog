package ecog.data;

import java.util.HashSet;
import java.util.Set;

public class PhoneTypes {
	
	  public static final Set<String> vowels = new HashSet<String>();
	  public static final Set<String> consonants = new HashSet<String>();
	  public static final Set<String> stops = new HashSet<String>();
	  public static final Set<String> dentals = new HashSet<String>();
	  public static final Set<String> voiced = new HashSet<String>();
	  public static final Set<String> unvoiced = new HashSet<String>();
	  
	  static {
		  vowels.add("aa");
		  vowels.add("ae");
		  vowels.add("ax");
		  vowels.add("ah");
		  vowels.add("ax-h");
		  vowels.add("ao");
		  vowels.add("eh");
		  vowels.add("ih");
		  vowels.add("ix");
		  vowels.add("iy");
		  vowels.add("uh");
		  vowels.add("uw");
		  vowels.add("ay");
		  vowels.add("aw");
		  vowels.add("ey");
		  vowels.add("ow");
		  vowels.add("oy");
		  vowels.add("er");

		  consonants.add("ng");
		  consonants.add("eng");
		  consonants.add("sh");
		  consonants.add("ch");
		  consonants.add("y");
		  consonants.add("zh");
		  consonants.add("jh");
		  consonants.add("dh");
		  consonants.add("hh");
		  consonants.add("hv");
		  consonants.add("th");
		  consonants.add("b");
		  consonants.add("bcl");
		  consonants.add("d");
		  consonants.add("dcl");
		  consonants.add("f");
		  consonants.add("g");
		  consonants.add("gcl");
		  consonants.add("k");
		  consonants.add("kcl");
		  consonants.add("l");
		  consonants.add("el");
		  consonants.add("m");
		  consonants.add("em");
		  consonants.add("n");
		  consonants.add("en");
		  consonants.add("nx");
		  consonants.add("p");
		  consonants.add("pcl");
		  consonants.add("r");
		  consonants.add("s");
		  consonants.add("t");
		  consonants.add("tcl");
		  consonants.add("dx");
		  consonants.add("v");
		  consonants.add("w");
		  consonants.add("y");
		  consonants.add("z");
		  consonants.add("epi");
		  consonants.add("h#");
		  consonants.add("pau");
		  consonants.add("q");


		  stops.add("p");
		  stops.add("pcl");
		  stops.add("b");
		  stops.add("bcl");
		  stops.add("t");
		  stops.add("tcl");
		  stops.add("dx");
		  stops.add("d");
		  stops.add("dcl");
		  stops.add("k");
		  stops.add("kcl");
		  stops.add("g");
		  stops.add("gcl");

		  dentals.add("d");
		  dentals.add("dcl");
		  dentals.add("t");
		  dentals.add("tcl");
		  dentals.add("dx");
		  dentals.add("n");
		  dentals.add("l");

		  voiced.add("m");
		  voiced.add("b");
		  voiced.add("bcl");
		  voiced.add("v");
		  voiced.add("dh");
		  voiced.add("n");
		  voiced.add("d");
		  voiced.add("dcl");
		  voiced.add("z");
		  voiced.add("l");
		  voiced.add("jh");
		  voiced.add("zh");
		  voiced.add("ng");
		  voiced.add("g");
		  voiced.add("gcl");
		  voiced.add("w");

		  unvoiced.add("p");
		  unvoiced.add("pcl");
		  unvoiced.add("f");
		  unvoiced.add("th");
		  unvoiced.add("t");
		  unvoiced.add("tcl");
		  unvoiced.add("s");
		  unvoiced.add("ch");
		  unvoiced.add("sh");
		  unvoiced.add("k");
		  unvoiced.add("kcl");
		  unvoiced.add("h");
	  }

}
