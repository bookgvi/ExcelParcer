package ParcerWithSplit;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.IntStream;

public class SerializeUtils {
    ArrayList<ArrayList<String>> _dataList;

    public SerializeUtils(ArrayList<ArrayList<String>> dataList) {
        _dataList = (ArrayList<ArrayList<String>>) dataList.clone();
    }

    public ArrayList<ArrayList<String>> flatArraylist() {
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
                        if (secondInsideEltArr.get().length() > index2) tmpList.add(secondInsideEltArr.get().get(index2));
                        else tmpList.add(secondInsideEltArr.get().get(secondInsideEltArr.get().length() - 1));
                        if (thirdInsideEltArr.get().length() > index2) tmpList.add(thirdInsideEltArr.get().get(index2));
                        else tmpList.add(thirdInsideEltArr.get().get(thirdInsideEltArr.get().length() - 1));
                        newDataList.add(tmpList);
                    });
                });
        return newDataList;
    }
}
