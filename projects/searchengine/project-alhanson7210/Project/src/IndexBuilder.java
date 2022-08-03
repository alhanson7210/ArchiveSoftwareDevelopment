import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author UxDeveloperxU
 *
 */
public class IndexBuilder {
	
	/** root logger */
	private static final Logger log = LogManager.getRootLogger();
	
	/**
	 * Build the inverted Index and return it
	 * @param pathEntry file path to build the inverted index from
	 * @return completed index
	 * @throws IOException issues while reading file or attempting to open file
	 * @throws UnsupportedOperationException user error related to invalid object use
	 * @throws FileNotFoundException invalid path or file doesn't exist on the file system
	 * @throws NullPointerException an argument was given as null
	 */
	public static WordIndex build(Path pathEntry) throws NullPointerException, FileNotFoundException, UnsupportedOperationException, IOException {
		WordIndex invertedIndex = new WordIndex();
		// this should get back a stream of all text files
		log.info("The path entry is: " + pathEntry);
		//list of paths
		List<Path> pathList = TextFileFinder.list(pathEntry);
		log.info("Generating map of stem positions");
		//loop through the path list
		for (Path path: pathList) {
			// format the given path
			String correctedFilePath = TextFileFinder.pathRelativizer.apply(pathEntry, path);
			//parse file and add file count if necessary
			parseFile(invertedIndex, path, correctedFilePath);
		}
		return invertedIndex;
	}
	
	/**
	 * Parse file and add file count
	 * @param invertedIndex accumulating, inverted index
	 * @param path file path from a stream of all text files
	 * @param correctedFilePath formatted file path
	 * @throws NullPointerException an argument was given as null
	 * @throws FileNotFoundException invalid path or file doesn't exist on the file system
	 * @throws UnsupportedOperationException user error related to invalid object use
	 * @throws IOException issues while reading file or attempting to open file
	 */
	public static void parseFile(WordIndex invertedIndex, Path path, String correctedFilePath) throws NullPointerException, FileNotFoundException, UnsupportedOperationException, IOException {
		TreeMap<String, TreeSet<Integer>> fileStems = TextFileStemmer.generateStemPositionMap(path);
		//initialize file count
		int fileCount = 0;
		// loop through the file stem entries
		for (String filePath : fileStems.keySet()) {
			//add all positions for stem
			Collection<Integer> stemPositions = fileStems.get(filePath);
			invertedIndex.addAll(filePath, correctedFilePath, stemPositions);
			//add file count
			fileCount += stemPositions.size();
		}
		//set file path word count
		invertedIndex.addFileCount(correctedFilePath, fileCount);
	}
}
