package mappers;

import models.bo.BrainstormingTeam;
import models.bo.Participant;
import models.dto.BrainstormingFindingDTO;
import models.bo.BrainstormingFinding;
import models.dto.BrainstormingTeamDTO;
import models.dto.ParticipantDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class ModelsMapper {

    private ModelMapper modelMapper;

    public ModelsMapper() {
        this.modelMapper = new ModelMapper();
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

    public BrainstormingTeamDTO tobrainstormingTeamDTO(BrainstormingTeam brainstormingTeam){
        return modelMapper.map(brainstormingTeam, BrainstormingTeamDTO.class);
    }

    public BrainstormingTeam toBrainstormingTeam(BrainstormingTeamDTO brainstormingTeamDTO){
        return modelMapper.map(brainstormingTeamDTO, BrainstormingTeam.class);
    }

}
