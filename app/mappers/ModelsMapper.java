package mappers;

import models.bo.*;
import models.dto.*;
import org.modelmapper.ModelMapper;

public class ModelsMapper {

    private ModelMapper modelMapper;

    public ModelsMapper() {
        this.modelMapper = new ModelMapper();

        /* FROM BO TO DTO */
        modelMapper.createTypeMap(Idea.class, IdeaDTO.class)
                .include(NoteIdea.class, IdeaDTO.class)
                .include(SketchIdea.class, IdeaDTO.class)
                .include(PatternIdea.class, PatternIdeaDTO.class);

        modelMapper.typeMap(NoteIdea.class, IdeaDTO.class).setProvider(request -> {
            NoteIdea idea = (NoteIdea)request.getSource();
            return new NoteIdeaDTO(idea.getDescription());
        });

        modelMapper.typeMap(SketchIdea.class, IdeaDTO.class).setProvider(request -> {
            SketchIdea idea = (SketchIdea)request.getSource();
            return new SketchIdeaDTO(idea.getDescription(), idea.getPictureId());
        });

        modelMapper.typeMap(PatternIdea.class, IdeaDTO.class).setProvider(request -> {
            PatternIdea idea = (PatternIdea)request.getSource();
            return new PatternIdeaDTO(idea.getDescription(), idea.getProblem(), idea.getSolution(), idea.getUrl(), idea.getCategory(), idea.getPictureId());
        });

        /* FROM DTO TO BO */
        modelMapper.createTypeMap(IdeaDTO.class, Idea.class)
                .include(NoteIdeaDTO.class, Idea.class)
                .include(SketchIdeaDTO.class, Idea.class)
                .include(PatternIdeaDTO.class, Idea.class);

        modelMapper.typeMap(NoteIdeaDTO.class, Idea.class).setProvider(request -> {
            NoteIdeaDTO idea = (NoteIdeaDTO)request.getSource();
            return new NoteIdea(idea.getDescription());
        });

        modelMapper.typeMap(SketchIdeaDTO.class, Idea.class).setProvider(request -> {
            SketchIdeaDTO idea = (SketchIdeaDTO)request.getSource();
            return new SketchIdea(idea.getDescription(), idea.getPictureId());
        });

        modelMapper.typeMap(PatternIdeaDTO.class, Idea.class).setProvider(request -> {
            PatternIdeaDTO idea = (PatternIdeaDTO)request.getSource();
            return new PatternIdea(idea.getDescription(), idea.getProblem(), idea.getSolution(), idea.getUrl(), idea.getCategory(), idea.getPictureId());
        });
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

    public BrainsheetDTO toBrainsheetDTO(Brainsheet brainsheet){
        return modelMapper.map(brainsheet, BrainsheetDTO.class);
    }

    public Brainsheet toBrainsheet(BrainsheetDTO brainsheetDTO){
        return modelMapper.map(brainsheetDTO, Brainsheet.class);
    }

}
