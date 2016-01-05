package Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Methods {
	
	public static List<String> parseIt( String str, String delimiter ) {
		
		StringTokenizer stok = new StringTokenizer(str, delimiter );
		ArrayList<String> lst = new ArrayList<String>();
		
		while( stok.hasMoreTokens() )
			lst.add(stok.nextToken());
		
		return lst;
	}
}
