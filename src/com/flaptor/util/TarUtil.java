package com.flaptor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Utilities for tar files 
 */
public class TarUtil {
    private static Logger logger = Logger.getLogger(Execute.whoAmI());
	
	/**
	 * extracts a tar file to a directory. Can be compressed (.tar.gz or .tgz) or uncompressed
	 * @param tarFile the tar file
	 * @param destDir the destination directory
	 * @throws IOException
	 * @return true if success, false otherwise
	 */
	public static boolean untarFile(File tarFile, File destDir) throws IOException {
		boolean compressed = tarFile.getName().endsWith("gz") || tarFile.getName().endsWith("GZ");
		if (!tarFile.exists()) throw new FileNotFoundException(tarFile.getPath());
		String command = "tar xvf" + (compressed ? "z" : "") + " " + tarFile.getAbsolutePath() + " --directory=" + destDir.getAbsolutePath(); 
		return CommandUtil.execute(command, null).first() == 0;
	}

	/**
	 * Creates a tar file
	 * @param tarFile the destination tar file, can be .tgz or .tar
	 * @param srcFile the file or dir to be tared
	 * @param fromDir the file from where to execute the tar, can be null for absolute
	 * @return true if everything went ok
	 * @throws IOException 
	 */
	public static boolean tarFile(File tarFile, File srcFile, File fromDir) throws IOException {
		String src = null;
		if (fromDir != null) {
			srcFile = srcFile.getCanonicalFile();
			fromDir = fromDir.getCanonicalFile();
			src = srcFile.getAbsolutePath();
			String from = fromDir.getAbsolutePath();
			if (!from.endsWith("/")) from += "/";
			if (src.startsWith(from)) src = src.substring(from.length());
		}
		
		boolean compressed = tarFile.getName().endsWith("gz") || tarFile.getName().endsWith("GZ");
		String command = "tar cvf" + (compressed ? "z" : "") + " " + tarFile.getAbsolutePath() + " " + (src != null ? src : srcFile.getAbsolutePath());
		//TODO do this with a library!!!!
		return CommandUtil.execute(command, fromDir).first() == 0;
	}
}
