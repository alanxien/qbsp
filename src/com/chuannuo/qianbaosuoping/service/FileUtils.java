package com.chuannuo.qianbaosuoping.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/** 
 * @ClassName: FileUtils 
 * @Description: 文件工具�? 
 * @author xin.xie 
 * @date 2016-3-17 上午11:54:40 
 *  
 */
public class FileUtils {
	
	
	/** 
     * @Title: DeleteFile 
     * @Description: 删除文件 
     * @param @param file 
     * @return void
     * @throws 
     */
    public static void deleteFile(File file) { 
        if (file.exists() == false) { 
            return; 
        } else { 
            if (file.isFile()) { 
                file.delete(); 
                return; 
            } 
            if (file.isDirectory()) { 
                File[] childFile = file.listFiles(); 
                if (childFile == null || childFile.length == 0) { 
                    file.delete(); 
                    return; 
                } 
                for (File f : childFile) { 
                    deleteFile(f); 
                } 
                file.delete(); 
            } 
        } 
    } 
    
    /**
	 * @Title: copyFile
	 * @Description: 文件复制
	 * @param @param oldFile
	 * @param @param newFile
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("resource")
	public void copyFile(String url, File toFile) {
		try {
			int bytesum = 0;
			int byteread = 0;
			if (!url.isEmpty()) { // 文件存在�?
				InputStream inStream = new FileInputStream(url); // 读入原文�?
				FileOutputStream fs = new FileOutputStream(toFile);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节�? 文件大小
					//System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}
}
