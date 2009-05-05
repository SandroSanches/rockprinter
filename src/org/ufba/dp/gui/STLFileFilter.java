package org.ufba.dp.gui;

import java.io.File;
import java.io.FileFilter;

public class STLFileFilter extends javax.swing.filechooser.FileFilter {

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		
		String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
		
		if (ext!=null && ext.equals("stl")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public String getDescription() {
		return "STL Files (.stl)";
	}

}
