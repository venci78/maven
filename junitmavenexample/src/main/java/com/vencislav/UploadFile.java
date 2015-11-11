package com.vencislav;

import java.io.File;
import java.nio.*;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.time.StopWatch;

public class UploadFile {
	private static final String codes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

	private static String encodeFileToBase64Binary(File file) {
		String encodedfile = null;
		try {
			FileInputStream fileInputStreamReader = new FileInputStream(file);
			byte[] bytes = new byte[(int) file.length()];
			fileInputStreamReader.read(bytes);
			encodedfile = Base64.getEncoder().encodeToString(bytes);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return encodedfile;
	}

	public static void main(String args[]) {
		Connection c = null;
		PreparedStatement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "pdv", "pdv");
			System.out.println("Opened database successfully");
			List<String> results = new ArrayList<String>();
			File dir = new File("D:/jboss/eclipse/workspace/LTF");
			Iterator<File> files =  FileUtils.iterateFilesAndDirs(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			 StopWatch  timerAll = new StopWatch() ;
			 timerAll.start();
			while(files.hasNext()){
			File file = (File)files.next();
				if (file.isFile()) {
					StopWatch timeForAFile = new StopWatch();
					timeForAFile.start();
					String toDB = encodeFileToBase64Binary(file);
					String sql = "INSERT INTO test (file_id,file_name,file_data,file_date) VALUES (nextval('test_file_id_seq'),?,?,?)";
					stmt = c.prepareStatement(sql);

					stmt.setString(1, file.getAbsolutePath());
					stmt.setString(2, toDB);
					stmt.setDate(3, new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
					stmt.executeUpdate();
					timeForAFile.stop();
					System.out.println( timeForAFile.toString());

				}
			}
timerAll.stop();
System.out.println("=================================================");
System.out.println(timerAll.toString());
 
			stmt.close();
			/*
			 * Statement statement = null; statement = c.createStatement();
			 * ResultSet rs = statement.executeQuery(
			 * "select file_data from test limit 1"); while (rs.next()) { String
			 * encoded = rs.getString("file_data"); byte[] decoded =
			 * Base64.getDecoder().decode(encoded); FileOutputStream fos = new
			 * FileOutputStream("c:/temp/2.jpg"); fos.write(decoded);
			 * fos.close(); }
			 */

			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Table created successfully");
	}
}
