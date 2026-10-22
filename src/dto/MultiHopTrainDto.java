package dto;


import model.Train;

import java.util.ArrayList;
import java.util.List;

public class MultiHopTrainDto {
    private List<SimpleTrainDto> trainLists;

    public MultiHopTrainDto() {
        trainLists = new ArrayList<>();
    }

    public void addTrainDto(SimpleTrainDto dto) {
        trainLists.add(dto);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<trainLists.size(); i++) {
            sb.append(trainLists.get(i));
            if (i < trainLists.size() - 1) {
                sb.append("\n+\n");
            }
        }
        return sb.toString();
    }
}
