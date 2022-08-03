import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author UxDeveloperxU
 *
 */
public class MultithreadedIndexBuilder {
	

	/** logger */
	private static final Logger log = LogManager.getRootLogger();
	
	/**
	 * Build the Multithreading index from found tasks
	 * @param tasks work queue to provide tasks to
	 * @param pathEntry file path to build the inverted index from
	 * @return completed word index
	 * @throws IOException issues while reading file or attempting to open file
	 * @throws InterruptedException the work queue was interrupted
	 */
	public static WordIndex build(WorkQueue tasks, Path pathEntry) throws IOException, InterruptedException {
		MultithreadedWordIndex index = new MultithreadedWordIndex();
		MultithreadedIndexBuilder.findTasks(index, tasks, pathEntry);
		tasks.finish();
		return index;
	}
	
	/**
	 * A recursive call that creates a runnable for each file and
	 * recurses on a directory to keep looking for files to create 
	 * a runnable for
	 * @param index the word index
	 * @param tasks given work queue to use
	 * @param path file or directory to recurse on
	 * @param pathEntry directory of paths or a single path object
	 * @throws IOException issues while reading file or attempting to open file
	 */
	private static void findTasks(MultithreadedWordIndex index, WorkQueue tasks, Path pathEntry) throws IOException {
		List<Path> paths = TextFileFinder.list(pathEntry);
		for (Path path: paths) {
			if (Files.exists(path) && Files.isReadable(path) && TextFileFinder.isText.test(path)) {
				Runnable processorTask = new MultithreadedIndexBuilder.ProcessorTask(index, path, pathEntry);
				tasks.execute(processorTask);
			}
		}
	}
	
	/**
	 * Runnable for processing the word index for a given path
	 * Note: could be a separate class but I would rather
	 * hide some of the implementation
	 * @author Alex L Hanson
	 */
	public static class ProcessorTask implements Runnable {
		/** word index to add stems to */
		private final MultithreadedWordIndex index;
		/** path to find stems */
		private final Path path;
		/** path file or directory to relativize */
		private final Path pathEntry;
		/**
		 * Constructor 
		 * @param index the word index
		 * @param path path to find stem
		 * @param pathEntry path file or directory to relativize
		 */
		public ProcessorTask(MultithreadedWordIndex index, Path path, Path pathEntry) {
			this.index = index;
			this.path = path;
			this.pathEntry = pathEntry;
		}
		
		@Override
		public void run() {
			// get the unique stems and their positions for a given text file
			try {
				//using a map is faster than using a WordIndex //but using a word index is more readable //so it is better to return the code to incorporate this initially
				WordIndex local = new WordIndex();
				//formatted file path
				String correctedFilePath = TextFileFinder.pathRelativizer.apply(pathEntry, path);
				//initialize file count //parsed file 
				IndexBuilder.parseFile(local, path, correctedFilePath);
				//add map to word index
				index.addAll(local, correctedFilePath);
				//add file count if necessary
				index.addFileCount(correctedFilePath, local.getFileCount(correctedFilePath));
			} catch (NullPointerException e) {
				log.warn("Either one of the paths given was null or the index could be null");
			} catch (FileNotFoundException e) {
				log.warn("file path was not found");
			} catch (UnsupportedOperationException e) {
				log.warn("Couldn't convert path to a file");
			} catch (IOException e) {
				log.warn("Error occurred creating tree map");
			}
		}
	}
}
