import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into collections of stemmed words.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Alex L Hanson
 * @version Spring 2020
 *
 * @see TextParser
 */
public class TextFileStemmer {
	
  /** The default stemmer algorithm used by this class. */
  public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	
  /**
   * Helper function to clean a line of words and yields a list of filter words
   * @param line unfiltered string of words
   * 
   * @return array words needed to be stemmed
   * 
   * @throws NullPointerException line was given as null
   */
  public static String[] getWords(String line) throws NullPointerException {
	  return TextParser.parse(line);
  }
  
  /**
   * Clean a line of words and collect all the stems to return as an array
   * @param line line of text that needs to parsed and cleaned to find stems
   * 
   * @return stems from a line that has been cleaned
   * 
 * @throws NullPointerException line was given as null
   */ 
  public static String[] getArrayOfStems(String line) throws NullPointerException {
	  return getArrayOfStems(line, new SnowballStemmer(DEFAULT));
  }
  
  /**
   * Clean a line of words and collect all the stems to return as an array
   *
   * @param line line of text that needs to parsed and cleaned to find stems
   * @param stemmer used to create stems
   * 
   * @return stems from a line that has been cleaned
   * 
   * @throws NullPointerException line or stemmer was given as null
   */
  public static String[] getArrayOfStems(String line, Stemmer stemmer) throws NullPointerException {
	  //clean line into an array of words
	  String[] words = getWords(line);
	  //create array and stemmer
	  String[] stems = new String[words.length];
	  //add all stems
	  for (int i = 0; i < words.length; i++) {
		  stems[i] = stemmer.stem(words[i]).toString();
	  }
	  //return list of stems
	  return stems;
  }

  /**
   * Returns a list of cleaned and stemmed words parsed from the provided line.
   *
   * @param line the line of words to clean, split, and stem
   * 
   * @return a list of cleaned and stemmed words
   *
   * @throws NullPointerException line was given as null
   *
   * @see SnowballStemmer
   * @see #DEFAULT
   * @see #listStems(String, Stemmer)
   */
  public static final ArrayList<String> listStems(String line) throws NullPointerException {
    // THIS IS PROVIDED FOR YOU; NO NEED TO MODIFY
    return listStems(line, new SnowballStemmer(DEFAULT));
  }
  
  /**
   * Returns a list of cleaned and stemmed words parsed from the provided line.
   *
   * @param line the line of words to clean, split, and stem
   * @param stemmer the stemmer to use
   * 
   * @return a list of cleaned and stemmed words
   * 
   * @throws NullPointerException line or stemmer was given as null
   * 
   *
   * @see Stemmer#stem(CharSequence)
   * @see TextParser#parse(String)
   */
  public static ArrayList<String> listStems(String line, Stemmer stemmer) throws NullPointerException {
	//clean line and make sure it's not not
	String[] stemArray = getArrayOfStems(line, stemmer);
	//create list of stems
	ArrayList<String> stems = new ArrayList<>();
	//add stems
	for (String stem : stemArray) {
		stems.add(stem);
	}
	//return list of stems
	return stems;
  }

  /**
   * Reads a file line by line, parses each line into cleaned and stemmed words, and then adds those
   * words to a set.
   *
   * @param inputFile the input file to parse
   * 
   * @return a set of stems from file
   * 
   * @throws NullPointerException The input file was null
   * @throws FileNotFoundException could not read file
   * @throws UnsupportedOperationException Couldn't convert path to a file
   * @throws IOException if unable to read or parse file
   *
   * @see #uniqueStems(String)
   * @see TextParser#parse(String)
   */
  public static ArrayList<String> listStems(Path inputFile) 
		throws IOException, NullPointerException, FileNotFoundException, UnsupportedOperationException {
		//Try with resource to filter the lines from the input file
		try (BufferedReader fileReader = new BufferedReader(new FileReader(inputFile.toFile()))) {
			//declarations
			String line = null;
			ArrayList<String> lineOfStems = null;
			ArrayList<String> accumulator = new ArrayList<>();
			//Reading line by line
			while ((line = fileReader.readLine()) != null) {
				lineOfStems = listStems(line);
				accumulator.addAll(lineOfStems);
			}
			//a set of stems from file
			return accumulator;
		}
  }

  /**
   * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from the provided
   * line.
   *
   * @param line the line of words to clean, split, and stem
   * 
   * @return a sorted set of unique cleaned and stemmed words
   * 
   * @throws NullPointerException The input file was null
   * @throws FileNotFoundException could not read file
   * @throws UnsupportedOperationException Couldn't convert path to a file
   * @throws IOException if unable to read or parse file
   * 
   * @see SnowballStemmer
   * @see #DEFAULT
   * @see #uniqueStems(String, Stemmer)
   */
  public static final TreeSet<String> uniqueStems(String line) 
		  throws IOException, NullPointerException, FileNotFoundException, UnsupportedOperationException {
    // THIS IS PROVIDED FOR YOU; NO NEED TO MODIFY
    return uniqueStems(line, new SnowballStemmer(DEFAULT));
  }

  /**
   * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from the provided
   * line.
   *
   * @param line the line of words to clean, split, and stem
   * @param stemmer the stemmer to use
   * 
   * @return a sorted set of unique cleaned and stemmed words
   * 
   * @throws NullPointerException The input file was null
   * @throws FileNotFoundException could not read file
   * @throws UnsupportedOperationException Couldn't convert path to a file
   * @throws IOException if unable to read or parse file
   *
   * @see Stemmer#stem(CharSequence)
   * @see TextParser#parse(String)
   */
  public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) 
		  throws IOException, NullPointerException, FileNotFoundException, UnsupportedOperationException {
	if (line == null) {
		return new TreeSet<String>();
	}
	//clean line and make sure it's not not
	ArrayList<String> listOfStems = listStems(line, stemmer);
	//create list of stems
	TreeSet<String> stems = new TreeSet<String>(listOfStems);
	//return list of stems
	return stems;
  }

  /**
   * Reads a file line by line, parses each line into cleaned and stemmed words, and then adds those
   * words to a set.
   *
   * @param inputFile the input file to parse
   * 
   * @return a sorted set of stems from file
   * 
   * @throws NullPointerException The input file was null
   * @throws FileNotFoundException could not read file
   * @throws UnsupportedOperationException Couldn't convert path to a file
   * @throws IOException Error occurred creating tree map
   * 
   * @see #uniqueStems(String)
   * @see TextParser#parse(String)
   */
  public static TreeSet<String> uniqueStems(Path inputFile) 
	throws IOException, NullPointerException, FileNotFoundException, UnsupportedOperationException {
	//clean line and make sure it's not not
	ArrayList<String> listOfStems = listStems(inputFile);
	TreeSet<String> accumulatorTreeSet = new TreeSet<>(listOfStems);	//Reads a file line by line, parses each line into cleaned and stemmed words, and then adds those words to a set 
	//return a sorted set of stems from file
	return accumulatorTreeSet;
  }
  
  /**
   * Description: Reads the inputFile line by line, parses the line, and creates a tree map of all
   * the unique stems found within a line in addition to documenting the line it was found on
   * 
   * @param inputFile expects a valid text file from the {@link TextFileFinder} static call `find`
   * 
   * @return a tree map of stems mapped to their positions found in the input file
   * 
   * @throws NullPointerException The input file was null
   * @throws FileNotFoundException could not read file
   * @throws UnsupportedOperationException Couldn't convert path to a file
   * @throws IOException Error occurred creating tree map
   * 
   */
	public static TreeMap<String, TreeSet<Integer>> generateStemPositionMap(Path inputFile)
		throws IOException, NullPointerException, FileNotFoundException, UnsupportedOperationException {
		ArrayList<String> generatedListStems = listStems(inputFile);
		TreeMap<String, TreeSet<Integer>> stemPositionMap = new TreeMap<>();
		int i = 1;
		for (String stem : generatedListStems) {
			stemPositionMap.putIfAbsent(stem, new TreeSet<Integer>());
			stemPositionMap.get(stem).add(i++);
		}
		return stemPositionMap;
	}
	
	/**
	 * Stems words from a parsed text/html 
	 * @param local word mapping for stem, url path or resource, and position(s)
	 * @param url path or resource
	 * @param html parsed text/html
	 */
	public static void parseHtml(WordIndex local, String url, String html) {
		int position = 0;
		for (String stem: listStems(html)) {
			local.add(stem, url, ++position);
		}
		local.addFileCount(url, position);
	}
}