import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into collections of stemmed words.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 *
 * @see TextParser
 */
public class TextFileStemmer {

  /** The default stemmer algorithm used by this class. */
  public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

  // TODO Add helper methods as needed (optional)
  /**
   * Helper function to clean a line of words and yields a list of filter words
   * @param line unfiltered string of words
   * @return array words needed to be stemmed
   */
  public static String[] cleanLine(String line) {
	  return TextParser.parse(line);
  }
  
  /**
   * Clean a line of words and collect all the stems to return as an array
   * @param line
   * @return stems from a line that has been cleaned
   */
  public static String[] cleanLineAndGetArrayOfStems(String line) {
	  //clean line
	  String[] cleanedLine = cleanLine(line);
	  if (cleanedLine.length == 0) return new String[0];
	  
	  //create array and stemmer
	  String[] stems = new String[cleanedLine.length];
	  Stemmer stemmer = new SnowballStemmer(DEFAULT);
	  
	  //add all stems
	  for (int i = 0; i < cleanedLine.length; i++) stems[i] = stemmer.stem(cleanedLine[i]).toString();
	  
	  //return list of stems
	  return stems;
  }
  
  /**
   * Returns a list of cleaned and stemmed words parsed from the provided line.
   *
   * @param line the line of words to clean, split, and stem
   * @param stemmer the stemmer to use
   * @return a list of cleaned and stemmed words
   *
   * @see Stemmer#stem(CharSequence)
   * @see TextParser#parse(String)
   */
  public static ArrayList<String> listStems(String line, Stemmer stemmer) {
    // TODO Fill in listStems(String, Stemmer)
	if (line == null) return new ArrayList<String>();
	if (stemmer == null) stemmer =  new SnowballStemmer(DEFAULT);
	
	//clean line and make sure it's not not
	String[] stemArray = cleanLineAndGetArrayOfStems(line);
	if (stemArray.length == 0) return new ArrayList<String>();
	
	//create list of stems
	ArrayList<String> stems = new ArrayList<>();
	for (String stem : stemArray) stems.add(stem);
	
	return stems;
  }

  /**
   * Returns a list of cleaned and stemmed words parsed from the provided line.
   *
   * @param line the line of words to clean, split, and stem
   * @return a list of cleaned and stemmed words
   *
   * @see SnowballStemmer
   * @see #DEFAULT
   * @see #listStems(String, Stemmer)
   */
  public static ArrayList<String> listStems(String line) {
    // THIS IS PROVIDED FOR YOU; NO NEED TO MODIFY
    return listStems(line, new SnowballStemmer(DEFAULT));
  }

  /**
   * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from the provided
   * line.
   *
   * @param line the line of words to clean, split, and stem
   * @param stemmer the stemmer to use
   * @return a sorted set of unique cleaned and stemmed words
   *
   * @see Stemmer#stem(CharSequence)
   * @see TextParser#parse(String)
   */
  public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
    // TODO Fill in uniqueStems(String, Stemmer)
	if (line == null) return new TreeSet<String>();
	if (stemmer == null) stemmer =  new SnowballStemmer(DEFAULT);
	
	//clean line and make sure it's not not
	String[] arrayOfStems = cleanLineAndGetArrayOfStems(line);
	if (arrayOfStems.length == 0) return new TreeSet<String>();
	
	//create list of stems
	TreeSet<String> stems = new TreeSet<String>();
	for (String stem : arrayOfStems) stems.add(stem);
	
	return stems;
  }

  /**
   * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from the provided
   * line.
   *
   * @param line the line of words to clean, split, and stem
   * @return a sorted set of unique cleaned and stemmed words
   *
   * @see SnowballStemmer
   * @see #DEFAULT
   * @see #uniqueStems(String, Stemmer)
   */
  public static TreeSet<String> uniqueStems(String line) {
    // THIS IS PROVIDED FOR YOU; NO NEED TO MODIFY
    return uniqueStems(line, new SnowballStemmer(DEFAULT));
  }

  /**
   * Reads a file line by line, parses each line into cleaned and stemmed words, and then adds those
   * words to a set.
   *
   * @param inputFile the input file to parse
   * @return a sorted set of stems from file
   * @throws IOException if unable to read or parse file
   *
   * @see #uniqueStems(String)
   * @see TextParser#parse(String)
   */
  public static TreeSet<String> uniqueStems(Path inputFile) throws IOException {
    // TODO Fill in uniqueStems(Path)
	if (inputFile == null) throw new IOException(String.format("The input file was null"));
	
	//Safety precautions
	try {
		//From Path to File 
		File input = inputFile.toFile();
		
		//IO issues with file that can be addressed with exception related to SecurityException
		if (!input.exists()) throw new IOException(String.format("The input file does not exist"));
		if (!input.isFile()) throw new IOException(String.format("The input file is not a file"));
		if (!input.canRead()) throw new IOException(String.format("The input file cannot be read"));
		
		//Try with resource to filter the lines from the input file
		try (BufferedReader fileReader = new BufferedReader( new FileReader(input))) {
			//declarations
			String line = null;
			String[] arrayOfStems = null;
			TreeSet<String> accumulatorTreeSet = new TreeSet<>();
			//Reading line by line
			while ((line = fileReader.readLine()) != null) {
				arrayOfStems = cleanLineAndGetArrayOfStems(line);
				
				if (arrayOfStems.length != 0) {
					for (String stem : arrayOfStems) accumulatorTreeSet.add(stem);
				}
			}
			
			return accumulatorTreeSet;
		//Exceptions only caused by the resource
		} catch (FileNotFoundException e) { throw new IOException("File reader could not open file");
		} catch (NullPointerException e) { throw new IOException("");
		}
	//Exceptions caused by the file and its methods
	} catch (UnsupportedOperationException e) { throw new IOException("Couldn't convert path to a file");
	} catch (SecurityException e) { throw new IOException("Read access to input is denied");
	} catch (IOException e) { throw new IOException();
	}
  }

  /**
   * Reads a file line by line, parses each line into cleaned and stemmed words, and then adds those
   * words to a set.
   *
   * @param inputFile the input file to parse
   * @return a sorted set of stems from file
   * @throws IOException if unable to read or parse file
   *
   * @see #uniqueStems(String)
   * @see TextParser#parse(String)
   */
  public static ArrayList<String> listStems(Path inputFile) throws IOException {
    // TODO Fill in uniqueStems(Path)
		if (inputFile == null) throw new IOException(String.format("The input file was null"));
		
		//Safety precautions
		try {
			//From Path to File 
			File input = inputFile.toFile();
			
			//IO issues with file that can be addressed with exception related to SecurityException
			if (!input.exists()) throw new IOException(String.format("The input file does not exist"));
			if (!input.isFile()) throw new IOException(String.format("The input file is not a file"));
			if (!input.canRead()) throw new IOException(String.format("The input file cannot be read"));
			
			//Try with resource to filter the lines from the input file
			try (BufferedReader fileReader = new BufferedReader( new FileReader(input))) {
				//declarations
				String line = null;
				String[] arrayOfStems = null;
				ArrayList<String> accumulatorArrayList = new ArrayList<>();
				//Reading line by line
				while ((line = fileReader.readLine()) != null) {
					arrayOfStems = cleanLineAndGetArrayOfStems(line);
					
					if (arrayOfStems.length != 0) {
						for (String stem : arrayOfStems) accumulatorArrayList.add(stem);
					}
				}
				
				return accumulatorArrayList;
			//Exceptions only caused by the resource
			} catch (FileNotFoundException e) { throw new IOException("File reader could not open file");
			} catch (NullPointerException e) { throw new IOException("");
			}
		//Exceptions caused by the file and its methods
		} catch (UnsupportedOperationException e) { throw new IOException("Couldn't convert path to a file");
		} catch (SecurityException e) { throw new IOException("Read access to input is denied");
		} catch (IOException e) { throw new IOException();
		}
  }

  /**
   * A simple main method that demonstrates this class.
   *
   * @param args unused
   * @throws IOException if an I/O error occurs
   */
  public static void main(String[] args) throws IOException {
    String text = "practic practical practice practiced practicer practices "
        + "practicing practis practisants practise practised practiser "
        + "practisers practises practising practitioner practitioners";

    System.out.println(uniqueStems(text));
    System.out.println(listStems(text));

    Path inputPath = Path.of("test", "animals.text");
    Set<String> actual = TextFileStemmer.uniqueStems(inputPath);
    System.out.println(actual);
  }
}
