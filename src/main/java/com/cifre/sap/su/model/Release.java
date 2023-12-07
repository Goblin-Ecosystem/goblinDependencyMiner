package com.cifre.sap.su.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Release {
    @Id
    private String gav;
    private long timestamp;

    public Release(){}

    public Release(String gav) {
        this.gav = gav;
    }

    public Release(String gav, long timestamp) {
        this.gav = gav;
        this.timestamp = timestamp;
    }

    public String getId() {
        return gav;
    }

    public String getGav() {
        return gav;
    }

    public String getVersion() {
        return gav.split(":")[2];
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setGav(String gav) {
        this.gav = gav;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Release that = (Release) o;
        return gav.equals(that.gav);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gav);
    }

    @Override
    public String toString() {
        return "Release{" +
                "gav='" + gav + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
