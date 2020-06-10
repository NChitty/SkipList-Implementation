import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public class SkipListMain {

	public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

		private SkipListNode<T> head;
		private int size = 0;

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
				if(current.right.element != null) {
					//if this level's element is less than the element we are inserting, move right
					if(current.right.element.compareTo(arg0) < 0) {
						current = current.right;
						continue; //we dont want to move down after moving right
					}
				}
				//the next element to the right is either null or greater than the insertion so we move down
				current = current.down;
			}
			SkipListNode<T> newNode = new SkipListNode<T>();
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends T> arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean contains(Object arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Iterator<T> iterator() {
			// TODO Auto-generated method stub
			return null;
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T[] toArray(T[] arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator<? super T> comparator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public T first() {
			// TODO Auto-generated method stub
			return null;
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