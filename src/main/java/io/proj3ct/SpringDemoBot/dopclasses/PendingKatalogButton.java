package io.proj3ct.SpringDemoBot.dopclasses;

public class PendingKatalogButton {


    private String name;
    private boolean awaitingDescription = true;

    public PendingKatalogButton(String label) {
        this.name = label;
    }

    public String getLabel() { return name; }
    public boolean isAwaitingDescription() { return awaitingDescription; }
    public void setAwaitingDescription(boolean awaitingDescription) {
        this.awaitingDescription = awaitingDescription;
    }

}
