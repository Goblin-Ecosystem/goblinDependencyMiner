package com.cifre.sap.su.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Link {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String source;
    private String targetArtifact;
    private String targetVersion;
    private String scope;

    public Link(String source, String targetArtifact, String targetVersion, String scope) {
        this.source = source;
        this.targetArtifact = targetArtifact;
        this.targetVersion = targetVersion;
        this.scope = scope;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTargetArtifact() {
        return targetArtifact;
    }

    public void setTargetArtifact(String targetArtifact) {
        this.targetArtifact = targetArtifact;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link dep = (Link) o;
        return source.equals(dep.source) && targetArtifact.equals(dep.targetArtifact) && targetVersion.equals(dep.targetVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, targetArtifact, targetVersion);
    }

    @Override
    public String toString() {
        return "Link{" +
                "source='" + source + '\'' +
                ", targetArtifact='" + targetArtifact + '\'' +
                ", targetVersion='" + targetVersion + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
