package cfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static List<List<Integer>> subsets(List<Integer> nums) {
        List<List<Integer>> subsets = new ArrayList<>();
        List<Integer> subset = new ArrayList<>();

        dfs(nums, 0, subset, subsets);

        return subsets;
    }

    private static void dfs(List<Integer> nums, int index, List<Integer> subset, List<List<Integer>> subsets) {
        if (index == nums.size()) {
            subsets.add(new ArrayList<>(subset));
            return;
        }
        subset.add(nums.get(index));
        dfs(nums, index + 1, subset, subsets);
        subset.remove(subset.size() - 1);
        dfs(nums, index + 1, subset, subsets);
    }


    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(1, 2, 3);
        List<List<Integer>> subsets = subsets(nums);
        for (List<Integer> subset : subsets) {
            System.out.println(subset);
        }
    }
}
