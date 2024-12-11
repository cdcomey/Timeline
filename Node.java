import java.io.Serializable;

public class Node<E> implements Serializable{
	
	private E data;
	private Node<E> prev;
	private Node<E> next;
	
	public Node(E data){
		this.data = data;
		prev = null;
		next = null;
	}
	
	public E get(){ return data; }
	public Node<E> prev(){ return prev; }
	public Node<E> next(){ return next; }
	public void set(E data){ this.data = data; }
	public void setPrev(Node<E> prev){ this.prev = prev; }
	public void setNext(Node<E> next){ this.next = next; }
}