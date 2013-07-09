package com.TroyEmpire.CenternetCentralUnit.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class IOUtil {

	/**
	 * @param object
	 *            an Serialized object which need to be converted into byte[]
	 * @return byte[] convert an Serialized Object into byte[]
	 */
	public static byte[] convertObjectToBytes(Object object) throws IOException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo;
		oo = new ObjectOutputStream(bStream);
		oo.writeObject(object);
		oo.close();
		byte[] serializedMessage = bStream.toByteArray();
		return serializedMessage;
	}

	public static <T> T convertBytesToObject(byte[] byteData)
			throws StreamCorruptedException, IOException,
			ClassNotFoundException {
		ObjectInputStream iStream;
		iStream = new ObjectInputStream(new ByteArrayInputStream(byteData));
		@SuppressWarnings("unchecked")
		T object = (T) iStream.readObject();
		iStream.close();
		return object;
	}

	/**
	 * delete a folder
	 */
	public static void deleteFolder(File folder) {
		if (folder.exists()) {
			File[] files = folder.listFiles();
			if (files != null) { // some JVMs return null for empty dirs
				for (File f : files) {
					if (f.isDirectory()) {
						deleteFolder(f);
					} else {
						f.delete();
					}
				}
			}
			folder.delete();
		} else {
			// the folder does not exist
		}
	}

	/**
	 * @param sourceFile
	 *            the filename of zip file including the absolute path
	 * @param destFileFolder
	 *            the folder in which the zipped files will be put in
	 */
	public static boolean unzipFile(String sourceFile, String destFileFolder) {
		try {
			File zipFile = new File(destFileFolder);
			if (!zipFile.exists())
				zipFile.mkdirs();
			ZipInputStream zipFileStream = new ZipInputStream(
					new FileInputStream(sourceFile));
			ZipEntry entry = zipFileStream.getNextEntry();
			while (entry != null) {
				String fileName = entry.getName();
				File newFile = new File(destFileFolder + File.separator
						+ fileName);
				if (entry.isDirectory())
					new File(newFile.getParent()).mkdirs();
				else {
					new File(newFile.getParent()).mkdirs();
					FileOutputStream out = new FileOutputStream(newFile);
					IOUtils.copy(zipFileStream, out);
				}
				entry = zipFileStream.getNextEntry();
			}
			zipFileStream.closeEntry();

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * zip a folder recursively
	 */
	public static void zipAFile(String sourceFile, String destFile) {
		ZIPFile appZip = new ZIPFile(sourceFile, destFile);
		appZip.generateFileList(new File(sourceFile));
		appZip.zipIt(destFile);
	}

	private static class ZIPFile {
		List<String> fileList;
		private String OUTPUT_ZIP_FILE;
		private String SOURCE_FOLDER;

		ZIPFile(String sourceFile, String destFile) {
			fileList = new ArrayList<String>();
			this.OUTPUT_ZIP_FILE = destFile;
			this.SOURCE_FOLDER = sourceFile;
		}

		/**
		 * Zip it
		 * 
		 * @param zipFile
		 *            output ZIP file location
		 */
		public void zipIt(String zipFile) {

			byte[] buffer = new byte[1024];

			try {
				FileOutputStream fos = new FileOutputStream(zipFile);
				ZipOutputStream zos = new ZipOutputStream(fos);
				for (String file : this.fileList) {
					ZipEntry ze = new ZipEntry(file);
					zos.putNextEntry(ze);
					FileInputStream in = new FileInputStream(SOURCE_FOLDER
							+ File.separator + file);
					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
					in.close();
				}
				zos.closeEntry();
				// remember close it
				zos.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		/**
		 * Traverse a directory and get all files, and add the file into
		 * fileList
		 * 
		 * @param node
		 *            file or directory
		 */
		public void generateFileList(File node) {

			// add file only
			if (node.isFile()) {
				fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
			}
			if (node.isDirectory()) {
				String[] subNote = node.list();
				for (String filename : subNote) {
					generateFileList(new File(node, filename));
				}
			}

		}

		/**
		 * Format the file path for zip
		 * 
		 * @param file
		 *            file path
		 * @return Formatted file path
		 */
		private String generateZipEntry(String file) {
			return file.substring(SOURCE_FOLDER.length() + 1, file.length());
		}
	}
}
