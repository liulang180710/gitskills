package org.my.springcloud.producer.utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class SortUtils {
    /**
    直接插入排序原理：1,4,3,2，时间复杂度O(n)-O(n^2)
    1,4->1,3,4->1,2,3,4
    思路：1.从数组的第二个数开始，比较当前这个数array[i]和前面一个数array[i-1]的数值大小，如果当前的数值小于前面的数，就执行第二步
    2.将当前每次比较的数先存储起来，作为比较的对象（哨兵）index=array[i]。
    3.将前一个数设置为当前的数值（哨兵）array[i-1]=index(array[i])，也就是将比较的值向数组末端移动一位，然后判断当前数前面的前面的数的值array[i-2]与当前值array[i]的大小，
    如果index小于array[i-2]就将array[i-1]=array[i-2]即，将比较的值向数组末端移动一位，腾出位置。
    4.如果index的值大于array[i-2]的值，就直接跳出，然后将指针前面的数值设置为index，然后返回array即可
     */
	public static int[] insertSort(int[] array){
		//首先判断数组的长度是否等于1？
		if(array.length<=1){		//如果长度小于1或者等于1，就直接返回
			return array;
		}else{						//否则就采用直接插入排序
			for(int i = 1; i < array.length; i++){
				int index;
				if(array[i-1] > array[i]){
					index = array[i];
					array[i] = array[i-1];
					int j;
					for(j=i-2;j>=0;j--){
						if(index<array[j]){
							array[j+1]=array[j];
						}else{
							break;
						}
					}
					array[j+1]=index;
				}
			}
			return array;
		}
	}

    //折半插入排序，时间复杂度O(n^2)
    //折半插入排序的原理就是：先从数组的第二个数i开始，通过在0到i-1个数之间进行折半查找，找到当前的数需要插入的位置，然后再将要插入位置开始
    //到当前这个数前面的数进行移动，然后将当前的值插入。
	public static int[] bInsertSort(int[] array){
		if(array.length<=1){
			return array;
		}else{
			for(int i=1;i < array.length;i++){
				int index = array[i];
				int low=0,high=i-1;
				while(low<=high){
					int m=(low+high)/2;
					if(index<array[m]){
						high=m-1;
					}else{
						low=m+1;
					}
				}//找到之后就移动，移动的范围就是m到i
				int j=0;
				for(j=i-1;j>=high+1;--j){
					array[j+1]=array[j];
				}
				array[high+1]=index;
			}
			return array;
		}
	}

    //希尔排序
    //希尔排序的时间复杂度比直接插入要低，大概在n^1.3-1.5左右
    public static int[] shellSort(int[] array,int[] dlta){
        for(int k=0;k<dlta.length;k++){
            shellInsert(array,dlta[k]);
        }
        return array;
    }

    public static int[] shellInsert(int[] array, int dk) {
        for(int i=dk;i<array.length;i++){
            if(array[i]<array[i-dk]){
                int index=array[i];
                int j=0;
                for(j=i-dk;j>=0;j-=dk){
                    if(array[j]>index){
                        array[j+dk]=array[j];
                    }else{
                        break;
                    }
                }
                array[j+dk]=index;
            }
        }
        return array;
    }

    //不需要先比较当前数值i和前面的一个数值i-1之后再让i与i-2进行比较，直接让i与i-1后面的数进行比较然后移动
    public static int[] insetSort2(int[] array){
        if(array.length<=1){
            return array;
        }else{
            for(int i=1;i<array.length;i++){
                int index=0;
                if(array[i]<array[i-1]){
                    index=array[i];
                    int j=0;
                    for(j=i-1;j>=0;j--){
                        if(index<array[j]){
                            array[j+1]=array[j];
                        }else{
                            break;
                        }
                    }
                    array[j+1]=index;
                }

            }
            return array;
        }
    }
    /*********************************/
	//冒泡排序，时间复杂度O(n^2)
	public static int[] bubbleSort(int[] array){
		if(array.length<=1){
			return array;
		}
		//总共需要比较的次数为n-1，最后一次不用，每次比较的时候都需要array.length-i次
		for(int i=0;i<array.length-1;i++){
			for(int j=1;j<array.length-i;j++){
				if(array[j]<array[j-1]){
					int temp=array[j-1];
					array[j-1]=array[j];
					array[j]=temp;
				}
			}
		}

		return array;
	}

    //快速排序，时间复杂度为nlogn,当序列基本有序时，退化为冒泡排序。
    public static int partition(int[] array, int low, int high) {
        int index=array[low];//设置第一个枢轴记录
        while(low<high){
            //找到第一个比index小的值
            //将array[low]设置为第一个比它小的数
            while(array[high]>=index&&high>low){
                high--;
            }
            array[low]=array[high];

            //找到第一个比index大的值
            //将array[high]设置为第一个比它大的数
            while(array[low]<=index&&low<high){
                low++;
            }
            array[high]=array[low];

        }
        array[low] = index;
        return low;
    }
    public static int[] qSort(int[] array,int low ,int high){
        if(low<high){
            //找到第一轮排序之后的中间值
            int pivotloc = partition(array,low,high);
            //左边递归
            qSort(array,low,pivotloc-1);
            //右边递归
            qSort(array,pivotloc+1,high);

        }
        return array;
    }

    public static int[] quickSort(int array[]){
        if(array.length<=1){
            return array;
        }else{
            return qSort(array, 0, array.length-1);
        }
    }



    // 数组中的第K个最大元素-选择排序
    public int findKthLargest(int[] _nums, int k) {
        int n = _nums.length;
        return quickSelect(_nums, 0, n - 1, n - k);
    }

    int quickSelect(int[] nums, int left, int right, int k) {
        if (left == right) return nums[k];
        int x = nums[left], i = left - 1, j = right + 1;
        while (i < j) {
            do i++; while (nums[i] < x);
            do j--; while (nums[j] > x);
            if (i < j){
                int tmp = nums[i];
                nums[i] = nums[j];
                nums[j] = tmp;
            }
        }
        if (j >= k) {
            return quickSelect(nums, left, j, k);
        } else {
            return quickSelect(nums, j + 1, right, k);
        }
    }



    /******************************/
    //简单选择排序O(n^2)，每一次找到最小的，然后依次找后面的
    public static int[] selectSort(int[] array){
        if(array.length<=1){
            return array;
        }else{
            for(int i=0;i<array.length;i++){
                int min=array[i];
                for(int j=i+1;j<array.length;j++){
                    if(min>array[j]){
                        array[i]=array[j];
                        array[j]=min;
                        min=array[i];
                    }
                }
            }
            return array;
        }
    }


    //堆排序
    //堆排序采用的存储结构是有序表
    //例如：value:49,38,65,97,76,13,27,49
//		  index:1  2  3  4  5  6  7  8
/*	     49
	    / \
 	   38  65
 	  /\   /\
     97 76 13 27
    /
   49

   *完全二叉树的最后的非叶子节点是97(n/2)。也就是说要首先调整97以前的数(非叶子节点)。
   *97在数组中的编号是3,需要找到97的孩子节点中较小的一个进行位置交换，此时就与4*2=8就是49。
   *由于97没有右孩子节点，所以直接与49位置交换。
   *再看65,65的孩子节点为13,27。由于13,27都没有孩子节点，所以直接和13进行位置交换。
   *再看38,38的孩子节点为49,76。因为38比97和76都要小，所以此时不变。
   *再看49,49的孩子节点为38,13。选择最小的13进行交换。交换之后，由于49的孩子节点此时为65和27，所以49与27再次进行交换，得到最后的结果。
   *     13
	    / \
 	   38  27
 	  /\   /\
     49 76 65 49
    /
   97
   *然后再将13输出之后，将其与97交换。交换之后，再次进行堆的调整。
   *
   *
   */
    //时间复杂度为nlogn
    public static int[] heapSort(int[] array){
        //如果需要升序排序，就需要构造大顶堆，即最大的元素在最上面，然后再将最小的值换上去，然后调整
        //首先找到最后一个非叶子节点即不大于[n/2]的那个节点。长度若为8，中间的那个值就是array[3];若为7，中间的那个就是array[3]
        int start=array.length/2-1;
        //首先从非叶子节点向arra[0]开始调整，构造一次大顶堆
        for(int i=start;i>=0;i--){
            heapAdjust(array,i,array.length-1);
        }
        //调整完了之后，从最后一个数（最小的）开始，与第一个（最大的值）交换，将最大的值放在最后，目的为了按照从小到大顺序输出
        for(int j=array.length-1;j>=0;j--){
            int temp=array[0];
            array[0]=array[j];
            array[j]=temp;
            //交换了之后继续向下调整，找出最大的值
            heapAdjust(array, 0, j-1);
        }
        return array;
    }

    public static void heapAdjust(int[] array, int i, int length) {
        // TODO Auto-generated method stub
        //目标就是找到最大的值
        int index=array[i];
        //找到i的所有后裔节点
        for(int j=i*2+1;j<=length;j=j*2+1){
            //找到i的直接孩子节点中较大的一个min
            if(j+1<=length&&array[j]<array[j+1]){
                ++j;
            }
            //如果index大于其孩子节点中最大的一个，就跳出循环，不改变
            if(index>=array[j]){
                break;
            }else{
                //否则就需要交换
                array[i]=array[j];
                i=j;
            }
        }
        array[i]=index;
    }


    //归并排序
    /*49 38 65 97 76 13 27
     * 2路归并算法。
     * 第一趟
     * [49 38] [65 97] [76 13] [27]
     * 排序之后
     * [38 49] [65 97] [13 76] [27]
     * 第二趟
     * [38 49 65 97] [13 76 27]
     * 第二趟之后
     * [38 49 65 97] [13 27 76]
     * 第三趟
     * [38 49 65 97 13 27 76]
     * 第三趟之后
     * [13 27 38 49 65 76 97]
     * */
    //时间复杂度为nlogn,稳定的排序方式。比较如果是在两个相邻的的数值之间进行比较的就是稳定的排序

    public static int[] mergeSort(int[] array){
        if(array.length<=1){
            return array;
        }else{
            int[] newArray=new int[array.length];
            mSort(array,newArray,0,array.length-1);
            return newArray;
        }
    }



    public static void mSort(int[] array, int[] newArray, int start, int end) {
        // TODO Auto-generated method stub
        //当将数组[49,38]继续分割为49的时候，start==end。此时有：
        if(start==end){
            newArray[newArray.length-1]=array[start];
        }else{	//否则继续分割
            int m=(start+end)/2;
            //构造左边的数组
            int[] left=new int[m-start+1];
            //对左边的数组进行递归调用继续进行分割
            mSort(array, left, start, m);
            //构造右边的数组
            int[] right=new int[end-m];
            //对右边的数组进行递归调用并继续进行分割
            mSort(array, right, m+1, end);
            //将左右两边的进行合并，并且排序
            merge(newArray,left,right);
        }

    }

    //实现两个数组的合并操作。
    private static void merge(int[] array, int[] left, int[] right) {
        // TODO Auto-generated method stub
        //实现将两个有序的顺序表合并成为一个有序的表array
        int i,j,k;
        //从一个数组A的第一个数开始，比较B的第一个数，如果比B大，就将其值装入合并数组C，并且继续比较B后面的值，
        for(i=0,j=0,k=0;i<left.length&&j<right.length;k++){
            if(left[i]>right[j]){
                array[k]=right[j++];
            }else{
                array[k]=left[i++];
            }
        }
        //如果此时数组B已经全部装入C，就将A剩下的装入C
        if(i<left.length){
            while(k<array.length){
                array[k++]=left[i++];
            }
        }//如果此时数组A已经全部装入C，就将B剩下的装入C
        if(j<right.length){
            while(k<array.length){
                array[k++]=right[j++];
            }
        }
    }

    /**
     *
     * @Title: main
     * @Description: RadixSort(基数排序)
     * @param @param args    设定文件
     * @return void    返回类型
     * @throws
     *
     *
     */

    public static int[] reSort(int[] array,int k){
        //构造一个按键升序的map
        Map<Integer,ArrayList<Integer>> map=new TreeMap<>();
        for(int i=0;i<array.length;i++){
            //首先获取每一个数值
            String number=String.valueOf(array[i]);
            //获取每个值得位数
            int t=number.length();
            //如果当前数值得位数小于k,说明该数第k位（向前数个，十……）为0，例如：8的位数小于（k=2）,所以第百位就为0
            if(t<k){
                //key就等于0
                if(map.containsKey(0)){
                    map.get(0).add(array[i]);
                }else{
                    ArrayList<Integer> list=new ArrayList<>();
                    list.add(array[i]);
                    map.put(0,list);
                }
            }else{	//如果当前数值的位数不小于k,说明当前数值小于10^k，但是第k位可以通过取余得到,例如：63当k=2时，直接通过除以10的k-1次方取商获得需要存储的键为6
                int first=array[i]/(10^(k-1));
                if(map.containsKey(first)){
                    map.get(first).add(array[i]);
                }else{
                    ArrayList<Integer> list=new ArrayList<>();
                    list.add(array[i]);
                    map.put(first, list);

                }
            }
        }
        int i=0;
        //实现对map的输出，注意输出值得时候需要获取一趟值得排序（此处可以通过其他的方式获得）
        for(Map.Entry<Integer, ArrayList<Integer>> entry:map.entrySet()){
            Collections.sort(entry.getValue());
            for(Integer n:entry.getValue()){
                array[i++]=n;
            }
        }
        return array;

    }

    //基数排序的改编版
    //首先我们找出所有值中最大的值，获取整个排序需要排序的次数
    public static int[] radixSort(int[] array){
        if(array.length<=1){
            return array;
        }else{
            int max=array[0];
            for(int i=1;i<array.length;i++){
                if(array[i]>max){
                    max=array[i];
                }
            }
            String number=String.valueOf(max);
            //获取需要排序的次数，将该次数作为每一个数值需要提取的键的位置，例如当遍历次数为1的时候，说明次轮排序是比较每个数值的最后一位。
            int n=number.length();
            for(int j=1;j<=n;j++){
                reSort(array,j);
            }
            return array;
        }
    }



    /*public static void main(String args[]){
        //int[] a=new Main().ShellSort(new int[]{49,38,65,97,76,13,27,49,55,4},new int[]{5,3,1});
        //int[] a=new Main().BubbleSort(new int[]{49,38,65,97,76,13,27,49,55,4});
        int[] a = heapSort(new int[]{49,38,65,97,76,13,27,49,55,4});
        //int[] a=new Main().RadixSort(new int[]{278,109,63,930,589,184,505,269,8,83});
        for(int x:a){
            System.out.println(x);
        }

    }*/


}
