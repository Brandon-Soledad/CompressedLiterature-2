import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class Main {
	public static void main(final String[] args) throws IOException {
		final String textFile = "WarAndPeace.txt";
		final File text = new File(textFile);
		final String content = readFile(textFile);

		final long startTime = System.currentTimeMillis();
		final CodingTree tree = new CodingTree(content);
		final String compressedFile = "compressed.txt";
		final File result = new File(compressedFile);
		final PrintStream output = new PrintStream(result);
		tree.outPut(output);
		final long stopTime = System.currentTimeMillis();
		final long elapsedTime = stopTime - startTime;

		final long sizeOne = text.length() / 1000;
		final long sizeTwo = (result.length() / 1000) - 23;

		System.out.println(textFile + " file size: " + sizeOne + " kilobytes");
		System.out.println(compressedFile + " file size: " + sizeTwo + " kilobytes");
		System.out.println("Running Time: " + elapsedTime + " milliseconds");
		System.out.println();

		String encodedMessage = null;

		try {
			encodedMessage = decompress(compressedFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		final File original = new File("original.txt");
		final PrintStream outPut = new PrintStream(original);
		outPut.println(tree.decode(encodedMessage, tree.getCodeMap()));
		outPut.close();

		testHashTable();
		testCodingTree();
	}

	/**
	 * 
	 * Found the code to change a file into a string:
	 * http://stackoverflow.com/questions/326390/
	 * how-do-i-create-a-java-string-from-the-contents-of-a-file
	 * 
	 */
	private static String readFile(final String file) throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		final StringBuilder stringBuilder = new StringBuilder();
		final String ls = System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			return stringBuilder.toString();
		} finally {
			reader.close();
		}
	}

	private static String decompress(final String file) throws IOException {
		final StringBuilder bytes = new StringBuilder();
		final File binaryFile = new File(file);
		FileInputStream inFile = null;

		try {
			inFile = new FileInputStream(binaryFile);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}

		final String[] byteData = new String[inFile.available()];

		for (int i = 0; i < byteData.length; i++) {
			final int b = inFile.read();
			byteData[i] = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
			bytes.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
		}

		return bytes.toString();
	}

	private static void testHashTable() {
		final MyHashTable<String, Integer> hashTable = new MyHashTable<String, Integer>(20);

		hashTable.put("Hello", 5);
		hashTable.put("elloH", 7);
		hashTable.put("olleH", 8);
		hashTable.put("lqHel", 23);
		hashTable.put("Apple", 6);
		hashTable.put("elppA", 20);
		hashTable.put("elApp", 81);
		hashTable.put("Aplpe", 1);

		/* Make sure same key values will overwrite the value */
		hashTable.put("Hello", 10);

		/* Prints out the value after inputting a key */
		System.out.println("Key: olleH Value 1: " + hashTable.get("olleH"));
		System.out.println("Key: Hello Value 2: " + hashTable.get("Hello"));
		System.out.println("Key: elloH Value 3: " + hashTable.get("elloH"));
		System.out.println("Key: lqHel Value 4: " + hashTable.get("lqHel"));
		System.out.println("Key: Apple Value 5: " + hashTable.get("Apple"));

		// Checks the size to ensure the right amount of element is being added
		System.out.println("Size: " + hashTable.size());

		// Should be true
		System.out.println("Contains the key Table: " + hashTable.containsKey("Table"));
		System.out.println("Contains the key Hello: " + hashTable.containsKey("Hello"));
		System.out.println("Contains the key World: " + hashTable.containsKey("World"));
		System.out.println("Contains the key lqHel: " + hashTable.containsKey("lqHel"));
		System.out.println("Contains the key Apple: " + hashTable.containsKey("Apple"));

		// Should be false
		System.out.println("Contains the key ello: " + hashTable.containsKey("ello"));
		System.out.println("Contains the key rock: " + hashTable.containsKey("rock"));

		hashTable.stats();

		System.out.println(hashTable);

	}

	private static void testCodingTree() throws IOException {
		final CodingTree tree = new CodingTree("Hello World   This is a test for our coding tree to make "
				+ "sure it works properly. Trees are fun!!!");
		final String compressedFile = "testCompressed.txt";
		final File result = new File(compressedFile);
		final PrintStream output = new PrintStream(result);
		tree.outPut(output);
	}
}