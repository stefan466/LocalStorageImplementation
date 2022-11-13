package local_storage_impl;

import org.Storage;
import org.StorageManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class LocalStorageImplementation extends Storage {

    static {
        StorageManager.registerStorage(new LocalStorageImplementation());
    }

    public LocalStorageImplementation() {

    }

    @Override
    public boolean initStorage(String name, String path) {
        File root = new File(path + File.separator + name);

        if(root.exists()){
            File[] files = root.listFiles();

            if(files.length != 0) {

                boolean valid = false;

                for (File file : files) {
                    if (file.getName().equals("config.properties")) {
                        valid = true;
                        break;
                    }
                }

                if(valid){
                    System.out.println("Uspesna konekcija na vec postojece skladiste!");
                    System.out.println("Za pomoc ukucajte /help");
                    return true;
                }else {
                    System.out.println("Konekcija neuspesna!");
                    return false;
                }
            } else {

                File config = new File(root, "config.properties");
                try {
                    config.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Uspesno kreiranje skladista na novom folderu!");
                System.out.println("Za pomoc ukucajte /help");

                return true;
            }
        }else {

            root.mkdir();

            File config = new File(root, "config.properties");
            try {
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Uspesno kreiranje novog foldera kao skladiste!");
            System.out.println("Za pomoc ukucajte /help");
            return true;
        }

    }

    @Override
    public void createNewFile(String newFilePath, String destPath) {
        if(newFilePath.equals("config.properties")) {
            System.out.println("Nije dozvoljeno kreiranje fajla pod nazivom config.properties");
            return;
        }

        if(destPath.equals("") || destPath.startsWith(File.separator)) {
            String rootPath = super.getPath();

            File newFile = new File(rootPath.concat(destPath).concat(System.getProperty("file.separator")).concat(newFilePath));
            if(newFile.exists()) {
                System.out.println("Fajl sa imenom " + newFile + " vec postoji.");
            } else {
                if(newFilePath.contains(".")) {
                    try {
                        boolean success = newFile.createNewFile();
                        if(success) {
                            System.out.println("Uspesno kreiran fajl na putanji " + newFile.getPath());
                        }

                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    newFile.mkdir();
                    System.out.println("Uspesno kreiran fajl na putanji " + newFile.getPath());
                }
            }
        } else if(!destPath.startsWith(File.separator)) {
            System.out.println("Pogresno zadata putanja destinacije. Putanja mora poceti separatorom");
        }

    }

    @Override
    public void moveFile(String path, String destinationPath) {
        if(path.equals("config.properties")){
            System.out.println("Nije dozvoljeno premestanje fajla config.properties!");
            return;
        }

        if(!destinationPath.startsWith(File.separator)){
            System.out.println("Pogreno zadata putanja destinacije. Putanja mora poceti separatorom.");
            return;
        }

        String rootPath = super.getPath();
        File sourceFile = new File(rootPath.concat(System.getProperty("file.separator")).concat(path));

        if(!sourceFile.exists()) {
            System.out.println("Fajl " + sourceFile.getPath() + " ne postoji");
        }

        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] parsedPath = sourceFile.getPath().split(pattern);
        String fileName = parsedPath[parsedPath.length-1];


        File destFile = new File(rootPath.concat(destinationPath));

        if(!destFile.exists()) {
            System.out.println("Zadata putanja na kojoj se premesta fajl/fajlovi ne postoji. Kreira se direktorijum sa nazivom " + destinationPath.substring(1));
            destFile.mkdir();
        }

        File newSourceFile = new File(rootPath.concat(destinationPath).
                concat(System.getProperty("file.separator")).
                concat(fileName));

        boolean valid = true;
        for(String name : destFile.list()) {
            if(name.equals(newSourceFile.getName())) {
                valid=false;
                break;
            }
        }
        if(valid) {
            sourceFile.renameTo(newSourceFile);
            System.out.println("Fajl (" + sourceFile.getPath() + ") je uspesno premesten na putanju " + newSourceFile.getPath());
        } else {
            System.out.println("Na putanji " + destFile.getPath() + " postoji fajl sa istim imenom.");
        }



    }

    @Override
    public void copyFile(String sourcePath, String destPath) {
        if(sourcePath.equals("config.properties")) {
            System.out.println("Nije dozvoljeno kopiranje fajla config.properties");
            return;
        }
        if(!destPath.startsWith(File.separator)) {
            System.out.println("Pogresno zadata putanja destinacije. Putanja mora zapoceti separatorom");
            return;
        }

        String rootPath = super.getPath();

        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] pathElements = sourcePath.split(pattern);
        String desiredFilePath = pathElements[pathElements.length-1];

        File sourceFile = new File(rootPath.concat(System.getProperty("file.separator")).concat(sourcePath));
        File destFile = new File(rootPath.concat(destPath));
        File desiredDestFile = new File(rootPath.concat(destPath).concat(System.getProperty("file.separator")).concat(desiredFilePath));

        if(sourceFile.isFile()) {
            boolean valid = true;
            for(String name : destFile.list()) {
                if(name.equals(sourceFile.getName())) {
                    valid=false;
                    break;
                }
            }
            if(valid) {
                try {
                  fileCopy(sourceFile, desiredDestFile);
                }  catch(IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Uspesno kopiranje fajla" + sourceFile.getPath() + " na putanju " + desiredDestFile.getPath());
            } else {
                System.out.println("Fajl na putanji " + desiredDestFile + " vec postoji");
            }
        } else if(sourceFile.isDirectory()) {
            boolean valid = true;
            for(String name : destFile.list()) {
                if(name.equals(sourceFile.getName())) {
                    valid = true;
                    break;
                }
            }
            if(valid) {
                try {
                    copyDir(sourceFile, desiredDestFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Uspesno kopiranje direktorijuma " + sourceFile.getPath() + " na putanju " + desiredDestFile.getPath());
            } else {
                System.out.println("Direktorijum na putanji " + desiredDestFile + " vec postoji.");
            }
        } else {
            System.out.println("Prosledjeni fajl/direktorijum ne postoji");
        }


    }

    private void fileCopy(File src, File dest) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);


            byte[] buffer = new byte[1024];
            int length;

            while((length=in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } finally {
            if(in!=null) {
                in.close();
            }if(out!=null) {
                out.close();
            }
        }
    }

    public void copyDir(File src, File dest) throws IOException {
        if(src.isDirectory()) {
            if(!dest.exists()) {
                dest.mkdir();
            }
            String files[] = src.list();

            for(String fileName : files) {
                File srcFile = new File(src, fileName);
                File destFile = new File(dest, fileName);

                copyDir(srcFile, destFile);
            }
        } else {
            if(!dest.exists()) {
                fileCopy(src, dest);
            }
        }
    }

    @Override
    public void downloadFile(String sourcePath) {
        String rootPath = super.getPath();

        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] pathElements = sourcePath.split(pattern);
        String desiredFilePath = pathElements[pathElements.length - 1];

        String DOWNLOADSPATH = System.getProperty("user.home").concat(System.getProperty("file.separator")).concat("Downloads");

        File sourceFile = new File(rootPath.concat(System.getProperty("file.separator")).concat(sourcePath));
        File newSourceFile = new File(DOWNLOADSPATH + File.separator + desiredFilePath);

        if(sourceFile.isFile()){
            try {
                fileCopy(sourceFile, newSourceFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Uspesno preuzimanje fajla sa putanje " + sourceFile.getPath());
        } else if (sourceFile.isDirectory()){
            try {
                this.copyDir(sourceFile, newSourceFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Uspesno preuzimanje direktorijuma sa putanje " + sourceFile.getPath());
        } else {
            System.out.println("Prosledjeni fajl/direktorijum na putanji " + sourceFile.getPath() + " ne postoji.");
        }

    }

    @Override
    public void deleteFile(String path) {
        if(path.equals("config.properties")) {
            System.out.println("Nije dozvoljeno brisanje fajla config.properties");
            return;
        }
        String rootPath = super.getPath();

        File file = new File(rootPath.concat(System.getProperty("file.separator")).concat(path));

        if(file.isFile()) {
            boolean success = file.delete();

            if(success) {
                System.out.println("Uspesno obrisan fajl na putanji " + file.getPath());

            } else if(file.isDirectory()) {
                try {
                    Files.walk(Path.of(file.getPath()))
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);

                } catch(IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Prosledjeni fajl/direktorijim na putanji " + file.getPath() + " ne postoji");

            }
        }

    }

    @Override
    public boolean uploadFile(String s) {
        return false;
    }

    @Override
    public List listFilesCreatedPeriod(String s, String s1) {
        return null;
    }

    @Override
    public List listAll(String path) {
        if(path.equals("") || path.startsWith(File.separator)) {
            String rootPath = super.getPath();

            String absolutePath = rootPath.concat(path);
            File file = new File(absolutePath);

            try {
                showDir(3, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(!path.startsWith(File.separator)) {
            System.out.println("Pogresno zadata putanja destinacije. Putanja mora poceti separatorom");
        }

        return null;
    }

    private void showDir(int indent, File file) throws IOException{
        for(int i=0; i<indent; i++) {
            if(i==indent-2) {
                if(file.isDirectory())
                    System.out.println(">");
                else if(file.isFile())
                    System.out.println("-");
            } else {
                System.out.println(" ");
            }
        }
        System.out.println(file.getName());
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(int i=0; i<files.length; i++) {
                showDir(i+3, files[i]);
            }
        }
    }

    @Override
    public List listFiles(String path) {
        List<File> fileList = null;

        if(path.equals("") || path.startsWith(File.separator)) {
            String rootPath = super.getPath();

            String absolutePath = rootPath.concat(path);
            File file = new File(absolutePath);

            if(file.isFile()) {
                System.out.println("mozete izlistavati samo dir");
            } else if(file.isDirectory()) {
                File[] files = file.listFiles();
                for(int i=0; i<files.length; i++) {
                    System.out.println("--" + files[i].getName());
                }
                fileList.addAll(Arrays.asList(files));
            }
        } else if(!path.startsWith(File.separator)) {
            System.out.println("Pogresno zadatak putanja destinacije. Putanja mora poceti separatorom");

        }
        return fileList;
    }

    @Override
    public List listDirs(String path) {
        if(path.equals("") || path.startsWith(File.separator)) {
            String rootPath = super.getPath();

            String absolutePath = rootPath.concat(path);
            File file = new File(absolutePath);

            try {
                showDirOnly(3, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(!path.startsWith(File.separator)) {
            System.out.println("Pogresno zadata putanja destinacije. Putanja mora zapoceti separtorom");
        }



        return null;
    }

    @Override
    public List listByName(String s, String s1) {

        return null;
    }

    @Override
    public void renameFile(String s, String s1) {

    }

    @Override
    public List listFilesWithExt(String s, String s1) {

        return null;
    }

    @Override
    public List listSubstringFiles(String s, String s1) {

        return null;
    }

    @Override
    public boolean containsFile(String s, List<String> list) {
        return false;
    }

    @Override
    public String returnDir(String s) {
        return null;
    }

    @Override
    public List sortByName(String s, String s1, String s2) {

        return null;
    }

    @Override
    public List sortByDate(String s, String s1, String s2) {

        return null;
    }

    @Override
    public List sortByModification(String s, String s1, String s2) {

        return null;
    }

    static void showDirOnly(int indent, File file) throws IOException {

        if(file.isDirectory()) {
            for (int i = 0; i < indent; i++)
                if (i == indent - 2) {
                    System.out.print('>');
                } else {
                    System.out.print(' ');
                }
            System.out.println(file.getName());

            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++)
                showDirOnly(indent + 3, files[i]);
        }
    }
}
