package de.trispeedys.resourceplanning.util;

import java.text.MessageFormat;

public class HtmlGenerator
{
    private StringBuffer buffer;

    public HtmlGenerator()
    {
        super();
        this.buffer = new StringBuffer();
    }

    /**
     * referred to images must be placed in the /src/main/webapp/img directory.
     * 
     * @param filename
     * @param suffix
     * @param width 
     * @param height 
     * 
     * @return
     */
    public HtmlGenerator withImage(String filename, String suffix, int width, int height)
    {
        MessageFormat mf = new MessageFormat("<img src=\"img/{0}.{1}\" width=\"{2}\" height=\"{3}\" align=\"middle\">");
        buffer.append(mf.format(new Object[]
        {
                filename, suffix, width, height
        }));
        newLine();
        return this;
    }

    public HtmlGenerator withHeader(String text)
    {
        buffer.append("<h1>" + text + "</h1>");
        newLine();
        return this;
    }

    public HtmlGenerator withLinebreak()
    {
        return withLinebreak(1);
    }

    public HtmlGenerator withLinebreak(int howMany)
    {
        for (int i = 0; i < howMany; i++)
        {
            buffer.append("<br>");
            newLine();
        }
        return this;
    }

    public HtmlGenerator withParagraph(String text)
    {
        buffer.append("<p>" + text + "</p>");
        newLine();
        return this;
    }
    
    public HtmlGenerator withClosingLink()
    {
        buffer.append("<a href=\"javascript:close_window();\">Schliessen</a>");
        newLine();
        return this;
    }
    
    public HtmlGenerator withLink(String link, String displayText)
    {
        MessageFormat mf = new MessageFormat("<a href=\"{0}\">{1}</a>");
        buffer.append(mf.format(new Object[]
        {
                link, displayText
        }));
        newLine();
        return this;
    }

    private void newLine()
    {
        buffer.append("\n");
    }

    public String render()
    {
        return buffer.toString();
    }
}