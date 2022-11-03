package local_storage_impl;

import org.Storage;
import org.StorageManager;

import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
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
        if(path.equals("users.json")){
            System.out.println("Nije dozvoljeno premestanje fajla users.json!");
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
    public void copyFile(String s, String s1) {

    }

    @Override
    public void downloadFile(String s) {

    }

    @Override
    public void deleteFile(String s) {

    }

    @Override
    public void listAll(String s) {

    }

    @Override
    public void listFiles(String s) {

    }

    @Override
    public void listDirs(String s) {

    }

    @Override
    public void listByName(String s, String s1) {

    }

    @Override
    public void renameFile(String s, String s1) {

    }

    @Override
    public void listFilesWithExt(String s, String s1) {

    }

    @Override
    public void listSubstringFiles(String s, String s1) {

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
    public void sortByName(String s, String s1, String s2) {

    }

    @Override
    public void sortByDate(String s, String s1, String s2) {

    }

    @Override
    public void sortByModification(String s, String s1, String s2) {

    }
}
