package com.sagi314.gradientscreator.utils.staticutils;

import android.graphics.Bitmap;

import com.sagi314.gradientscreator.utils.OutParameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * An utility class for files operations.
 */
public final class FilesUtils
{
    private static final int OK = 0;
    private static final int IO = 1;
    private static final int FILE_NO_FOUND = 2;

    private static int privateTryRead(File file, OutParameter<String> outFileContent)
    {
        Validators.requireNotNull(file, "can't read from a null file");
        Validators.requireNotFolder(file, "can't read text from a folder");

        Validators.requireNotNull(outFileContent, "can't return file content when out parameter is null");

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file)))
        {
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                text.append(line).append('\n');
            }

            if (text.length() > 0)
            {
                //deletes the '\n' added after the last line
                text.deleteCharAt(text.length() - 1);
            }

            //passing the text to the user
            outFileContent.set(text.toString());
            return OK;
        }
        catch (FileNotFoundException e)
        {
            return FILE_NO_FOUND;
        }
        catch (IOException e)
        {
            return IO;
        }
    }

    private static boolean privateTryWrite(File file, String content, boolean append, boolean appendLine)
    {
        Validators.requireNotNull(file, "'file' can't be null");
        Validators.requireNotFolder(file, "'file' is not allowed to be a folder");

        Validators.requireNotNull(content, "'content' can't be null");

        var finalContent = appendLine ? content + "\n" : content;

        try (FileWriter writer = new FileWriter(file, append))
        {
            writer.append(finalContent);
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

    public static boolean tryWrite(File file, String content, boolean appendLine)
    {
        return privateTryWrite(file, content, false, appendLine);
    }

    public static boolean tryAppend(File file, String content, boolean appendLine)
    {
        return privateTryWrite(file, content, true, appendLine);
    }

    public static boolean tryDelete(File file)
    {
        Validators.requireNotNull(file, "'file' can't be null");

        return file.delete();
    }

    /**
     * will create a directory and path even if the there is a file named same as {@code folderName}.
     * <p><b>in case it happens the original file will be deleted!!!</b></p>
     * @param parentDirectory the directory to create the new directory in.
     * @param directoryName the name of the new directory.
     * @return the created file or {@code null} if directory not created.
     */
    public static File createDirectoryAndPathEvenIfFileExists(File parentDirectory, String directoryName)
    {
        Validators.requireNotNull(parentDirectory, "'parentDirectory' can't be null");
        Validators.requireNotFile(parentDirectory, "'parentDirectory' can't be a file");

        Validators.illegalNullOrBlank(directoryName, "'directoryName' can't be null or blank");

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

        //now we are trying to create it - as directory
        return file.mkdirs() ? file : null;
    }

    public static File createFileWithTimeStamp(File parentDirectory, String prefix, String suffix)
    {
        //check parentDirectory
        Validators.requireNotNull(parentDirectory, "'parentDirectory' can't be null");
        Validators.requireFolder(parentDirectory, "'parentDirectory' must be a directory but it is a regular file");

        //check and update suffix if needed
        Validators.illegalNull(suffix, "'suffix' can't be null");
        Validators.requireMatches(suffix, "^\\S+$", "'suffix' must be without any whitespaces");

        suffix = suffix.startsWith(".") ? suffix : "." + suffix;
        Validators.illegalBelow(suffix.length(), 4, "'suffix' must be at least 3 characters long without a dot or 4 characters long with a dot");

        //update prefix if needed
        prefix = Validators.ifNullGet(prefix, "");

        return new File(parentDirectory, prefix + System.currentTimeMillis() + suffix);
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, Bitmap.CompressFormat imageFormat, File file)
    {
        Validators.requireNotNull(bitmap, "'bitmap' can't be null");
        Validators.requireNotNull(imageFormat, "'imageFormat' can't be null");
        Validators.requireNotFolder(file, "'file' can't be a directory");

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

    //prevents creating instances
    private FilesUtils() { }
}