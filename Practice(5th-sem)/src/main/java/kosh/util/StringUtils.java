package kosh.util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StringUtils
{
	private static String lastCashedDir = "";
	
    /**
     * Get folder from clipboard.
     * Works once with new coppied path.
     * @return existing folder from clipboard or null if clipboard data doesn't denote folder
     */
	public static File getFolderFromClipboard()
    {
    	String str = null;
    	try {
			str = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (Exception e)
    	{ return null; }
    	if (str == null)
    		return null;
    	
    	File f = new File(str);
    	if (f.exists() && !f.isFile())
    	{
    		if ( !str.equals(lastCashedDir) )
    		{
    			// ����� ��� ����� ����� �� �������� ��� �����, ������� � ����
    			lastCashedDir = str;
    			return f;
    		}
    	}

    	return null;
    }
	
	/**
     * Cuts the string to the last dot if it is not too far from the end ("asdf.dat" -> "asdf")
     * @param fileName
     * @return modified fileName without extension
     */
	public static String cutExtension (String fileName)
    {
		if (fileName == null)
			return null;
		
		int dotInd = fileName.lastIndexOf(".");
		if ((dotInd > 0) && (fileName.length()-dotInd < 6) )
			fileName = fileName.substring(0, dotInd);

		return fileName;
    }
	
	/**
     * Returns a minimal set of characters that have to be removed from (or added to) the respective
     * strings to make the strings equal.
     */
    public static Pair<String> diff(String a, String b) {
        return diffHelper(a, b, new HashMap<>());
    }

    /**
     * Recursively compute a minimal set of characters while remembering already computed substrings.
     * Runs in O(n^2).
     */
    private static Pair<String> diffHelper(String a, String b, Map<Long, Pair<String>> lookup) {
        long key = ((long) a.length()) << 32 | b.length();
        if (!lookup.containsKey(key)) {
            Pair<String> value;
            if (a.isEmpty() || b.isEmpty()) {
                value = new Pair<>(a, b);
            } else if (a.charAt(0) == b.charAt(0)) {
                value = diffHelper(a.substring(1), b.substring(1), lookup);
            } else {
                Pair<String> aa = diffHelper(a.substring(1), b, lookup);
                Pair<String> bb = diffHelper(a, b.substring(1), lookup);
                if (aa.first.length() + aa.second.length() < bb.first.length() + bb.second.length()) {
                    value = new Pair<>(a.charAt(0) + aa.first, aa.second);
                } else {
                    value = new Pair<>(bb.first, b.charAt(0) + bb.second);
                }
            }
            lookup.put(key, value);
        }
        return lookup.get(key);
    }

    public record Pair<T>(T first, T second) {

        public String toString() {
                return "(" + first + "," + second + ")";
            }
        }
}
