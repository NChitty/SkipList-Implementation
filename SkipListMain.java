import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;
import java.util.SortedSet;

public class SkipListMain {

	public class SkipListSet<T extends Comparable> implements SortedSet<T> {

		private SkipListNode<T> head;
		private int size = 0;
		private final SecureRandom random = new SecureRandom();

		public SkipListSet(int height) {
			this.head = (new SkipListNode<T>(height)).top();
		}

		public SkipListSet() {
			this.head = (new SkipListNode<T>(2)).top();
		}

		@Override
		public boolean add(T arg0) {
			if(size + 1 == head.height) {
				head.changeStackHeight(head.height*2);
				head = head.top();
			}
			SkipListNode<T> current = head;
			//while we are not on the bottom level
			while(current.down != null) {
				//check next element on this level exists
				if(current.right != null) {
					//if this level's element is less than the element we are inserting, move right
					if(current.right.element.compareTo(arg0) < 0) {
						current = current.right;
						continue; //we dont want to move down after moving right
					} else if(current.right.element.compareTo(arg0) == 0) {
						//we want to move down and add after this element
						current = current.right.down;
						continue;
					}
				}
				//the next element to the right is either null or greater than the insertion so we move down
				current = current.down;
			}
			//Create the new node, the constructor handles the rest
			new SkipListNode<T>(1 + this.randomHeight(), current, current.right, arg0);
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends T> arg0) {
			for(T o : arg0)
				this.add(o);
			return true;
		}

		@Override
		public void clear() {
			SkipListNode<T> current = head.top();
			while(current.down != null) {
				current.right = null;
				current = current.down;
			}
		}

		@Override
		public boolean contains(Object arg0) {
			SkipListNode<T> current = head.top();
			while(current.down != null) {
				//check if there is a node to the right
				if(current.right != null) {
					//if the element to the right is less the the element we are searching for move right
					if(current.right.element.compareTo(arg0) < 0) {
						current = current.right;
						continue; //do not move down
					} else if (current.right.element.compareTo(arg0) == 0)
						return true; //compareTo returned 0 so we found it
				}
				//there was not a node to the right or it was greater so we move down
				current = current.down;
			}
			return false;
		}

		/**
		 * Basically do all the elements in this collection reside in <code>arg0</code>
		 * In other words, is my set a subset of the collection
		 * @param arg0
		 */
		@Override
		public boolean containsAll(Collection<?> arg0) {
			for(T a : this)
				if(!arg0.contains(a)) return false;
			return true;
		}

		@Override
		public boolean isEmpty() {
			return size == 0;
		}

		@Override
		public Iterator<T> iterator() {
			Iterator<T> retVal = new Iterator<T>() {

				SkipListNode<T> current = head.bottom(); //we want the bottom layer to get all the elements

				@Override
				public boolean hasNext() {
					return current.right != null; //if the right node is null then there is nothing to go to
				}

				@Override
				public T next() {
					current = current.right; //move the cursor to the right
					return current.element; //return the current element
				}
			};
			return retVal;
		}

		@Override
		public boolean remove(Object arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int size() {
			return this.size;
		}

		@Override
		public Object[] toArray() {
			return null;
		}

		@Override
		public <T> T[] toArray(T[] arg0) {
			T[] retVal = Arrays.copyOf(arg0, size);
			int i = 0;
			Iterator iterator = this.iterator();
			while(iterator.hasNext()) {
				retVal[i] = (T) iterator.next();
				i++;
			}
			return retVal;
		}

		@Override
		public Comparator<? super T> comparator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public T first() {
			return head.bottom().right.element;
		}

		@Override
		public SortedSet<T> headSet(T arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public T last() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SortedSet<T> subSet(T arg0, T arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SortedSet<T> tailSet(T arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		private int randomHeight() {
			int range = Integer.MAX_VALUE % head.height;
			int randNum = random.nextInt() % head.height;
			int valToReturn = 0;
			while(randNum > range/2) {
				valToReturn++;
				randNum -= (range/2);
				range /= 2;
			}
			return valToReturn;
		}
	}

	public class SkipListNode<T> {
		private T element;
		private SkipListNode<T> left, right, up, down;
		private int height;
		private boolean head = false;

		/**
		 * Use this to build the head because all other nodes need a prev and next
		 * 
		 * @param height
		 */
		public SkipListNode(int height) {
			this.head = true;
			this.element = null;
			this.height = height;
			this.left = null;
			this.right = null;
			this.buildStack();
		}

		/**
		 * Since all new values are going to be added at the bottom of the skip list the
		 * stack of nodes can still be built bottom to top
		 * 
		 * @param height  the randomized height
		 * @param left    the node preceding this one
		 * @param right   the node succeeding this one
		 * @param element the element being inserted
		 */
		public SkipListNode(int height, SkipListNode<T> left, SkipListNode<T> right, T element) {
			this.height = height;
			this.left = left;
			this.right = right;
			this.element = element;
			this.buildStack();
			//change the left and right stacks to point to this node
			left.right = this;
			right.left = this;
		}

		/**
		 * This constructor is only for building nodes up, hence private
		 * 
		 * @param down
		 */
		private SkipListNode(SkipListNode<T> down) {
			// inherit the height and the element
			this.height = down.height;
			this.element = down.element;
			// get the previous node in the stack
			this.down = down;
			// copy the status of whether this is the head or not
			this.head = down.head;
			// get the predecessor
			SkipListNode<T> prevStack = this.down.left;
			// check the predecessor to see if it has a node above it to point to this node
			// in the stack
			while (prevStack != null && prevStack.up == null) {
				// it doesnt so we keep going back
				prevStack = prevStack.left;
			}
			if (prevStack != null) {
				// this left is the prevstack but one level higher
				this.left = prevStack.up;
				// right is the prevstack's right
				this.right = prevStack.up.right;
				// update prevstack's right
				prevStack.up.right = this;
			}
		}

		public void buildStack() {
			int level = 0;
			SkipListNode<T> downLevel = this;
			while (level < this.height) {
				downLevel.up = new SkipListNode<T>(downLevel);
				downLevel = downLevel.up;
				level++;
			}
		}

		public SkipListNode<T> top() {
			SkipListNode<T> up = this;
			while (up.up != null) {
				up = up.up;
			}
			return up;
		}

		public SkipListNode<T> bottom() {
			SkipListNode<T> down = this;
			while (down.down != null) {
				down = down.down;
			}
			return down;
		}

		public void changeStackHeight(int newHeight) {
			SkipListNode<T> current;
			int level;
			//Two cases:
			//decreasing height
			if(this.height > newHeight) {
				//start at top
				current = this.top();
				level = this.height;
				//delete node and fix prev and next
				while(level >= 0) {
					if(level >= newHeight) {
						current.left.right = current.right;
						current.right.left = current.left;
						current.up = null;
						current = current.down;
						current.down = null;
					}
					//at height < newHeight, fix the height variable of each node
					this.height = newHeight;
					level--;
				}
			}
			//increasing height
			else {
				//start at bottom
				current = this.bottom();
				level = 0;
				int oldHeight = current.height;
				while(level < newHeight) {
					//change height variable
					current.height = newHeight;
					//add new nodes
					if(level >= oldHeight) {
						current.up = new SkipListNode<T>(current);
						current = current.up;
					}
					level++;
				}
			}
		}
	}
}