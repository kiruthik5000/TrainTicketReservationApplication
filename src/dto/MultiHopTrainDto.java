package dto;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiHopTrainDto {
    private final List<SimpleTrainDto> trainLists;

    public MultiHopTrainDto() {
        trainLists = new ArrayList<>();
    }

    public void addTrainDto(SimpleTrainDto dto) {
        trainLists.add(dto);
    }
    public void reverseTrains() {
        Collections.reverse(trainLists);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (trainLists.isEmpty()) return null;
        for (int i=0; i<trainLists.size(); i++) {
            sb.append(trainLists.get(i));
            if (i < trainLists.size() - 1) {
                sb.append("\n+\n");
            }
        }
        return sb.toString();
    }
}
