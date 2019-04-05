package models.dto;

import java.util.ArrayList;

public final class BrainwaveDTO {
    private int nrOfBrainwave;
    ArrayList<IdeaDTO> ideas = new ArrayList<>();

    public BrainwaveDTO() {

    }

    public BrainwaveDTO(int nrOfBrainwave, ArrayList<IdeaDTO> ideas) {
        this.nrOfBrainwave = nrOfBrainwave;
        this.ideas = ideas;
    }

    public int getNrOfBrainwave() {
        return nrOfBrainwave;
    }

    public void setNrOfBrainwave(int nrOfBrainwave) {
        this.nrOfBrainwave = nrOfBrainwave;
    }

    public ArrayList<IdeaDTO> getIdeas() {
        return ideas;
    }

    public void setIdeas(ArrayList<IdeaDTO> ideasDTO) {
        this.ideas = ideasDTO;
    }

    public void addIdea(IdeaDTO ideaDTO){
        this.ideas.add(ideaDTO);
    }
}
