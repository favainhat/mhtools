/*  MHTools - MHP2G 53xx.bin language table extractor
    Copyright (C) 2008-2011 Codestation

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.Vector;

import base.Decoder;
import base.HelperDec;

/**
 * ExtractPluginF v1.0 - 53xx.bin language table extractor
 * 
 * @author Codestation
 */
public class ExtractPluginF extends HelperDec implements Decoder {

    private int mhp3_skip_bytes;
    private int mhp3_seek_skip;

    public ExtractPluginF(int newdec) {
    	mhp3_seek_skip = 0;
    	switch(newdec) {
    	case 2:
    		mhp3_seek_skip = 32;
    	case 1:
    		mhp3_skip_bytes = 4;
    		break;
    	default:
    		mhp3_skip_bytes = 0;
    	}
    }

    @Override
    public void extract(String filename) {
        byte[] unknownData;
        Vector<Integer> table_offset;
        try {
            RandomAccessFile file = new RandomAccessFile(filename,"r");
            table_offset = new Vector<Integer>();
            table_offset.add(readInt(file));
            table_offset.add(0x8);
            filename = new File(filename).getName();
            String directory = filename.split("\\.")[0];
            new File(directory).mkdir();
            PrintStream filelist = new PrintStream(new FileOutputStream(
                    new File(directory + "/filelist.txt")), true, "UTF-8");
            filelist.println(filename + " " + file.length());
            //for (int j = 0; j < table_offset.size(); j++) {
            for (int j = 0; j < 1; j++) { //??
                file.seek(table_offset.get(j) + mhp3_seek_skip);
                System.out.println("Creating " + directory + "/string_table_"
                        + j + ".txt");
                PrintStream stringout = new PrintStream(new FileOutputStream(
                        new File(directory + "/string_table_" + j + ".txt")),
                        true, "UTF-8");
                filelist.println("string_table_" + j + ".txt");
                file.skipBytes(24);
                int offset_table_pointer = readInt(file);
                //System.out.println("offset_table_pointer:"+String.format("0x%08X", offset_table_pointer));
                file.seek(offset_table_pointer + mhp3_seek_skip);
                int string_table_pointers = readInt(file);
                for (long i = string_table_pointers; i < offset_table_pointer; i += 4) {
                    file.seek(i + mhp3_seek_skip);
                    int current_string = readInt(file);
                    file.seek(current_string + mhp3_seek_skip);
                    String str = readString(file);
                    if (str.length() == 1 && str.charAt(0) == 0) {
                        // some offsets points to empty strings, so i put this
                        // string to make
                        // sure that it will created at the moment of re-pack
                        stringout.println("<EMPTY STRING>");
                    } else {
                        str = str.substring(0, str.length() - 1);
                        // need one string per line, so better replace the
                        // newlines
                        stringout.println(str.replaceAll("\n", "<NEWLINE>"));
                    }
                }
                stringout.close();
                file.seek((offset_table_pointer + 7 * 4) + mhp3_seek_skip);
            }
            //for (int j = 0; j < table_offset.size(); j++) {
            ///*
            for (int j = 1; j < 2; j++) { //??
                file.seek(table_offset.get(j) + mhp3_seek_skip);
                System.out.println("Creating " + directory + "/string_table_"
                        + j + ".txt");
                PrintStream stringout = new PrintStream(new FileOutputStream(
                        new File(directory + "/string_table_" + j + ".txt")),
                        true, "UTF-8");
                filelist.println("string_table_" + j + ".txt");
                //file.skipBytes(24);
                int offset_table_pointer = readInt(file);
                //System.out.println("offset_table_pointer:"+String.format("0x%08X", offset_table_pointer));
                file.seek(offset_table_pointer + mhp3_seek_skip-4);
                int string_table_pointers = readInt(file);
                for (long i = string_table_pointers;; i += 4) {
                    file.seek(i + mhp3_seek_skip);
                    int current_string = readInt(file);
                    if(current_string == 0)
                        break;
                    file.seek(current_string + mhp3_seek_skip);
                    String str = readString(file);
                    if (str.length() == 1 && str.charAt(0) == 0) {
                        // some offsets points to empty strings, so i put this
                        // string to make
                        // sure that it will created at the moment of re-pack
                        stringout.println("<EMPTY STRING>");
                    } else {
                        str = str.substring(0, str.length() - 1);
                        // need one string per line, so better replace the
                        // newlines
                        stringout.println(str.replaceAll("\n", "<NEWLINE>"));
                    }
                }
                stringout.close();
                file.seek((offset_table_pointer + 7 * 4) + mhp3_seek_skip);
            }
            //*/
            file.close();
            filelist.close();
            System.out.println("Copying " + filename + " to " + directory + "/"
                    + filename + " (needed for rebuild)");
            copyfile(filename, directory + "/" + filename);
            System.out.println("Finished!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyfile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException ex) {
            System.out
                    .println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
