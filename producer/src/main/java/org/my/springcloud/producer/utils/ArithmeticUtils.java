package org.my.springcloud.producer.utils;

import org.my.springcloud.base.bean.ListNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArithmeticUtils {

    /**
     * 链表逆转-迭代
     * @param head
     * @return
     */
    private ListNode reverseNodeList(ListNode head){
        ListNode prev = null;
        ListNode curr = head;
        while (curr != null) {
            //获取下一个节点
            ListNode next = curr.next;
            curr.next = prev; //翻转
            prev = curr; //前移动
            curr = next; //前移动
        }
        return prev;
    }

    /***
     * 链表反转-递归
     * @param head
     * @return
     */
    public ListNode reverseList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode newHead = reverseList(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }


    /***
     * 将数字逆转
     * @param x
     * @return
     */
    public int reverse(int x) {
        if (x>Integer.MAX_VALUE || x< Integer.MIN_VALUE || x==0) {
            return 0;
        }
        boolean flag =false;
        if (x<0) {
            x=-1*x;
            flag = true;
        }
        char[] s = Integer.toString(x).toCharArray();
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=s.length-1; i>=0;i--) {
            stringBuffer.append(s[i]);
        }
        try{
            return flag? Integer.parseInt(stringBuffer.toString()) : -1*Integer.parseInt(stringBuffer.toString());
        }catch (Exception e) {
            return 0;
        }
    }




    /**
     * 删除倒数第n个数，快慢指针
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        //创建头结点
        ListNode dummy = new ListNode(0, head);
        ListNode second = dummy;
        ListNode first = head;
        //快指针先找到正数第n个数
        for (int i = 0; i < n; ++i) {
            first = first.next;
        }
        //快慢指针依次向链表尾部移动
        while (first != null) {
            first = first.next;
            second = second.next;
        }
        //删除对应的值
        second.next = second.next.next;
        ListNode ans = dummy.next;
        return ans;
    }

    /**
     * 获取字符串数组的最长公共前缀-横向扫描
     */
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        String prefix = strs[0];
        int count = strs.length;
        for (int i = 1; i < count; i++) {
            prefix = longestCommonPrefix(prefix, strs[i]);
            if (prefix.length() == 0) {
                break;
            }
        }
        return prefix;
    }

    public String longestCommonPrefix(String str1, String str2) {
        int length = Math.min(str1.length(), str2.length());
        int index = 0;
        while (index < length && str1.charAt(index) == str2.charAt(index)) {
            index++;
        }
        return str1.substring(0, index);
    }

    /**
     *  获取字符串数组的最长公共前缀-纵向扫描
     */
    public String longestCommonPrefix2(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        int length = strs[0].length();
        int count = strs.length;
        for (int i = 0; i < length; i++) {
            char c = strs[0].charAt(i);
            for (int j = 1; j < count; j++) {
                if (i == strs[j].length() || strs[j].charAt(i) != c) {  //i == strs[j].length()说明存在字符串的长度小于第一个字符串
                    return strs[0].substring(0, i);
                }
            }
        }
        return strs[0];
    }
    /**
     * 合并K个顺序列表
     */
    public ListNode mergeKLists(ListNode[] lists) {
        ListNode ans = null;
        for (int i = 0; i < lists.length; ++i) {
            ans = mergeTwoLists(ans, lists[i]);
        }
        return ans;
    }

    public ListNode mergeTwoLists(ListNode a, ListNode b) {
        if (a == null || b == null) {
            return a != null ? a : b;
        }
        ListNode head = new ListNode(0);
        ListNode tail = head, aPtr = a, bPtr = b;
        while (aPtr != null && bPtr != null) {
            if (aPtr.val < bPtr.val) {
                tail.next = aPtr;
                aPtr = aPtr.next;
            } else {
                tail.next = bPtr;
                bPtr = bPtr.next;
            }
            tail = tail.next;
        }
        tail.next = (aPtr != null ? aPtr : bPtr);
        return head.next;
    }

    /**
     * 两两交换链表中的节点-递归
     * @param head
     * @return
     */
    public static ListNode swapPairs(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode newHead = head.next;
        head.next = swapPairs(newHead.next);
        newHead.next = head;
        return newHead;
    }

    /**
     * 两两交换链表中的节点-迭代
     * @param head
     * @return
     */
    public ListNode swapPairs2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode newHeadNode = new ListNode(-1);
        newHeadNode.next = head;
        ListNode currentNode = newHeadNode.next;
        ListNode preNode = newHeadNode;
        while(currentNode != null && currentNode.next !=null) {
            ListNode tempNode = currentNode.next.next; //记录第三个节点
            preNode.next = currentNode.next; //第一个节点等于第二个节点
            currentNode.next.next = currentNode;
            currentNode.next = tempNode;
            //向前移动
            preNode = currentNode;
            currentNode =tempNode;
        }
        return newHeadNode.next;
    }

    /**
     * 实现 strStr() 函数，找到haystack中needle字符串的位置
     * @param haystack
     * @param needle
     * @return
     */
    public int strStr(String haystack, String needle) {
        int n = haystack.length();
        int m = needle.length();
        for (int i = 0; i + m <= n; i++) {
            boolean flag = true;
            for (int j = 0; j < m; j++) {
                if (haystack.charAt(i + j) != needle.charAt(j)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 无重复字符的最长子串 KMP
     * @param s
     * @return
     */
    public int lengthOfLongestSubstring(String s) {
        // 哈希集合，记录每个字符是否出现过
        Set<Character> occ = new HashSet<>();
        int n = s.length();
        // 右指针，初始值为 -1，相当于我们在字符串的左边界的左侧，还没有开始移动
        int rk = -1, ans = 0;
        for (int i = 0; i < n; ++i) {
            if (i != 0) {
                // 左指针向右移动一格，移除一个字符
                occ.remove(s.charAt(i - 1));
            }
            while (rk + 1 < n && !occ.contains(s.charAt(rk + 1))) {
                // 不断地移动右指针
                occ.add(s.charAt(rk + 1));
                ++rk;
            }
            // 第 i 到 rk 个字符是一个极长的无重复字符子串
            ans = Math.max(ans, rk - i + 1);
        }
        return ans;
    }

    //回文数字
    public boolean isPalindrome(int x) {
        // 特殊情况：
        // 如上所述，当 x < 0 时，x 不是回文数。
        // 同样地，如果数字的最后一位是 0，为了使该数字为回文，
        // 则其第一位数字也应该是 0
        // 只有 0 满足这一属性
        if (x < 0 || (x % 10 == 0 && x != 0)) {
            return false;
        }

        int revertedNumber = 0;
        while (x > revertedNumber) {
            revertedNumber = revertedNumber * 10 + x % 10;
            x /= 10;
        }

        // 当数字长度为奇数时，我们可以通过 revertedNumber/10 去除处于中位的数字。
        // 例如，当输入为 12321 时，在 while 循环的末尾我们可以得到 x = 12，revertedNumber = 123，
        // 由于处于中位的数字不影响回文（它总是与自己相等），所以我们可以简单地将其去除。
        return x == revertedNumber || x == revertedNumber / 10;
    }

    //三数之和
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        int n = nums.length;
        Arrays.sort(nums);
        for (int i= 0 ; i < n-1 ; i++) {
            int first = nums[i];
            for (int j=i+1; j<n; j++) {
                int second = nums[j];
                for (int k = j+1; k<n ;k++) {
                    int third = nums[k];
                    if (first + second + third == 0) {
                        List<Integer> list = new ArrayList<>();

                        list.add(first);
                        list.add(second);
                        list.add(third);
                        if (!result.contains(list))
                            result.add(list);
                    }
                }
            }
        }
        return result;
    }

    public List<List<Integer>> threeSum2(int[] nums) {
        int n = nums.length;
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        // 枚举 a
        for (int first = 0; first < n; ++first) {
            // 需要和上一次枚举的数不相同
            if (first > 0 && nums[first] == nums[first - 1]) {
                continue;
            }
            // c 对应的指针初始指向数组的最右端
            int third = n - 1;
            int target = -nums[first];
            // 枚举 b
            for (int second = first + 1; second < n; ++second) {
                // 需要和上一次枚举的数不相同
                if (second > first + 1 && nums[second] == nums[second - 1]) {
                    continue;
                }
                // 需要保证 b 的指针在 c 的指针的左侧
                while (second < third && nums[second] + nums[third] > target) {
                    --third;
                }
                // 如果指针重合，随着 b 后续的增加
                // 就不会有满足 a+b+c=0 并且 b<c 的 c 了，可以退出循环
                if (second == third) {
                    break;
                }
                if (nums[second] + nums[third] == target) {
                    List<Integer> list = new ArrayList<Integer>();
                    list.add(nums[first]);
                    list.add(nums[second]);
                    list.add(nums[third]);
                    ans.add(list);
                }
            }
        }
        return ans;
    }

    //删除元素,快慢指针，右指针判断不等于val的值并且放入左指针对应的值中
    public int removeElement(int[] nums, int val) {
        int n = nums.length;
        int left = 0;
        for (int right = 0; right < n; right++) {
            if (nums[right] != val) {
                nums[left] = nums[right];
                left++;
            }
        }
        return left;
    }


    //跳跃游戏
    public boolean canJump(int[] nums) {
        int n = nums.length;
        int rightmost = 0;
        for (int i = 0; i < n; ++i) {
            if (i <= rightmost) {
                rightmost = Math.max(rightmost, i + nums[i]);
                if (rightmost >= n - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    //列出所有的罗马数字对应的阿拉伯数字
    private static final int[] ALL_NUM = {1000 , 900 , 500 , 400 , 100 , 90 , 50 , 40 , 10 , 9 , 5 , 4 , 1};
    //列出所有的罗马数字
    private static final String[] ALL_ROM_DATA = {"M" , "CM" , "D" , "CD" , "C" , "XC" , "L" , "XL" , "X" , "IX" , "V" , "IV" , "I"};



    private static String changeToRomData(int num) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (i < 13) {
            //如果当前数字大于比较的数字，则一直向后找
            while (num >= ALL_NUM[i]) {
                num = num - ALL_NUM[i];
                stringBuilder.append(ALL_ROM_DATA[i]);
            }
            i++;
        }
        return stringBuilder.toString();
    }

    //判断括号是否成对对应
    public static boolean isValid(String s) {
        int n = s.length();
        if (n % 2 == 1) {
            return false;
        }

        Map<Character, Character> pairs = new HashMap<Character, Character>() {{
            put(')', '(');
            put(']', '[');
            put('}', '{');
        }};
        Deque<Character> stack = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            char ch = s.charAt(i);
            if (pairs.containsKey(ch)) {
                //对于所有闭合的符号，出栈然后判断值与实际应该对应的符号是否相等，如果为空说明数量不对应也不符合
                if (stack.isEmpty() || stack.peek() != pairs.get(ch)) {
                    return false;
                }
                stack.pop();
            } else {
                //将所有非闭合的符号入栈
                stack.push(ch);
            }
        }
        return stack.isEmpty();
    }

    //搜索插入位置
    public int searchInsert(int[] nums, int target) {
        for(int i =0 ;i < nums.length ;i ++) {
            if (target <= nums[i]) {
                return i;
            }else {
                if (i < nums.length-1) {
                    if(target > nums[i] && target <= nums[i+1]) {
                        return i+1;
                    }
                }
            }
        }
        if (target > nums[nums.length-1]) {
            return nums.length;
        }else {
            return nums.length-1;
        }
    }

    //二分查找
    public int searchInsert2(int[] nums, int target) {
        int n = nums.length;
        int left = 0, right = n - 1, ans = n;
        while (left <= right) {
            int mid = ((right - left) >> 1) + left;
            if (target <= nums[mid]) {
                ans = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return ans;
    }

    public static int[] plusOne(int[] digits) {
        for(int i=digits.length-1 ; i>=0 ;i--) {
            int num = digits[i] +1;
            if (num == 10) {
                digits[i] = 0;
            }else {
                digits[i] = num;
                return digits;
            }
        }
        digits= new int[digits.length + 1];
        digits[0] = 1;
        return digits;
    }

    //最长回文子串
    public String longestPalindrome(String s) {
        int len = s.length();
        if (len < 2) {
            return s;
        }

        int maxLen = 1;
        int begin = 0;
        // dp[i][j] 表示 s[i..j] 是否是回文串
        boolean[][] dp = new boolean[len][len];
        // 初始化：所有长度为 1 的子串都是回文串
        for (int i = 0; i < len; i++) {
            dp[i][i] = true;
        }

        char[] charArray = s.toCharArray();
        // 递推开始
        // 先枚举子串长度
        for (int L = 2; L <= len; L++) {
            // 枚举左边界，左边界的上限设置可以宽松一些
            for (int i = 0; i < len; i++) {
                // 由 L 和 i 可以确定右边界，即 j - i + 1 = L 得
                int j = L + i - 1;
                // 如果右边界越界，就可以退出当前循环
                if (j >= len) {
                    break;
                }

                if (charArray[i] != charArray[j]) {
                    dp[i][j] = false;
                } else {
                    if (j - i < 3) {
                        dp[i][j] = true;
                    } else {
                        dp[i][j] = dp[i + 1][j - 1];
                    }
                }

                // 只要 dp[i][L] == true 成立，就表示子串 s[i..L] 是回文，此时记录回文长度和起始位置
                if (dp[i][j] && j - i + 1 > maxLen) {
                    maxLen = j - i + 1;
                    begin = i;
                }
            }
        }
        return s.substring(begin, begin + maxLen);
    }
}
