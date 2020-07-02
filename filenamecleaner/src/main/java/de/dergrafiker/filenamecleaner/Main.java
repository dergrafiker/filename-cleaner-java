package de.dergrafiker.filenamecleaner;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Import({
        CaseSensitivityChecker.class,
        FilenameChecker.class,
        FilenameCleaner.class,
        FileVisitor.class,
        MatcherUtil.class,
        RemovedCharsUtil.class,
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
            if (args.length != 1) {
                throw new IllegalArgumentException("Please provide a root path");
            }

            List<Method> methods = Arrays.stream(ReflectionUtils.getAllDeclaredMethods(Character.class))
                    .filter(m -> Arrays.asList(boolean.class, Boolean.class).contains(m.getReturnType())
                            && m.getParameterCount() == 1
                            && Arrays.asList(char.class, Character.class).contains(m.getParameterTypes()[0]))
                    .collect(Collectors.toList());
            System.out.println();

            final Path rootPath = Paths.get(args[0]);
            Files.walkFileTree(rootPath, printFiles);

/*            for (Path key : FileVisitor.PATH_TO_REMOVED_CHARS.keySet()) {
                Collection<Character> characters = FileVisitor.PATH_TO_REMOVED_CHARS.get(key);

                List<String> results = new ArrayList<>();

                for (Character character : characters) {
                    String matchingResults = methods.stream()
                            .map(invokeMethodAndReturnNameWhenTrue(character))
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(",", "[", "]"));
                    results.add(String.format("[%s] => %s", character, matchingResults));
                }

                LOG.info("Path => {} {}", key, String.join("; ", results));
            }*/

            Set<Character> characterSet = new HashSet<>(FileVisitor.PATH_TO_REMOVED_CHARS.values())
/*
                    .stream()
//                    .filter(Character::isLetterOrDigit)
                    .collect(Collectors.toSet())
*/;
            List<String> results = new ArrayList<>();

            for (Character character : characterSet) {
                String matchingResults = methods.stream()
                        .map(invokeMethodAndReturnNameWhenTrue(character))
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(",", "[", "]"));
                results.add(String.format("[%s] => %s", character, matchingResults));
            }

            results.forEach(System.out::println);

            System.out.println();


        } catch (IOException e) {
            LOG.error("", e);
        }
    }

    private Function<Method, String> invokeMethodAndReturnNameWhenTrue(Character character) {
        return m -> {
            try {
                if ((boolean) m.invoke(null, character)) {
                    return m.getName();
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                ExceptionUtils.rethrow(e);
            }
            return null;
        };
    }
}
