package models.dto;

import java.util.ArrayList;

public final class BrainsheetDTO {
    private int nrOfSheet;
    private ArrayList<BrainwaveDTO> brainwaves = new ArrayList<>();

    public BrainsheetDTO() {

    }

    public BrainsheetDTO(int nrOfSheet, ArrayList<BrainwaveDTO> brainwavesDTO) {
        this.nrOfSheet = nrOfSheet;
        this.brainwaves = brainwavesDTO;
    }

    public int getNrOfSheet() {
        return nrOfSheet;
    }

    public void setNrOfSheet(int nrOfSheet) {
        this.nrOfSheet = nrOfSheet;
    }

    public ArrayList<BrainwaveDTO> getBrainwaves() {
        return brainwaves;
    }

    public void setBrainwaves(ArrayList<BrainwaveDTO> brainwavesDTO) {
        this.brainwaves = brainwavesDTO;
    }

    public void addBrainwave(BrainwaveDTO brainwaveDTO){
        this.brainwaves.add(brainwaveDTO);
    }
}
