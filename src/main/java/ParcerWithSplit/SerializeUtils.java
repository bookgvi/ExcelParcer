package ParcerWithSplit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SerializeUtils {
    private final ArrayList<ArrayList<String>> _dataList;

    public SerializeUtils(ArrayList<ArrayList<String>> dataList) {
        _dataList = (ArrayList<ArrayList<String>>) dataList.clone();
    }

    public ArrayList<ArrayList<String>> splitToArraylistV1() {
        ArrayList<ArrayList<String>> newDataList = new ArrayList<>();
        IntStream.range(0, _dataList.size())
                .forEach(index -> {
                    ArrayList<String> insideList = _dataList.get(index);
                    AtomicReference<AtomicReferenceArray<String>> firstInsideEltArr = new AtomicReference<>();
                    AtomicReference<AtomicReferenceArray<String>> secondInsideEltArr = new AtomicReference<>();
                    AtomicReference<AtomicReferenceArray<String>> thirdInsideEltArr = new AtomicReference<>();
                    IntStream.range(0, insideList.size()).filter(i -> i % 3 == 0).forEach(i -> {
                        firstInsideEltArr.set(new AtomicReferenceArray<>(insideList.get(i).split("\\|")));
                        secondInsideEltArr.set(new AtomicReferenceArray<>(insideList.get(i + 1).split("\\|")));
                        thirdInsideEltArr.set(new AtomicReferenceArray<>(insideList.get(i + 2).split("\\|")));
                    });
                    int maxSize = Math.max(firstInsideEltArr.get().length(), secondInsideEltArr.get().length());
                    maxSize = Math.max(maxSize, thirdInsideEltArr.get().length());
                    IntStream.range(0, maxSize).forEach(index2 -> {
                        ArrayList<String> tmpList = new ArrayList<>();
                        if (firstInsideEltArr.get().length() > index2) tmpList.add(firstInsideEltArr.get().get(index2));
                        else tmpList.add(firstInsideEltArr.get().get(firstInsideEltArr.get().length() - 1));
                        if (secondInsideEltArr.get().length() > index2)
                            tmpList.add(secondInsideEltArr.get().get(index2));
                        else tmpList.add(secondInsideEltArr.get().get(secondInsideEltArr.get().length() - 1));
                        if (thirdInsideEltArr.get().length() > index2) tmpList.add(thirdInsideEltArr.get().get(index2));
                        else tmpList.add(thirdInsideEltArr.get().get(thirdInsideEltArr.get().length() - 1));
                        newDataList.add(tmpList);
                    });
                });
        return newDataList;
    }

    public ArrayList<ArrayList<String>> splitToArraylistV2() {
        final int defaultColumnsCount = 3;
        return splitToArraylistV2(defaultColumnsCount);
    }

    public ArrayList<ArrayList<String>> splitToArraylistV2(int columnsCount) {
        ArrayList<ArrayList<String>> newDataList = new ArrayList<>();
        IntStream.range(0, _dataList.size())
                .forEach(index -> {
                    ArrayList<String> nestedArrayList = _dataList.get(index);
                    List<String[]> splitArrayHolder = this.splitString(columnsCount, nestedArrayList, "\\|");
                    int maxSize = getMaxSizeOfArrays(splitArrayHolder);
                    joinArray(splitArrayHolder, maxSize, newDataList);
                });
        return newDataList;
    }

    private List<String[]> splitString(int columnsCount, ArrayList<String> arrayList, String delimeter) {
        return IntStream.range(0, columnsCount)
                .mapToObj(i -> arrayList.get(i).split(delimeter)).collect(Collectors.toList());
    }

    private int getMaxSizeOfArrays(List<String[]> streamWithArrays) {
        List<Integer> listWithArrays = streamWithArrays.stream().map(arr -> arr.length).sorted().collect(Collectors.toList());
        return listWithArrays.get(listWithArrays.size() - 1);
    }

    private ArrayList<ArrayList<String>> joinArray(List<String[]> splitArrayHolder, int maxSize, ArrayList<ArrayList<String>> res) {
        IntStream.range(0, maxSize).forEach(i -> {
            ArrayList<String> tmpArr = new ArrayList<>();
            splitArrayHolder.forEach(array -> {
                if (array.length > i) tmpArr.add(array[i]);
                else if (array.length == 1) tmpArr.add(array[0]);
                else tmpArr.add("-");
            });
            res.add(tmpArr);
        });
        return res;
    }
}
