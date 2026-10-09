package com.aptech.s09;

import com.aptech.s09.model.Student;
import com.aptech.s09.nio.RegistryExport;
import com.aptech.s09.repository.H2StudentRepository;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Example 10 — {@code java.nio.file} doing the whole job: write, read, walk, glob, compare,
 * copy, move, and then write into something that is not a disk at all.
 *
 * <p>The last section is the one that changes how you think about the API. A {@link Path}
 * does not belong to a drive; it belongs to a {@link FileSystem}, and the JDK ships a second
 * one that stores files inside a zip. The identical {@code Files.copy(…)} call writes to
 * either, because it never asked which.
 */
public final class E10_FileSystem {

    private static final Path EXPORT = Path.of("sandbox", "export");

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();

        List<Student> students = new H2StudentRepository().findAll();
        RegistryExport export = new RegistryExport(EXPORT);

        // Start from nothing, so this example prints the same thing on the tenth run
        // as it did on the first.
        export.deleteAll();
        Files.createDirectories(EXPORT.getParent());

        System.out.println(Db.rule("1. A Path is a name, not an open file"));
        System.out.println("  the folder  : " + EXPORT);
        System.out.println("  exists yet? : " + Files.exists(EXPORT));
        Path file = export.export(students);
        List<String> head = Files.readAllLines(file, StandardCharsets.UTF_8);
        System.out.println("  exported to : " + file);
        System.out.println("  bytes       : " + Files.size(file));
        System.out.println("  header line : " + head.get(0));
        System.out.println("  second line : " + head.get(1));

        System.out.println(Db.rule("2. Read it back and compare"));
        List<Student> back = export.read();
        System.out.println("  read back   : " + back.size() + " students");
        System.out.println("  equal to the database : " + students.equals(back));
        System.out.println();
        System.out.println("  That is one line because Student is a record. With a generated");
        System.out.println("  toString() and a hand-written equals, this check would be a test of");
        System.out.println("  the test rather than a test of the file.");

        System.out.println(Db.rule("3. Files.walk - the tree, in an order you chose"));
        for (Student s : students.subList(0, 3)) {
            export.writeOne(s);
        }
        for (Path p : export.tree()) {
            Path relative = EXPORT.relativize(p);
            boolean directory = Files.isDirectory(p);
            String indent = "  ".repeat(Math.max(0, relative.getNameCount() - 1));
            String name = relative.getNameCount() == 0
                    ? EXPORT + "/"
                    : relative.getFileName() + (directory ? "/" : "");
            System.out.println("    " + indent + name
                    + (directory ? "" : "   (" + Files.size(p) + " bytes)"));
        }
        System.out.println();
        System.out.println("  Files.walk gives whatever order the file system hands out. RegistryExport");
        System.out.println("  sorts, because a report anyone has to diff cannot change order between");
        System.out.println("  two runs on two machines.");

        System.out.println(Db.rule("4. Two ways to ask for *.txt"));
        System.out.println("  Files.newDirectoryStream(folder, \"*.txt\") - one level:");
        for (Path p : export.txtFiles()) {
            System.out.println("    " + EXPORT.relativize(p));
        }
        System.out.println("  PathMatcher \"glob:**/*.txt\" - the whole tree:");
        for (Path p : export.everythingTxt()) {
            System.out.println("    " + EXPORT.relativize(p));
        }
        System.out.println();
        System.out.println("  (On Windows a path prints with backslashes, because that is this");
        System.out.println("   file system's separator. The code above never names one.)");

        System.out.println(Db.rule("5. A FileSystem that is not a disk"));
        Path zip = Path.of("sandbox", "registry.zip");
        Files.deleteIfExists(zip);
        URI uri = URI.create("jar:" + zip.toUri());
        try (FileSystem zipFs = FileSystems.newFileSystem(uri, Map.of("create", "true"))) {
            Path inside = zipFs.getPath("/registry.txt");
            Files.copy(file, inside, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  wrote into the zip : " + inside);
            System.out.println("  its size inside    : " + Files.size(inside) + " bytes");
            System.out.println("  the disk separator : \"" + FileSystems.getDefault().getSeparator()
                    + "\"   the zip separator : \"" + zipFs.getSeparator() + "\"");
            try (Stream<Path> entries = Files.list(zipFs.getPath("/"))) {
                System.out.println("  entries in the zip : "
                        + entries.map(p -> p.getFileName().toString()).sorted().toList());
            }
        }
        System.out.println("  Files.copy, Files.size and Files.list did not change. Only the");
        System.out.println("  FileSystem the paths belong to did.");

        System.out.println(Db.rule("6. mismatch, copy and move"));
        Path copy = EXPORT.resolve("all-copy.txt");
        Files.copy(file, copy, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("  mismatch(file, copy)   : " + Files.mismatch(file, copy)
                + "   (-1: byte-for-byte identical)");

        Path edited = EXPORT.resolve("all-edited.txt");
        Files.writeString(edited, Files.readString(file).replace("Ada", "Zoe"));
        System.out.println("  mismatch(file, edited) : " + Files.mismatch(file, edited)
                + "   (the offset of the first byte that differs)");

        Path backup = EXPORT.resolve("all-backup.txt");
        Files.move(copy, backup, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("  after the move, all-copy.txt   exists: " + Files.exists(copy));
        System.out.println("  after the move, all-backup.txt exists: " + Files.exists(backup));
        System.out.println();
        System.out.println("  mismatch never reads either file into memory. Comparing two 4 GB");
        System.out.println("  backups is one call, and that is the entire argument for it.");

        System.out.println(Db.rule("7. Path and File are not the same thing"));
        File legacy = file.toFile();
        System.out.println("  Path : " + file);
        System.out.println("  File : " + legacy + "   (the same name, and almost nothing else)");
        System.out.println();
        System.out.println("  File cannot walk a tree, copy with options, match a glob, or name");
        System.out.println("  anything inside a zip. Path is used everywhere in this project except");
        System.out.println("  this one line, which is here only to show the old type still works.");
    }
}
