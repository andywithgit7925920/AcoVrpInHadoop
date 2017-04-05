package util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
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
}
