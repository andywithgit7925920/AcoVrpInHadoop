package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSUtil {
	private static Configuration conf = new Configuration();

	/**
	 * create a new file in HDFS
	 * 
	 * @param path
	 * @param content
	 * @throws IOException
	 */
	public static void CreateFile(String path, String content)
			throws IOException {
		FileSystem fs = null;
		FSDataOutputStream outputStream = null;
		try{
			Path dfs = new Path(path);
			/**
			 * single computer
			 */
			// FileSystem hdfs=FileSystem.get(conf);
			
			fs = dfs.getFileSystem(conf);
			if (fs.exists(dfs)) {
				fs.delete(dfs);
			}
			byte[] buff = content.getBytes();
			outputStream = fs.create(dfs);
			outputStream.write(buff, 0, buff.length);
		}finally{
			outputStream.flush();
			outputStream.close();
			fs.close();
		}
		System.out.println("Runing CreateFile over!!");
	}

	/**
	 * get the cache data
	 * 
	 * @param path
	 * @param conf
	 * @return
	 */
	public static String getCacheStr(Path path) throws Exception {
		FileSystem fsopen = null;
		FSDataInputStream in = null;
		Scanner scan = null;
		StringBuffer sb = null;
		try {
			fsopen = FileSystem.getLocal(conf);
			in = fsopen.open(path);
			scan = new Scanner(in);
			sb = new StringBuffer();
			while (scan.hasNext()) {
				sb.append(scan.next());
			}
		} finally {
			scan.close();
			in.close();
			fsopen.close();
		}
		return sb.toString();

	}

	/**
	 * read all files in a HDFS dictionary
	 * 
	 * @throws Exception
	 */
	public static String readFile(String path) throws Exception {
		FileSystem fs = null;
		FSDataInputStream in = null;
		Scanner scan = null;
		StringBuilder sb = null;
		try {
			Path filePath = new Path(path);
			/**
			 * single computer
			 */
			//fs = FileSystem.newInstance(conf);
			fs = filePath.getFileSystem(conf);
			in = fs.open(filePath);
			scan = new Scanner(in);
			sb = new StringBuilder();
			while (scan.hasNext()) {
				sb.append(scan.next());
			}
		} finally {
			//scan.close();
			//in.close();
			//fs.close();
		}
		return sb.toString();
	}
}
