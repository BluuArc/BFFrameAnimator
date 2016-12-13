/**
 * Common functions between array stacks anf queues in Java
 * 
 * Date: 11/29/2016
 * 
 * @Author - Joshua Castor (jcasto3)
 * 
 */


public class ARCommon{
	protected Object[] arr;
	protected int top;			//also considered head/front
	protected int bottom;		//also considered tail/back
	protected int numElements;
	protected int capacity;

	//constructors
	public ARCommon(){
		this(2);
	}

	public ARCommon(int n){
		arr = new Object[n];
		for(int i = 0; i < n; ++i)	arr[i] = null;
		top = -1;
		bottom = -1;
		numElements = 0;
		capacity = n;
	}

	public Object top(){
		return arr[top];
	}

	public boolean isEmpty(){
		return (numElements == 0);
	}

}