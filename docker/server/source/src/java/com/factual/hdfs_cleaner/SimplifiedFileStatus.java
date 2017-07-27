package com.factual.hdfs_cleaner;

import org.apache.hadoop.fs.Path;

import java.util.List;

public class SimplifiedFileStatus {
  Path path;
  long size;
  long lastModified;
  boolean isDir;
  int replication;
  List<SimplifiedFileStatus> subFiles;
  boolean hasError = false;

  public Path getPath() {
    return path;
  }

  public void setPath(Path path) {
    this.path = path;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public long getLastModified() {
    return lastModified;
  }

  public void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }

  public boolean isDir() {
    return isDir;
  }

  public void setDir(boolean dir) {
    isDir = dir;
  }

  public int getReplication() {
    return replication;
  }

  public void setReplication(int replication) {
    this.replication = replication;
  }

  public List<SimplifiedFileStatus> getSubFiles() {
    return subFiles;
  }

  public void setSubFiles(List<SimplifiedFileStatus> subFiles) {
    this.subFiles = subFiles;
  }

  public boolean isHasError() {
    return hasError;
  }

  public void setHasError(boolean hasError) {
    this.hasError = hasError;
  }
}
