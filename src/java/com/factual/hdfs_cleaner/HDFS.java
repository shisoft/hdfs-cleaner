package com.factual.hdfs_cleaner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HDFS {

  private static Configuration config;
  public static FileSystem dfs;

  public static void init() {
    config = new Configuration();
    try {
      dfs = FileSystem.get(config);
      assert dfs.listFiles(new Path("/"), false).hasNext();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<SimplifiedFileStatus> scan(Path path) throws IOException {
    List<SimplifiedFileStatus> statuses = new ArrayList<>();
    for (FileStatus file : dfs.listStatus(path)) {
      SimplifiedFileStatus status = new SimplifiedFileStatus();
      status.path = file.getPath();
      status.isDir = file.isDirectory();
      status.lastModified = file.getModificationTime();
      status.replication = file.getReplication();
      try {
        status.subFiles = file.isDirectory() ? scan(file.getPath()) : null;
        status.size =
            file.isDirectory() ?
                status.subFiles.stream().mapToLong(SimplifiedFileStatus::getSize).sum() :
                file.getLen();
      } catch (Exception e) {
        status.hasError = true;
      }
      statuses.add(status);
    }
    return statuses;
  }
}
