package com.zlcdgroup.libs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import android.text.TextUtils;






public class FileUtil  {

	public  static  boolean  isImage(File  file){
		if(null != file && file.exists()){
			if(file.getAbsolutePath().endsWith(".jpg")){
				return  true;
			}
		}
		return  false;
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取目录文件大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

//	/**
//	 * 删除文件夹
//	 * 
//	 * @param sPath
//	 * @return
//	 */
//	public static boolean deleteDirectory(String sPath) {
//		boolean flag = false;
//		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
//		if (!sPath.endsWith(File.separator)) {
//			sPath = sPath + File.separator;
//		}
//		File dirFile = new File(sPath);
//		// 如果dir对应的文件不存在，或者不是一个目录，则退出
//		if (!dirFile.exists() || !dirFile.isDirectory()) {
//			return false;
//		}
//		flag = true;
//		// 删除文件夹下的所有文件(包括子目录)
//		File[] files = dirFile.listFiles();
//		for (int i = 0; i < files.length; i++) {
//			// 删除子文件
//			if (files[i].isFile()) {
//				flag = deleteFile(files[i].getAbsolutePath());
//				if (!flag)
//					break;
//			} // 删除子目录
//			else {
//				flag = deleteDirectory(files[i].getAbsolutePath());
//				if (!flag)
//					break;
//			}
//		}
//		if (!flag) {
//			return false;
//		}
//		// 删除当前目录
//		if (dirFile.delete()) {
//			return true;
//		} else {
//			return false;
//		}
//	}

	
	  /**
     * 根据路径 删除文件或者目录
     * @param   path 被删除目录的文件路径
     * @return  路径为空、删除失败返回false
     */
    public static  boolean deleteDirectory(String path) {
    	try{
    		if(path==null||path.isEmpty()) 
    			return false;
	        File dirFile = new File(path);
	        if (!dirFile.exists()) {
	            return false;
	        }
	        if (dirFile.isFile()) {//文件
	            return dirFile.delete();
	        }else  if(dirFile.isDirectory()){//目录
		        File[] files = dirFile.listFiles();
		        for (File file : files) {
		        	 if(!deleteDirectory(file.getAbsolutePath()))
		        		 return false;
		        }
		        //删除当前目录
		       return dirFile.delete();
	        }
	        return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    
	
	
	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 * @return
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return "";
		}

		int filePosi = filePath.lastIndexOf(File.separator);
		return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
	}
	
	/**
	 * 获取路径
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFolderName(String filePath) {

		if (TextUtils.isEmpty(filePath)) {
			return filePath;
		}

		int filePosi = filePath.lastIndexOf(File.separator);
		return (filePosi == -1) ? "" : filePath.substring(0, filePosi+1);
	}
	
	public   static   boolean   isFileExists(String  filepath){
		if(TextUtils.isEmpty(filepath)){
			return false;
		}
		try {
			File   file   =  new   File(filepath);
			return file.exists();
		} catch (Exception e) {
			return   false;
		}
	}

	
	  /**
     * write file
     * 
     * @param filePath
     * @param content
     * @param append is append, if true, write to the end of file, else clear content of file and write into it
     * @return return false if content is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }
    
	/**
     * 复制单个文件(原名复制)
     * @param oldPathFile 准备复制的文件源
     * @param ：到目录  不需要文件名）
     * @return
     */
    public static boolean CopySingleFileTo(String oldPathFile, String targetPath) {
    	boolean flag = false;
        try {
            int byteread = 0;
            File oldfile = new File(oldPathFile);
            File moveDir = new File(targetPath);
			if (!moveDir.exists()) {
				moveDir.mkdirs();
			}
            String targetfile = targetPath + File.separator +  oldfile.getName();
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPathFile); //读入原文件
                FileOutputStream fs = new FileOutputStream(targetfile);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
                inStream.close();
                flag = true;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        	flag = false;
        }
        return flag;
    }
    
    /**
     * 复制单个文件(原名复制)
     * @param oldPathFile 准备复制的文件源
     *
     * @return
     */
    public static boolean CopySingleFileTo(String oldPathFile, String targetPath,String newFileName) {
    	boolean flag = false;
    	InputStream inStream = null;
		FileOutputStream   fs = null;
        try {
            int byteread = 0;
            File oldfile = new File(oldPathFile);
            File moveDir = new File(targetPath);
			if (!moveDir.exists()) {
				moveDir.mkdirs();
			}
		
            String targetfile = targetPath + File.separator +  oldfile.getName();
            if (oldfile.exists()) { //文件存在时
                 inStream = new FileInputStream(oldPathFile); //读入原文件
                 fs = new FileOutputStream(targetfile);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
              
                flag = true;
            }
            if(flag && !TextUtils.isEmpty(newFileName)){
            	renameFile(targetPath, oldfile.getName(), newFileName);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        	flag = false;
        }finally{
        	try {
        		if(null != fs){
             	   fs.close();
             	}
             	if(null != inStream){
             		inStream.close();
             	}
			} catch (Exception e2) {
				// TODO: handle exception
			}
        	
        	
        }
        return flag;
    }
    
    
    public static boolean renameFile(String Directory,String oldname,String newname){ 
        if(!oldname.equals(newname)){//新的文件名和以前文件名不同时,才有必要进行重命名 
            File oldfile=new File(Directory,oldname); 
            File newfile=new File(Directory,newname); 
            if(!oldfile.exists()){
                return false;//重命名文件不存在
            }
            if(newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
               return true ;
            else{ 
               return  oldfile.renameTo(newfile); 
            } 
        }else{
            return true;
        }
    } 
}
