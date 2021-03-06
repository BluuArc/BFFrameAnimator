/**
 * Array Queues in Java
 * 
 * Date: 11/29/2016
 * 
 *	This program is licensed under the Creative Commons Attribution 3.0 United States License.
 *	Visit https://github.com/BluuArc/BFFrameAnimator for updates.
 *
 * @Author - Joshua Castor (jcasto3)
 * 
 */


public class ARQueue extends ARCommon{
	//constructors
	public ARQueue(){
		super();
	}

	public ARQueue(int n){
		super(n);
	}

	private void growArray(){
		int newSize = capacity*2;
		Object[] newArr = new Object[newSize];

		int index = top;
		int i;
		for(i = 0; i < numElements; ++i){
			newArr[i] = arr[index++];

			if(index > capacity)	index = 0;
		}
		for(; i < newSize; ++i)	newArr[i] = null;

		arr = newArr;
		capacity = newSize;
		top = 0;
		bottom = numElements-1;
	}

	public void push(Object val){
		enqueue(val);
	}

	public Object pop(){
		return dequeue();
	}

	public void enqueue(Object val){
		if(numElements+1 >= capacity)	growArray();
		arr[++bottom] = val;
		if(top == -1)	top = 0;
		numElements++;
		if(bottom >= capacity) bottom = 0;
	}

	public Object dequeue(){
		if(!isEmpty()){
			Object out = top();
			top++;
			numElements--;
			if(top >= capacity)	top = 0;
			return out;
		}else{
			return null;
		}
	}

	public String toString(){
		return toString(", ");
	}

	public String toString(String separator){
		String out = "";
		if(!isEmpty()){
			int i;
			for(i = top; i != bottom; ++i){
				if(i >= capacity)	i = 0;
				out += arr[i] + separator;
			}
			out += arr[i];
		}
		return out;
	}

	public void printList(){
		System.out.println(toString());
	}
	
	public Object[] getList(){
		if(isEmpty())	return null;
		
		Object[] newList = new Object[numElements];
		
		int i = 0;
		for(int count = top; count < numElements; ++count){
			if(i >= capacity) i = 0;
			newList[count] = arr[i];
		}
		
		return newList;
	}
}