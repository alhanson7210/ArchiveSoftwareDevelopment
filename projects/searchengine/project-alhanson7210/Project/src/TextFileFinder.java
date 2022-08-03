import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A utility class for finding all text files in a directory using lambda functions and streams.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Alex L Hanson
 * @version Spring 2020
 */
public class TextFileFinder {

	/**
	 * root logger
	 */
	private static final Logger log = LogManager.getRootLogger();
	
	/**
	 * Given a json File, create the file and return the generated path
	 * 
	 * parameter -> jsonFile -> assumes to be given a valid file needed to be generated
	 * 
	 * returns -> the generated path or null
	 * 
	 */
	public static final Function<String, Path> pathGenerator = (filePath) -> {
		if (filePath == null) {
			return null;
		}
		Path path = null;
		try (OutputStream out = Files.newOutputStream(path = Path.of(filePath), StandardOpenOption.CREATE)) {
			log.info("Created a path for the given filePath " + filePath);
		} catch(IOException e) {
			log.warn("Failed To Create a path " + filePath + " because of an IO Error");
		}
		return path;
	};
	
	/**
	 * Corrects a malformed file path against a directory
	 * in order to get a correct relative path
	 * 
	 * parameter -> entry -> path to a file or path of a directory
	 * parameter -> givenPath -> malformed file path
	 * 
	 * return -> corrected relative path adjusted by a directory or the given text file path
	 *
	 * @apiNote Due to using find instead of walk, #TextFileFinder() will grab all text files 
	 * from the entry as expected, and {@link #find(Path)} will maintain the file path for 
	 * all symbolic links found. Simply using the {@link Path#resolve(Path)} and 
	 * {@link Path#relativize(Path)} methods do not yield the correct relative path if there 
	 * are nested directories in the entry. The main issue was not using {@link Path#toString()} on the givenPath
	 * 
	 * @see #TextFileFinder() {@link #find(Path)}
	 * 
	 */
	public static final BiFunction<Path,Path, String> pathRelativizer = (entry, givenPath) -> {
		//entry assumed to be a file
		String filePath = entry.toString();
		//checks if the entry is a directory
		if (!TextFileFinder.isText.test(entry)) {
			filePath = givenPath.toString();
		}
		//corrected relative path adjusted by a directory or the given text file path
		return filePath;
	};

	/**
	 * A lambda function that returns true if the path is a file that ends in a .txt or .text
	 * extension (case-insensitive). Useful for {@link Files#walk(Path, FileVisitOption...)}.
	 *
	 * @see Files#isRegularFile(Path, java.nio.file.LinkOption...)
	 * @see Path#getFileName()
	 * @see Files#walk(Path, FileVisitOption...)
	 */
	public static final Predicate<Path> isText = 
			(path) -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)
			&& (path.getFileName().toString().toLowerCase().endsWith(".txt") 
					|| path.getFileName().toString().toLowerCase().endsWith(".text"));

	/**
	 * A lambda function that returns true if the path is a file that ends in a .txt or .text
	 * extension (case-insensitive). Useful for
	 * {@link Files#find(Path, int, BiPredicate, FileVisitOption...)}.
	 *
	 * @see Files#find(Path, int, BiPredicate, FileVisitOption...)
	 */
	// DO NOT MODIFY; THIS IS PROVIDED FOR YOU
	// (Hint: This is only useful if you decide to use Files.find(...) instead of Files.walk(...)
	public static final BiPredicate<Path, BasicFileAttributes> isTextWithAttribute =
			(path, attr) -> isText.test(path);

	/**
	 * Returns a stream of text files, following any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @return a stream of text files
	 *
	 * @throws IOException if an I/O error occurs
	 *
	 * @see #isText
	 * @see #isTextWithAttribute
	 *
	 * @see FileVisitOption#FOLLOW_LINKS
	 * @see Files#walk(Path, FileVisitOption...)
	 * @see Files#find(Path, int, java.util.function.BiPredicate, FileVisitOption...)
	 *
	 * @see Integer#MAX_VALUE
	 */
	public static Stream<Path> find(Path start) throws IOException {
		return Files.find(start, Integer.MAX_VALUE, isTextWithAttribute, FileVisitOption.FOLLOW_LINKS);
	}

	/**
	 * Returns a list of text files.
	 *
	 * @param start the initial path to search
	 * @return list of text files
	 * @throws IOException if an I/O error occurs
	 *
	 * @see #find(Path)
	 */
	public static final List<Path> list(Path start) throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU DO NOT MODIFY
		return find(start).collect(Collectors.toList());
	}
}