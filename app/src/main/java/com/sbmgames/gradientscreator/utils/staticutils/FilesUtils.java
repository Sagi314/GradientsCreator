package com.sbmgames.gradientscreator.utils.staticutils;

import android.graphics.Bitmap;

import com.sbmgames.gradientscreator.utils.OutParameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class FilesUtils
{
    private static final int OK = 0;
    private static final int IO = 1;
    private static final int FILE_NO_FOUND = 2;

    private FilesUtils() { }

    private static int privateTryRead(File file, OutParameter<String> outFileContent)
    {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file)))
        {
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                text.append(line).append('\n');
            }
            //deletes the '\n' added after the last line
            text.deleteCharAt(text.length() - 1);

            //passing the text to the user
            outFileContent.set(text.toString());
            return OK;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return FILE_NO_FOUND;
        }
        catch (IOException e)
        {
            return IO;
        }
    }

    private static boolean privateTryWrite(File file, String content, boolean append)
    {
        try (FileWriter writer = new FileWriter(file, append))
        {
            writer.append(content);
            writer.flush();

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }


    public static boolean tryRead(File file, OutParameter<String> outFileContent)
    {
        return privateTryRead(file, outFileContent) == OK;
    }

    public static boolean tryRead(File parentFolder, String fileName, OutParameter<String> outFileContent)
    {
        var file = new File(parentFolder, fileName);

        return tryRead(file, outFileContent);
    }


    public static boolean tryWrite(File file, String content)
    {
        return privateTryWrite(file, content, false);
    }

    public static boolean tryWrite(File parentFolder, String fileName, String content)
    {
        var file = new File(parentFolder, fileName);

        return tryWrite(file, content);
    }


    public static boolean tryAppend(File file, String contentToAppend)
    {
        return privateTryWrite(file, contentToAppend, true);
    }

    public static boolean tryAppend(File parentFolder, String fileName, String contentToAppend)
    {
        var file = new File(parentFolder, fileName);

        return tryAppend(file, contentToAppend);
    }


    public static boolean tryAppendLine(File file, String content)
    {
        return privateTryWrite(file, content + "\n", true);
    }

    public static boolean tryAppendLine(File parentFolder, String fileName, String content)
    {
        var file = new File(parentFolder, fileName);

        return tryAppendLine(file, content);
    }


    public static boolean tryDelete(File file)
    {
        return file.delete();
    }

    public static boolean tryDelete(File parentFolder, String fileName)
    {
        var file = new File(parentFolder, fileName);

        return tryDelete(file);
    }

    public static boolean createDirectory(File parentFolder, String folderName)
    {
        return new File(parentFolder, folderName).mkdir();
    }

    public static boolean createDirectoryAndPath(File parentFolder, String folderName)
    {
        return new File(parentFolder, folderName).mkdirs();
    }

    /**
     * will create a directory and path even if the there is a file named same as {@code folderName}.
     * <p><b>in case it happens the file will be deleted!!!</b></p>
     * @param parentDirectory the directory to create the new folder in.
     * @param directoryName the name of the new directory.
     * @return the created file or {@code null} if directory not created.
     */
    public static File createDirectoryAndPathEvenIfFileExists(File parentDirectory, String directoryName)
    {
        var file = new File(parentDirectory, directoryName);

        if (file.isDirectory())
        {
            return file;
        }

        //means its not a directory - delete
        if (file.exists())
        {
            FilesUtils.tryDelete(file);
        }

        //now we are trying to create it again - as directory
        return file.mkdirs() ? file : null;
    }

    public static File createDirectoryEvenIfFileExists(File parentDirectory, String directoryName)
    {
        var file = new File(parentDirectory, directoryName);

        if (file.isDirectory())
        {
            return file;
        }

        //means its not a directory - delete
        if (file.exists())
        {
            FilesUtils.tryDelete(file);
        }

        //now we are trying to create it again - as directory
        return file.mkdir() ? file : null;
    }

    //todo check prefix, suffix, set to default etc..
    public static File createFileWithTimeStamp(File parentDirectory, String prefix, String suffix)
    {
        prefix = prefix == null ? "" : prefix;
        suffix = suffix == null || suffix.length() < 3? ".tmp" : (suffix.startsWith(".") ? suffix : "." + suffix);

        return new File(parentDirectory, prefix + System.currentTimeMillis() + suffix);
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, Bitmap.CompressFormat imageFormat, File file)
    {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file))
        {
            bitmap.compress(imageFormat, 100, fileOutputStream);

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();

            return false;
        }
    }
}