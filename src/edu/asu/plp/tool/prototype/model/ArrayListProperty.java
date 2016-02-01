package edu.asu.plp.tool.prototype.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ArrayListProperty<E> implements ObservableList<E>
{
	private ObservableList<E> backingList;
	
	public ArrayListProperty()
	{
		this.backingList = FXCollections.observableArrayList();
	}
	
	@Override
	public boolean add(E arg0)
	{
		return backingList.add(arg0);
	}
	
	@Override
	public void add(int arg0, E arg1)
	{
		backingList.add(arg0, arg1);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> arg0)
	{
		return backingList.addAll(arg0);
	}
	
	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1)
	{
		return backingList.addAll(arg0, arg1);
	}
	
	@Override
	public void clear()
	{
		backingList.clear();
	}
	
	@Override
	public boolean contains(Object arg0)
	{
		return backingList.contains(arg0);
	}
	
	@Override
	public boolean containsAll(Collection<?> arg0)
	{
		return backingList.containsAll(arg0);
	}
	
	@Override
	public E get(int arg0)
	{
		return backingList.get(arg0);
	}
	
	@Override
	public int indexOf(Object arg0)
	{
		return backingList.indexOf(arg0);
	}
	
	@Override
	public boolean isEmpty()
	{
		return backingList.isEmpty();
	}
	
	@Override
	public Iterator<E> iterator()
	{
		return backingList.iterator();
	}
	
	@Override
	public int lastIndexOf(Object arg0)
	{
		return backingList.lastIndexOf(arg0);
	}
	
	@Override
	public ListIterator<E> listIterator()
	{
		return backingList.listIterator();
	}
	
	@Override
	public ListIterator<E> listIterator(int arg0)
	{
		return backingList.listIterator(arg0);
	}
	
	@Override
	public boolean remove(Object arg0)
	{
		return backingList.remove(arg0);
	}
	
	@Override
	public E remove(int arg0)
	{
		return backingList.remove(arg0);
	}
	
	@Override
	public boolean removeAll(Collection<?> arg0)
	{
		return backingList.removeAll(arg0);
	}
	
	@Override
	public boolean retainAll(Collection<?> arg0)
	{
		return backingList.retainAll(arg0);
	}
	
	@Override
	public E set(int arg0, E arg1)
	{
		return backingList.set(arg0, arg1);
	}
	
	@Override
	public int size()
	{
		return backingList.size();
	}
	
	@Override
	public List<E> subList(int arg0, int arg1)
	{
		return backingList.subList(arg0, arg1);
	}
	
	@Override
	public Object[] toArray()
	{
		// TODO Auto-generated method stub return null;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public <T> T[] toArray(T[] arg0)
	{
		// TODO Auto-generated method stub return null;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void addListener(InvalidationListener arg0)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void removeListener(InvalidationListener arg0)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean addAll(E... arg0)
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void addListener(ListChangeListener<? super E> arg0)
	{
		backingList.addListener(arg0);
	}
	
	@Override
	public void remove(int arg0, int arg1)
	{
		backingList.remove(arg0, arg1);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean removeAll(E... arg0)
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void removeListener(ListChangeListener<? super E> arg0)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean retainAll(E... arg0)
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean setAll(E... arg0)
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public boolean setAll(Collection<? extends E> arg0)
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
}
