package com.techone.keeper.model;

import java.sql.Timestamp;

public class SingleNote {

    private String id;
    private String title;
    private String body;
    private Timestamp createdOn;
    private String editedOn;
    private Boolean isPinned;
    private Boolean isArchived;
    private Boolean isInTrash;
    private String owner;
    private String colorPallete;
    private Boolean isLabeled;
    private String label;

    public Boolean getCustomColor() {
        return isCustomColor;
    }

    public void setCustomColor(Boolean customColor) {
        isCustomColor = customColor;
    }

    private Boolean isCustomColor;

    public SingleNote(){
        isPinned = false;
        isArchived = false;
        isInTrash = false;
        isLabeled = false;
        colorPallete = "#FFFFFF";
    }
    public SingleNote(String title,String body){
        this.title=title;
        this.body=body;
    }
    public SingleNote(String colorPallete)
    {
        this.colorPallete = colorPallete;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public String getEditedOn() {
        return editedOn;
    }

    public void setEditedOn(String editedOn) {
        this.editedOn = editedOn;
    }

    public Boolean getPinned() {
        return isPinned;
    }

    public void setPinned(Boolean pinned) {
        isPinned = pinned;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public Boolean getInTrash() {
        return isInTrash;
    }

    public void setInTrash(Boolean inTrash) {
        isInTrash = inTrash;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getColorPallete() {
        return colorPallete;
    }

    public void setColorPallete(String colorPallete) {
        this.colorPallete = colorPallete;
    }

    public Boolean getLabeled() {
        return isLabeled;
    }

    public void setLabeled(Boolean labeled) {
        isLabeled = labeled;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
