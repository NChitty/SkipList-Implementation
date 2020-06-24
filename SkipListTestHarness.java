import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

public class SkipListTestHarness {
	private static class CPUTimer {
		public static <T> long timeFor(Callable<T> task) {
			try {
				long start = System.currentTimeMillis();
				T t = task.call();
				long end = System.currentTimeMillis();
				return end - start;
			} catch (Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}
			return 0;
		}
	}
	
	static long RandomSeed = 1;

	static Random RandomGenerator = new Random(RandomSeed);
	static byte[] buf = new byte[1024];
	
	private static ArrayList<Integer> generateIntArrayList(int howMany) {
		ArrayList<Integer> list = new ArrayList<Integer>(howMany);
		
		for(int i = 0; i < howMany; i++) {
			list.add(Integer.valueOf(RandomGenerator.nextInt()));
		}
		
		return list;
	}
	
	private static ArrayList<Double> generateDoubleArrayList(int howMany) {
		ArrayList<Double> list = new ArrayList<Double>(howMany);
		
		for(int i = 0; i < howMany; i++) {
			list.add(Double.valueOf(RandomGenerator.nextDouble()));
		}
		
		return list;
	}

	private static String generateRandomString(int len) {
		if(len > 1024) len = 1024;
		
		buf[len - 1] = (byte) 0;

		for(int j = 0; j < (len - 1); j++) {
			buf[j] = (byte) (RandomGenerator.nextInt(94) + 32);
		}

		return new String(buf);
	}
	
	private static ArrayList<String> generateStringArrayList(int howMany, int len) {
		ArrayList<String> list = new ArrayList<String>(howMany);
		
		for(int i = 0; i < howMany; i++) {
			list.add(generateRandomString(len));
		}
		
		return list;
	}
	
	private static <T> ArrayList<T> generateStrikeList(ArrayList<? extends T> fromList, int howMany) {
		ArrayList<T> strikeList = new ArrayList<T>(howMany);
		int fromLast = fromList.size() - 1;
		
		for(int i = 0; i < howMany; i++) {
			strikeList.add(fromList.get(RandomGenerator.nextInt(fromLast)));
		}
		
		return strikeList;
	}

	private static <T> int executeFinds(Collection<? extends T> coll, ArrayList<? extends T> strikes) {
		boolean sentinel;
		int failures = 0;

		for (T e: strikes) {
			sentinel = coll.contains(e);
			if(sentinel == false) {
				failures++;
			}
		}
		
		if(failures > 0) {
			System.out.printf("    %,d find failures\n", failures);
		}
		
		return 0;
	}

	private static <T extends Comparable<T>> void executeCase(ArrayList<? extends T> values, ArrayList<? extends T> strikes, boolean includeLinkedList) {
		LinkedList<T> linkedList = new LinkedList<T>();
		TreeSet<T> treeSet = new TreeSet<T>();
		SkipListSet<T> skipListSet = new SkipListSet<T>();
		long start;
		long end;
		long ms;
		
		if(includeLinkedList) {
			ms = CPUTimer.timeFor(() -> linkedList.addAll(values));
			System.out.printf("  LinkedList add:  %,d ms\n", ms);
			ms = CPUTimer.timeFor(() -> executeFinds(linkedList, strikes));
			System.out.printf("  LinkedList find: %,d ms\n", ms);
		}
		
		ms = CPUTimer.timeFor(() -> skipListSet.addAll(values));
		System.out.printf("  SkipListSet add:  %,d ms\n", ms);
		ms = CPUTimer.timeFor(() -> executeFinds(skipListSet, strikes));
		System.out.printf("  SkipListSet find: %,d ms\n", ms);

		start = System.currentTimeMillis();
		skipListSet.reBalance();
		end = System.currentTimeMillis();
		ms = end - start;
		System.out.printf("  SkipListSet rebalance: %,d ms\n", ms);
		ms = CPUTimer.timeFor(() -> executeFinds(skipListSet, strikes));
		System.out.printf("  SkipListSet find: %,d ms\n", ms);
		
		ms = CPUTimer.timeFor(() -> treeSet.addAll(values));
		System.out.printf("  TreeSet add:  %,d ms\n", ms);
		ms = CPUTimer.timeFor(() -> executeFinds(treeSet, strikes));
		System.out.printf("  TreeSet find: %,d ms\n", ms);
		
		System.out.println("");
	}

	public static void executeStringCase(int listSize, int strikeSize, int stringSize, boolean includeLinkedList) {
		System.out.printf("Generating string case for %,d strings of length %,d, with %,d finds...\n", listSize, stringSize, strikeSize);

		ArrayList<String> strings = generateStringArrayList(listSize, stringSize);
		ArrayList<String> strikes = generateStrikeList(strings, strikeSize);
		
		System.out.printf("  Case generated\n");
		executeCase(strings, strikes, includeLinkedList);
	}
	
	public static void executeIntCase(int listSize, int strikeSize, boolean includeLinkedList) {
		System.out.printf("Generating integer case for %,d integers with %,d finds...\n", listSize, strikeSize);

		ArrayList<Integer> intlist = generateIntArrayList(listSize);
		ArrayList<Integer> strikes = generateStrikeList(intlist, strikeSize);
		
		System.out.printf("  Case generated\n");
		executeCase(intlist, strikes, includeLinkedList);
	}
	
	public static void executeDoubleCase(int listSize, int strikeSize, boolean includeLinkedList) {
		System.out.printf("Generating double case for %,d doubles with %,d finds...\n", listSize, strikeSize);

		ArrayList<Double> doubles = generateDoubleArrayList(listSize);
		ArrayList<Double> strikes = generateStrikeList(doubles, strikeSize);
		
		System.out.printf("  Case generated\n");
		executeCase(doubles, strikes, includeLinkedList);
	}
	
	public SkipListTestHarness() {}
	
	public static void main(String args[]) {
		SkipListTestHarness.executeStringCase(10000, 1000, 1000, true);
		SkipListTestHarness.executeStringCase(100000, 1000, 1000, true);
		SkipListTestHarness.executeStringCase(1000000, 1000, 1000, true);
		SkipListTestHarness.executeStringCase(1000000, 100000, 1000, false);
		SkipListTestHarness.executeIntCase(100000, 1000, true);
		SkipListTestHarness.executeIntCase(1000000, 1000, true);
		SkipListTestHarness.executeIntCase(10000000, 1000, false);
		SkipListTestHarness.executeIntCase(10000000, 1000000, false);	
		SkipListTestHarness.executeDoubleCase(100000, 1000, true);
		SkipListTestHarness.executeDoubleCase(1000000, 1000, true);
	}
}