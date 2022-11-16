package de.dergrafiker.filenamecleaner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    CaseSensitivityChecker.class,
    FilenameChecker.class,
    FilenameCleaner.class,
    FileVisitor.class,
    MatcherUtil.class,
    RenameUtil.class
})
public class Main implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  private final FileVisitor printFiles;

  public Main(FileVisitor printFiles) {
    this.printFiles = printFiles;
  }

  public static void main(String[] args) {
    LOG.info("STARTING THE APPLICATION");
    SpringApplication.run(Main.class, args);
    LOG.info("APPLICATION FINISHED");
  }

  @Override
  public void run(String... args) {
    try {
      //footest
      if (args.length != 1) {
        throw new IllegalArgumentException("Please provide a root path");
      }
      final Path rootPath = Paths.get(args[0]);
      Files.walkFileTree(rootPath, printFiles);

      System.out.println("files " + printFiles.getFileCounter());
      System.out.println("dirs " + printFiles.getDirCounter());

    } catch (IOException e) {
      LOG.error("", e);
    }
  }
}
