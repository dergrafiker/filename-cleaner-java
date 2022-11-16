package de.dergrafiker.filenamecleaner;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileVisitor extends SimpleFileVisitor<Path> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileVisitor.class);
  private final FilenameChecker filenameChecker;
  private final FilenameCleaner filenameCleaner;

  private int fileCounter = 0;
  private int dirCounter = 0;

  private String dirPath = null;

  public FileVisitor(FilenameChecker filenameChecker,
      FilenameCleaner filenameCleaner) {
    this.filenameChecker = filenameChecker;
    this.filenameCleaner = filenameCleaner;
  }

  @Override
  public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
    if (exc != null) {
      throw exc;
    }

    fixName(dir);
    dirCounter++;
    return CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    fixName(file);
    fileCounter++;
    return CONTINUE;
  }

  private void fixName(Path path) {
    Path absolutePath = path.getParent().toAbsolutePath();

    if (!absolutePath.toString().equals(dirPath)) {
      dirPath = absolutePath.toString();
      System.out.println(dirPath);
      System.out.println();
    }

    String oldName = path.getFileName().toString();
    String normalized = Normalizer.normalize(oldName, Normalizer.Form.NFC);

    if (!oldName.equals(normalized)) {
      System.out.println("normalized " + oldName + " => " + normalized);
    }

    boolean isDirectory = Files.isDirectory(path);

    String cleaned = filenameCleaner.clean(normalized, isDirectory);
    if (!oldName.equals(cleaned)) {
      System.out.println(oldName + " => " + cleaned);
    }

    if (filenameChecker.isInvalid(cleaned, isDirectory)) {
      String invalid = MatcherUtil.getMatcher("[-_.A-Za-z0-9]+", cleaned).replaceAll("");

      String message = String.format("Name is still invalid after clean '%s' => '%s' [%s] %s",
          oldName,
          cleaned,
          absolutePath,
          invalid);

      System.err.println(message);
      //throw new IllegalArgumentException(message);
    }
  }

  public int getFileCounter() {
    return fileCounter;
  }

  public int getDirCounter() {
    return dirCounter;
  }
}
