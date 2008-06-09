package com.flaptor.util;

import java.io.File;


/**
 * Use this class to avoid a serializing to be truncated by system exiting.
 * Given a file and an object, does this:
 * 
 *  object -> tempFile
 *  file -> oldFile
 *  tempFile -> file
 *  delete (oldFile)
 *  
 * @author Martin Massera
 *
 */
public class FileSerializer {
    
    private File file;
    private File oldFile;
    private File tempFile;
    
    public FileSerializer(File file) {
        this.file = file;
        oldFile = new File(file.getAbsolutePath()+".old");
        tempFile = new File(file.getAbsolutePath()+".temp");
    }
    
    public void serialize(Object obj) {
        IOUtil.serialize(obj, tempFile.getAbsolutePath(), true);
        file.renameTo(oldFile);
        tempFile.renameTo(file);
        oldFile.delete();
    }
    
    public Object deserialize() {
        Object obj = null;

        boolean ok = false;
        
        if (tempFile.exists()) {
            try {
                obj = IOUtil.deserialize(tempFile.getAbsolutePath(), true);
                tempFile.renameTo(file);
                ok = true;
            } catch (Throwable t) {}
            tempFile.delete();
        }
        if (!ok && file.exists()) {
            try {
                obj = IOUtil.deserialize(file.getAbsolutePath(), true);
                ok = true;
            } catch (Throwable t) {}
        }
        if (!ok && oldFile.exists()) {
            try {
                obj = IOUtil.deserialize(oldFile.getAbsolutePath(), true);
                oldFile.renameTo(file);
            } catch (Throwable t) {}
            oldFile.delete();
        }
        return obj;
    }
}
