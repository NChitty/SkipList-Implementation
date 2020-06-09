import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;


public class SkipListMain {

    public class SkipListSet<T> implements SortedSet<T> {
        private T[][] header;

        public SkipListSet() {
            super();
            
        }

		@Override
		public boolean add(T arg0) {
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			return 0;
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
}