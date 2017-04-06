package util;

import java.io.IOException;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSUtil {
	/**
	 * create a new file in HDFS
	 * @param path
	 * @param content
	 * @throws IOException
	 */
	public static void CreateFile(String path,String content) throws IOException
    {
        Configuration conf=new Configuration();
        Path dfs=new Path(path);
        /**
         * single computer
         */
        //FileSystem hdfs=FileSystem.get(conf);
        FileSystem fs = dfs.getFileSystem(conf);
        if(fs.exists(dfs)){
        	fs.delete(dfs);
        }
        byte[] buff=content.getBytes();
        FSDataOutputStream outputStream=fs.create(dfs);
        outputStream.write(buff,0,buff.length);
        outputStream.close();
        fs.close();
        System.out.println("Runing CreateFile over!!");
    }
	public static String getCacheStr(Path path,Configuration conf){
		FileSystem fsopen = null;
		FSDataInputStream in = null;
		Scanner scan = null;
		StringBuffer sb = null;
		try{
			fsopen = FileSystem.getLocal(conf);
			in = fsopen.open(path);
			scan = new Scanner(in);
			sb = new StringBuffer();
			while (scan.hasNext()) {
				sb.append(scan.next());
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			scan.close();
			try {
				in.close();
				fsopen.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
		
	}
}
