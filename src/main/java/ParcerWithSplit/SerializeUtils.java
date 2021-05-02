package ParcerWithSplit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public ArrayList<ArrayList<String>> splitToArraylistV2(String delimiter) {
        ArrayList<ArrayList<String>> newDataList = new ArrayList<>();
        _dataList.forEach(nestedArrayList -> {
            Supplier<Stream<String[]>> splitArrayHolderStreamSupplier = splitString(nestedArrayList, delimiter);
            int maxSize = getMaxSizeOfArrays(splitArrayHolderStreamSupplier.get());
            joinToArray(splitArrayHolderStreamSupplier, maxSize, newDataList);
        });
        return newDataList;
    }

    private Supplier<Stream<String[]>> splitString(ArrayList<String> arrayList, String delimiter) {
        return () -> arrayList.stream().map(elt -> elt.split(delimiter));
    }

    private int getMaxSizeOfArrays(Stream<String[]> streamWithArrays) {
        return streamWithArrays.mapToInt(arr -> arr.length).reduce(0, Math::max);
    }

    private ArrayList<ArrayList<String>> joinToArray(Supplier<Stream<String[]>> streamWithArraysSupplier, int maxSize, ArrayList<ArrayList<String>> res) {
        IntStream.range(0, maxSize).forEach(i -> {
            Stream<String[]> streamWithArrays = streamWithArraysSupplier.get();
            res.add((ArrayList<String>) streamWithArrays.map(array -> {
                if (array.length > i) return array[i];
                else if (array.length == 1) return array[0];
                return "-";
            }).collect(Collectors.toList()));
        });
        return res;
    }
}
