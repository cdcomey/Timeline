import java.io.Serializable;

public class DLList<E> implements Serializable{
	private Node<E> head, tail;
	private int size;

	public DLList(){
		head = new Node<E>(null);
		tail = new Node<E>(null);
		size = 0;

		head.setPrev(null);
		head.setNext(tail);
		tail.setPrev(head);
		tail.setNext(null);
	}

	public Node<E> getFirstElement(){ return head.next(); }

	public void add(E n){
		Node<E> newNode = new Node<E>(n);
		newNode.setPrev(tail.prev());
		newNode.setNext(tail);
		tail.prev().setNext(newNode);
		tail.setPrev(newNode);

		size++;
	}

	/* public void sortedAdd(E n){
		Node<E> newNode = new Node<E>(n);
		Node<E> current = head.next();
		while (current.next() != null){
			if (newNode.get().compareTo(current.get()) < 0){
				newNode.setPrev(current);
				newNode.setNext(current.next());
				current.next().setPrev(newNode);
				current.setNext(newNode);
				size++;
				break;
			}
		}
	} */

	public void add(int location, E n){
		Node<E> newNode = new Node<E>(n);
		Node<E> current = head;
		
		if (location > size)
			return;
		else if (location <= size/2){
			current = head;
			for (int i = 0; i < location - 1; i++)
				current = current.next();
		} else{
			current = tail.prev();
			for (int i = size; i > location; i--)
				current = current.prev();
		}

		newNode.setPrev(current);
		newNode.setNext(current.next());
		current.next().setPrev(newNode);
		current.setNext(newNode);

		size++;
	}

	public boolean contains(E n){
		Node<E> current = head.next();
		while (current.get() != null){
			if (current.get().equals(n))
				return true;
			current = current.next();
		}

		return false;
	}

	public void remove(E n){
		Node<E> current = head.next();
		while (current.get() != null){
		  if (current.get().equals(n)){
			current.prev().setNext(current.next());
			current.next().setPrev(current.prev());
			size--;
			return;
		  }
		  
		  current = current.next();
		}
  }

	public void remove(int n){
		if (n >= size)
			return;
		if (n <= size/2){
			Node<E> current = head.next();
 
		for (int i = 0; i < n; i++)
			current = current.next();

		current.prev().setNext(current.next());
		current.next().setPrev(current.prev());
		size--;
 
		} else {
			Node<E> current = tail.prev();
			for (int i = size-1; i > n; i--)
				current = current.prev();
			current.prev().setNext(current.next());
			current.next().setPrev(current.prev());
			size--;
		}
	}
  
	public void clear(){
		head.setNext(tail);
		tail.setPrev(head);
	}

	public Node<E> getNode(int n){
		Node<E> current = head;
		for (int i = 0; i < n-1; i++){
		  current = current.next();
		  if (current.next() == null)
			return null;
		}

		return current.next();
	}
  
	public E get(int n){
		if (n >= size)
			return null;
		else if (n <= size/2){
			Node<E> current = head.next();
			for (int i = 0; i < n; i++)
				current = current.next();
		   return current.get();
		 
		} else{
			Node<E> current = tail.prev();
			for (int i = size; i > n+1; i--)
				current = current.prev();
		   return current.get();
		}
	}

	public int size(){ return size; }

	public int indexOf(E e){
		Node<E> current = head;
		for (int i = 0; current != null; i++){
			if (current.get() != null && current.get().equals(e))
				return i;
			current = current.next();
		}

		return -1;
	}

	public void set(int n, E other){
		if (n >= size)
			return;
		if (n <= size/2){
			Node<E> current = head.next();
			for (int i = 0; current != null; i++){
				if (i == n){
					current.set(other);
					return;
				}
			current = current.next();
			}
		} else{
			Node<E> current = tail.prev();
			for (int i = size - 1; current != null; i--){
				if (i == n){
					current.set(other);
					return;
				}
			current = current.prev();
			}
		}
	}
	
	@Override
	public String toString(){
		String s = "[";
		Node<E> it = head.next();
		while (it.get() != null){
			s += it.get() + ", ";
			it = it.next();
		}
	
		return s.substring(0, s.length()-2) + "]";
	}
  
	public void reverse(){
		Node<E> current1 = head.next();
		Node<E> current2 = tail.prev();
		for (int i = 0; i < size/2; i++){
			E temp = current1.get();
			current1.set(current2.get());
			current2.set(temp);
			
			current1 = current1.next();
			current2 = current2.prev();
		}
	}
	
	public Object[] toArray(){
		Object[] arr = new Object[size];
		Node<E> current = head.next();
		for (int i = 0; i < size; i++){
			arr[i] = current.get();
			current = current.next();
		}
		
		return arr;
	}
	
	/* public void bubbleSort(){
		Node<E> current1 = head.next();
		Node<E> current2 = current1.next();
		for (int i = 0; i < size - 1; i++){
			for (int j = i+1; j < size; j++){
				if (current1.get().compareTo(current2.get()) < 0){
					
				}
			}
		}
	} */
}