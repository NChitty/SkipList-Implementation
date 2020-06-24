import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public class SkipListSet<T extends Comparable<? super T>> implements SortedSet<T> {

	private SkipListNode<T> head;
	private int size = 0;
	private int level = 0;
	private final SecureRandom random = new SecureRandom();

	public SkipListSet(Collection<? extends T> collection) {
		this.head = (new SkipListNode<T>(collection.size() + 1));
		level = 1;
		this.addAll(collection);
	}

	public SkipListSet() {
		this.head = (new SkipListNode<T>(8));
		this.level = 1;
	}

	@Override
	public boolean add(T arg0) {
		if (size + 1 > head.height) {
			head.changeHeight(head.height * 2);
		}
		SkipListNode<T> current = head;
		//start at the level where head is pointing to the highest next
		//element decrement down until we are at the bottom
		for(int i = level; i >= 0; i--) {
			while(current.next[i] != null && current.next[i].element.compareTo(arg0) < 0) {
				current = current.next[i];
			}
			//move down by decrementing
		}
		// Create the new node, the constructor handles the rest
		SkipListNode<T> newNode = new SkipListNode<T>(1 + randomHeight(), current, arg0);
		// if the newNode height is greater than the level we are currently at then
		// change the level we start searching at
		if (newNode.height > level) {
			level = newNode.height - 1;
		}
		size++;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		for (T o : arg0)
			this.add(o);
		return true;
	}

	@Override
	public void clear() {
		SkipListNode<T> current = head.next[0];
		while (current.next[0] != null) {
			current.remove();
			current = current.next[0];
		}
	}

	@Override
	@SuppressWarnings(value = "unchecked")
	public boolean contains(Object arg0) {
		SkipListNode<T> current = head;
		for(int i = level; i >= 0; i--) {
			while(current.next[i] != null && current.next[i].element.compareTo((T) arg0) <= 0) {
				current = current.next[i];
			}
			if(current.element != null && current.element.compareTo((T) arg0) == 0) return true;
			//move down by decrementing
		}
		return false;
	}

	/**
	 * Basically do all the elements in this collection reside in <code>arg0</code>
	 * In other words, is my set a subset of the collection
	 * 
	 * @param arg0
	 */
	@Override
	public boolean containsAll(Collection<?> arg0) {
		for (T a : this)
			if (!arg0.contains(a))
				return false;
		return true;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new SkipListIterator<T>(head);
	}

	@Override
	@SuppressWarnings(value = "unchecked")
	public boolean remove(Object arg0) {
		if (this.contains(arg0)) {
			this.walkTo((T) arg0).remove();
			size--;
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		for (Object o : arg0)
			if (!this.remove(o))
				return false;
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		for (T element : this) {
			if (!arg0.contains(element))
				this.remove(element);
		}
		return false;
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public Object[] toArray() {
		Object[] returnedArray = new Object[size];
		SkipListIterator<T> iterator = (SkipListIterator<T>) iterator();
		for (int i = 0; i < size; i++) {
			if (!iterator.hasNext()) {
				returnedArray[i] = null; // padded by nulls
				return returnedArray;
			}
			returnedArray[i] = iterator.next();
		}
		return returnedArray;
	}

	@Override
	@SuppressWarnings(value = "unchecked")
	/**
	 * Found a sample on how to get a typed generic array from stack overflow here:
	 * https://stackoverflow.com/questions/4010924/java-how-to-implement-toarray-for-collection
	 */
	public <T> T[] toArray(T[] arg0) {
		T[] returnedArray = arg0.length >= size ? arg0
				: (T[]) Array.newInstance(arg0.getClass().getComponentType(), size);
		SkipListIterator<T> iterator = (SkipListIterator<T>) iterator();
		for (int i = 0; i < size; i++) {
			if (!iterator.hasNext()) {
				if (arg0 != returnedArray)
					return Arrays.copyOf(returnedArray, i);
				returnedArray[i] = null; // padded by nulls
				return returnedArray;
			}
			returnedArray[i] = iterator.next();
		}
		return returnedArray;
	}

	@Override
	public Comparator<? super T> comparator() {
		return null;
	}

	@Override
	public T first() {
		return head.next[0].element;
	}

	@Override
	public SortedSet<T> headSet(T arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T last() {
		SkipListNode<T> current = head;
		for (int i = level; i >= 0; i--) {
			while (current.next.length > i && current.next[i] != null) {
				current = current.next[i];
			}
		}
		return current.element;
	}

	@Override
	public SortedSet<T> subSet(T arg0, T arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedSet<T> tailSet(T arg0) {
		throw new UnsupportedOperationException();
	}

	public void reBalance() {
		SkipListNode<T> current = head.next[0];
		while (current.next[0] != null) {
			int newHeight = 1 + randomHeight();
			current.changeHeight(newHeight);
			current = current.next[0];
		}
	}

	private SkipListNode<T> walkTo(T element) {
		SkipListNode<T> current = head;
		// until we reach the bottom level
		for (int i = level; i >= 0; i--) {
			// check to see that next at the current height exists and that the next element
			// is less than the one being inserted
			while ((current.next().length > i) && (current.next()[i].element.compareTo(element) < 0)) {
				// move to the right
				current = current.next()[i];
			}
			if (current.element.compareTo(element) == 0)
				return current;
			// move down by decrementing
		}
		return null;
	}

	private int randomHeight() {
		int height = 0;
		boolean heads = random.nextBoolean();
		while (height + 1 < head.height && heads) {
			height++;
			heads = random.nextBoolean();
		}
		return height;
	}

	private class SkipListNode<T> {
		private final T element;
		private SkipListNode<T>[] next, previous;
		private int height;
		private boolean head = false;

		/**
		 * Use this to build the head because all other nodes need a prev and next
		 * 
		 * @param height
		 */
		@SuppressWarnings(value = "unchecked")
		public SkipListNode(int height) {
			this.head = true;
			this.element = null;
			this.height = height;
			next = (SkipListNode<T>[]) Array.newInstance(this.getClass(), height);
			previous = null;
		}

		@SuppressWarnings(value = "unchecked")
		public SkipListNode(int height, SkipListNode<T> predecessor, T element) {
			this.element = element;
			this.height = height;
			next = (SkipListNode<T>[]) Array.newInstance(this.getClass(), height);
			previous = (SkipListNode<T>[]) Array.newInstance(this.getClass(), height);
			SkipListNode<T> current;
			for(int i = 0; i < height; i++) {
				current = predecessor;
				//starting from the bottom find the closest previous stack with
				//a next[i]
				while(i >= current.height) {
					current = current.previous[current.height-1];
				}
				//make link
				this.makeLinks(i, current);
			}
		}

		public void changeHeight(int newHeight) {
			if (newHeight > this.height) {
				//Increasing height
				this.next = (SkipListNode<T>[]) Arrays.copyOf(this.next, newHeight);
				if (!this.head) {
					this.previous = (SkipListNode<T>[]) Arrays.copyOf(this.previous, newHeight);
					SkipListNode<T> current = this.previous[this.height- 1];
					for (int i = this.height; i < newHeight; i++) {
						while(current.previous != null && i >= current.height) {
							current = current.previous[current.height-1];
						}
						this.makeLinks(i, current);			
					}
				}
				this.height = newHeight;
			} else {
				//decreasing height
				int i = this.height - 1;
				while (i >= newHeight) {
					this.removeLinks(i);
					i--;
				}
				this.height = newHeight;
				this.next = (SkipListNode<T>[]) Arrays.copyOf(this.next, newHeight);
				if (!this.head)
					this.previous = (SkipListNode<T>[]) Arrays.copyOf(this.previous, newHeight);
			}
		}

		public void makeLinks(int i, SkipListNode<T> predecessor) {
			this.next[i] = predecessor.next[i];
			this.previous[i] = predecessor;
			predecessor.next[i] = this;
			if(this.next[i] != null) {
				this.next[i].previous[i] = this;
			}
		}

		public void removeLinks(int i) {
			this.previous[i].next[i] = this.next[i];
			if(this.next[i] != null) {
				this.next[i].previous[i] = this.previous[i];
			}
		}

		public void remove() {
			for (int i = height - 1; i >= 0; i--) {
				removeLinks(i);
			}
		}

		public SkipListNode<T>[] previous() {
			return this.previous;
		}

		public SkipListNode<T>[] next() {
			return this.next;
		}

	}

	private class SkipListIterator<T> implements Iterator<T> {

		SkipListNode<T> current;

		private SkipListIterator(final SkipListNode<T> head) {
			this.current = head;
		}

		@Override
		public boolean hasNext() {
			return current.next()[0] != null;
		}

		@Override
		public T next() {
			current = current.next()[0];
			return current.element;
		}

		@Override
		public void remove() {
			current.remove();
		}

	}
}