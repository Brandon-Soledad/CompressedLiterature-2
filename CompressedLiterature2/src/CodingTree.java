import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


public class CodingTree {
	public static final int RANGE = 8;
	
	private final MyHashTable<String, String> Codes;
	
	private final MyHashTable<String, Integer> wordFrequency;
	
	private final List<String> Words;

	private final PriorityQueue<TreeNode> Queue;
	
	private TreeNode firstTree;
	
	private final StringBuilder coding;
		
	/**
	 * Constructs a coding tree that initializes all the fields
	 * 
	 * @param message a String
	 * @throws FileNotFoundException
	 */
	public CodingTree(final String message) throws FileNotFoundException {
		Codes = new MyHashTable<String, String>(32768);
		wordFrequency = new MyHashTable<String, Integer>(32768);
		Words = new ArrayList<String>();
		Queue = new PriorityQueue<TreeNode>();
		coding = new StringBuilder();
		final File out = new File("output.txt");
		final PrintStream output = new PrintStream(out);
		breakString(message);
		frequency();
		buildTree();
		bitMap(firstTree, "");
		checkMapCoding(output);
		encode();
	}
	
	/**
	 * Breaks the string into word tokens
	 * 
	 * @param text a String
	 */
	private void breakString(final String text) {
		String word = "";
		for(final char ch : text.toCharArray()) {
			if(!isWord(ch)) {
				if(word.length() > 0) {
					Words.add(word);
				} 
				word = "" + ch;
				if(word.length() > 0) {
					Words.add(word);
				}
				word = "";
			} else {
				word += ch;
			}
		}
		if(word.length() > 0) {
			Words.add(word);
		}	
	}
	
	/**
	 * Checks to see if character is a letter, number, apostrophe, hyphen
	 * @param ch a char
	 * @return a boolean
	 */
	private boolean isWord(final char ch){
		return (Character.isLetter(ch))||(Character.isDigit(ch))||(ch=='\'')||(ch=='-');
	}
	/**
	 * Counting the frequency of each character in the text file
	 */
	private void frequency() {
		for(int i = 0; i < Words.size(); i++) {
			if(Words.get(i) != null) {
				if(!wordFrequency.containsKey(Words.get(i))) {
					wordFrequency.put(Words.get(i), 1);
				} else {
					int count = wordFrequency.get(Words.get(i));
					count++;
					wordFrequency.put(Words.get(i), count);
				}
			}
		}
		for(final String word : Words) {
			if(!wordFrequency.containsKey(word)) {
				wordFrequency.put(word, 1);
			} else {
				int count = wordFrequency.get(word);
				count++;
				wordFrequency.put(word, count);
			}
		}
	}
	
	/**
	 * Creates a tree with all the character using the Huffman's Algorithm
	 */
	private void buildTree() {
		final Set<String> entry = wordFrequency.entrySet();

		for(final String word : entry) {
			final TreeNode newTree = new TreeNode(word, null, null,
					wordFrequency.get(word)) ;
			Queue.offer(newTree);
		}

		TreeNode firstMin;
		TreeNode secondMin;
		while(Queue.size() > 1) {
			firstMin = Queue.poll();
			secondMin = Queue.poll();

			final int combineWeight = firstMin.getWeight() + secondMin.getWeight();
			final TreeNode root = new TreeNode(null, firstMin, secondMin, combineWeight);
			Queue.offer(root);
		}
		firstTree = Queue.poll();
	}	
	/**
	 * Finds the bit coding for each word
	 */
	private void bitMap(final TreeNode node, final String code) {
		// If it is a leaf then store the code into the map
		if(node.isLeaf()) {
			Codes.put(node.getData(), code);
		} else {
			// Traverse through the this.left side
			bitMap(node.getLeft(), code + 0);
			
			// Traverse through the this.right side
			bitMap(node.getRight(), code + 1);
		}
	}
	
	/**
	 * Checks the value of the map coding to ensure it is working properly
	 * This is used for testing.
	 */
	public void checkMapCoding(final PrintStream output) {
		final Set<String> entry = Codes.entrySet();
		int count = 0;
		for(final String word : entry) {
			output.println("" + count + " Key: " + word + " Value: " + Codes.get(word));
			count++;
		}
	}
	
	/**
	 * Encodes the String with the Map coding
	 */
	private void encode() {
		for(int i = 0; i < Words.size(); i++) {
			coding.append(Codes.get(Words.get(i)));
		}
	}
	/**
	 * 
	 * 
	 * @param out the printstream
	 * @throws IOException
	 */
	public void outPut(final PrintStream out) throws IOException {
		String partial;
		int part;
		final int rounds = coding.length() / RANGE;
		for(int i = 0; i < rounds * RANGE;  i += RANGE) {
			partial = coding.substring(i, i + RANGE);
			part = Integer.parseInt(partial, 2);
			final Byte b = (byte)part;
			out.write(b);
		}
		partial = coding.substring(rounds * RANGE, coding.length());
	}
	
	public MyHashTable<String, String> getCodeMap() {
		return Codes;
	}
	
	public String decode(final String bits, final MyHashTable<String, String> Codes) {
		final StringBuilder decodedMessage = new StringBuilder();
		final Map<String, String> codesReversed = new HashMap<String, String>();
		for (final String word : Codes.entrySet()) {
			final String code = Codes.get(word);
			codesReversed.put(code, word);
		}
		
		final StringBuilder subEncoded = new StringBuilder();
		String wordTemp;
		
		for (int i = 0; i < bits.length(); i++) {
			subEncoded.append(bits.charAt(i));
			wordTemp = codesReversed.get(subEncoded.toString());
			if (wordTemp != null) {
				decodedMessage.append(wordTemp);
				subEncoded.setLength(0);
			}
		}
		return decodedMessage.toString();
	}
	
	public class TreeNode implements Comparable<TreeNode> {
		private final TreeNode left;
	
		private final TreeNode right;
		
		private final String data;
		
		private final int weight;
		
		/**
		 * A constructor of the TreeNode that initializes the fields
		 * 
		 * @param data of the Character
		 * @param .left is TreeNode
		 * @param right is TreeNode
		 * @param weight is the frequency
		 */
		public TreeNode(final String data, final TreeNode left, final TreeNode right, final int weight) {
			this.data = data;
			this.left = left;
			this.right = right;
			this.weight = weight;
		}
		
		/**
		 * Checks to see if the TreeNode is a leaf
		 * 
		 * @return a boolean
		 */
		public boolean isLeaf() {
			return (left == null && right == null);
		}
		
		/**
		 * Returns the character data
		 * 
		 * @return a Character
		 */
		public String getData() {
			return data;
		}
		
		/**
		 * Gets the frequency of the character
		 * 
		 * @return an int.
		 */
		public int getWeight() {
			return weight;
		}
		
		/**
		 * Gets the this.left node of this TreeNode
		 * 
		 * @return a TreeNode
		 */
		public TreeNode getLeft() {
			return left;
		}
		
		/**
		 * Gets the this.right node of this TreeNode
		 * 
		 * @return a TreeNode
		 */
		public TreeNode getRight() {
			return right;
		}
		
		/**
		 * Compares with another TreeNode to see which TreeNode 
		 * is larger
		 * 
		 * @return an int
		 */
		@Override
		public int compareTo(final TreeNode other) {
			final TreeNode node = other;
			int compare = 0;
			if(weight > node.getWeight()) {
				compare = 1;
			} else if(weight < node.getWeight()) {
				compare = -1;
			}
			return compare;
		}
		
		/**
		 * A string representation of the TreeNode
		 * 
		 * @return a String
		 */
		public String toString() {
			return "Character: " + data +" Weight: "+ weight;
			
		}
	}
}