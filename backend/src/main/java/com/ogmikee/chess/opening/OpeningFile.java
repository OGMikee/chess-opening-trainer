package com.ogmikee.chess.opening;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OpeningFile {
    private String version;
    private String name;
    private OpeningTree tree;

    public OpeningFile(){
    }
    @JsonCreator
    public OpeningFile(@JsonProperty("version") String version, @JsonProperty("name") String name, @JsonProperty("tree") OpeningTree tree){
        this.version = version;
        this.name = name;
        this.tree = tree;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OpeningTree getTree() {
        return tree;
    }

    public void setTree(OpeningTree tree) {
        this.tree = tree;
    }
}