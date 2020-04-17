package com.merico.inftest.commonutils;

import com.google.common.base.Splitter;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.collections.Lists;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

public class ReaderFileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileUtils.class);
    public static String getFileContext(String fileName){

        File file = new File(fileName);
        try {
            byte[] bytes = Files.toByteArray(file);
            return new String(bytes);
        } catch (IOException e) {
            LOGGER.error("read file error : " + fileName);
            throw  new RuntimeException("read file error! fileName: " + fileName);
        }
    }

    /**
     * 输入目录  testcases/demo/*.json
     *          testcases/demo/*
     *          testcases/newTest.json
     * @param fileNames
     * @return
     */
    public static List<String> getFilesWithPath(String fileNames){

        List<String> fileList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(fileNames);
        List<String> fileWihtPath = Lists.newArrayList();

        for(String fileName : fileList){
            //如果以这个开头则需要选择该目录下所有的case文件
            if(fileName.endsWith("*") || fileName.endsWith("*.json")){
                getTestFileFromDir(fileName,fileWihtPath);
            } else {
                //单个文件处理
                getTestFilePath(fileName,fileWihtPath);
            }
        }
        return  fileWihtPath;
    }

    private static void getTestFilePath(String fileName, List<String> fileWihtPath) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if(null == resource ){
            return;
        }
        fileWihtPath.add(resource.getPath());


    }

    public static String getFilePath(String fileName){
        URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if(null == resource ){
            return "";
        }
        return resource.getPath();
    }

    private static void getTestFileFromDir(String fileName, List<String> fileWihtPath) {
        int lastIndex = fileName.lastIndexOf("/");
        String dirPath = fileName.substring(0, lastIndex);
        //遍历目录下的所有的文件 符合条件添加
        //获取resource目录
        Enumeration<URL> resource = null;
        try {
            resource = Thread.currentThread().getContextClassLoader().getResources(dirPath);
            while (resource.hasMoreElements()){
                URL url = resource.nextElement();
                String path = url.getPath();
                for(String filePath :filterPath(new File(path))){
                    fileWihtPath.add(filePath);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        }
    }

    /**
     * 递归遍历找到所有的文件
     * @param file
     * @return
     */
    private static List<String> filterPath(File file) {
        List<String> filePaths = Lists.newArrayList();
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File fileTmp : files){
                filePaths.addAll(filterPath(fileTmp));
            }
        } else {
            filePaths.add(file.getAbsolutePath());
        }

        return filePaths;
    }

    /**
     * 迭代获取给定路径下所有文件的名字
     * @param path
     * @return
     *
     */
    public static List<String> getFileNames(String path){
        List<String> fileNames = Lists.newArrayList();
        File file=new File(path);
        File[] tempList = file.listFiles();
        if(tempList.length>0){
            for(File f : tempList) {
                //如果是文件，直接打印文件名称
                if(f.isFile()){
                    fileNames.add(f.getName());
                }
                if(f.isDirectory()){
                    getFileNames(path+"/"+f.getName());
                }
            }
        }
        return fileNames;
    }
}
