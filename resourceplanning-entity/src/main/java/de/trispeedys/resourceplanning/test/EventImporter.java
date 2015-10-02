package de.trispeedys.resourceplanning.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class EventImporter
{
    public static void main(String[] args)
    {
        try
        {
            importEventFile("import_2015.txt");
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    private static void importEventFile(String importFileName) throws URISyntaxException
    {
        URL resource = EventImporter.class.getClassLoader().getResource(importFileName);
        File file = new File(resource.toURI());

        if (!file.canRead() || !file.isFile()) System.exit(0);

        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new FileReader(file));            
            String row = null;
            while ((row  = in.readLine()) != null)
            {
                row = in.readLine();
                System.out.println(row);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (in != null) try
            {
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}