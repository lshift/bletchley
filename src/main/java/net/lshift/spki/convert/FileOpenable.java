package net.lshift.spki.convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOpenable implements Openable
{
    private final File file;

    public FileOpenable(File file)
    {
        super();
        this.file = file;
    }

    public File getFile()
    {
        return file;
    }

    @Override
    public FileInputStream read() throws IOException {
         return new FileInputStream(file);
    }

    @Override
    public FileOutputStream write() throws IOException {
        return new FileOutputStream(file);
    }
}