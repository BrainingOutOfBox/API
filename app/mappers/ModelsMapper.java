package mappers;

import models.bo.Participant;
import models.dto.BrainstormingFindingDTO;
import models.bo.BrainstormingFinding;
import models.dto.ParticipantDTO;
import org.modelmapper.ModelMapper;

public class ModelsMapper {

    private ModelMapper modelMapper;

    public ModelsMapper() {
        modelMapper = new ModelMapper();
    }

    public BrainstormingFindingDTO toBrainstormingFindingDTO(BrainstormingFinding finding){
        return modelMapper.map(finding, BrainstormingFindingDTO.class);
    }

    public BrainstormingFinding toBrainstormingFinding(BrainstormingFindingDTO findingDTO){
        return modelMapper.map(findingDTO, BrainstormingFinding.class);
    }

    public ParticipantDTO toParticipantDTO(Participant participant){
        return modelMapper.map(participant, ParticipantDTO.class);
    }

    public Participant toParticipant(ParticipantDTO participantDTO){
        return modelMapper.map(participantDTO, Participant.class);
    }

}
