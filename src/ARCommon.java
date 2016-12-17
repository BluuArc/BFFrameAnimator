/**
 * Common functions between array stacks and queues in Java
 * 
 * Date: 11/29/2016
 * 
 *	This program is licensed under the Creative Commons Attribution 3.0 United States License.
 *	Visit https://github.com/BluuArc/BFFrameAnimator for updates.
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
	
	public int getNumElements(){
		return numElements;
	}

}