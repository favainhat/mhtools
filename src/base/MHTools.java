/*  MHTools - MH Utilities
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

package base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import crypt.Decrypter;
import crypt.Encrypter;
import crypt.KirkCypher;
import crypt.QuestCypher;
import crypt.SavedataCypher;
import dec.ExtractPluginA;
import dec.ExtractPluginB;
import dec.ExtractPluginC;
import dec.ExtractPluginD;
import dec.ExtractPluginE;
import dec.ExtractPluginF;
import enc.RebuildPluginA;
import enc.RebuildPluginB;
import enc.RebuildPluginC;
import enc.RebuildPluginD;
import enc.RebuildPluginE;
import enc.RebuildPluginF;

public class MHTools {

    public static void extract(String filename, String decoder) {
        // (00[1-2][0-9]|47[0-9][0-9])\\..* decoder A
        // 53[0-9][0-9]\\..* decoder B
        // 54[0-9][0-9]\\..* decoder C

        Decoder dec = null;
        int type = Integer.parseInt(decoder);
        switch (type) {
        case 1:
            dec = new ExtractPluginA();
            break;
        case 2:
            dec = new ExtractPluginB(0);
            break;
        case 4:
            dec = new ExtractPluginB(1);
            break;
        case 7:
            dec = new ExtractPluginB(2);
            break;
        case 3:
            dec = new ExtractPluginC();
            break;
        case 5:
            dec = new ExtractPluginD();
            break;
        case 6:
            dec = new ExtractPluginE();
            break;
        case 9:
            dec = new ExtractPluginF(0);
            break;
        default:
            System.err.println("Unknown decoder: " + decoder);
            System.exit(1);
        }
        dec.extract(filename);
    }

    public static void rebuild(String filename, String encoder) {
        Encoder enc = null;
        int type = Integer.parseInt(encoder);
        if(type < 5) {
            String str = checkFile(filename + "/filelist.txt");
            if (str == null) {
                System.exit(1);
            }
        }
        switch (type) {
        case 1:
            enc = new RebuildPluginA();
            break;
        case 2:
            enc = new RebuildPluginB(0);
            break;
        case 4:
        case 7:
            enc = new RebuildPluginB(type);
            break;
        case 3:
            enc = new RebuildPluginC();
            break;
        case 5:
            enc = new RebuildPluginD();
            break;
        case 6:
            enc = new RebuildPluginE();
            break;
        case 9:
            enc = new RebuildPluginF(0);
            break;
        default:
            System.err.println("Unknown encoder: " + encoder);
            System.exit(1);
        }
        enc.compile(filename);
    }
    
    public static void createPatch(String[] args) {
        
    }

    public static void main(String[] args) {
        System.out.println("mhtools v2.0 - MHP2G/MHFU/MHP3 utils");
        System.out.println();
        if (args.length < 2) {
            System.err.println("Usage: java -jar mhtools.jar --extract <path to xxxx.bin> <decoder number>");
            System.err.println("       java -jar mhtools.jar --rebuild <path to project folder> <encoder number>");
            System.err.println("       java -jar mhtools.jar --decrypt <path to xxxx.bin>");
            System.err.println("       java -jar mhtools.jar --encrypt <path to xxxx.bin>");
            System.err.println("       java -jar mhtools.jar --dec-ext <path to xxxx.bin> <decoder number>");
            System.err.println("       java -jar mhtools.jar --reb-enc <path to project folder> <encoder number>");
            System.err.println("       java -jar mhtools.jar --gen-index <data.bin>");
            System.err.println("       java -jar mhtools.jar --dec-all <data.bin> <path to output folder>");
            System.err.println("       java -jar mhtools.jar --create-patch <xxxx.bin.enc> [ ... <xxxx.bin.enc>] <output_file>");
            System.err.println("       java -jar mhtools.jar --decrypt-quest <mxxxxx.mib>");
            System.err.println("       java -jar mhtools.jar --encrypt-quest <mxxxxx.mib>");
            System.err.println("       java -jar mhtools.jar --extract-quests <xxxxxx.bin>");
            System.err.println("       java -jar mhtools.jar --update-sha1 <mxxxxx.mib>");
            System.err.println("       java -jar mhtools.jar --decrypt-save <xxxxx.bin>");
            System.err.println("       java -jar mhtools.jar --encrypt-save <xxxxx.bin>");
            System.err.println("       java -jar mhtools.jar --decrypt-kirk <xxxxx.bin>");
            System.err.println("       java -jar mhtools.jar --encrypt-kirk <xxxxx.bin>");
            System.exit(1);
        } else {
            if (args[0].equals("--extract")) {
                if (args.length < 3) {
                    System.err.println("Decoder number missing. Aborting");
                    System.exit(1);
                }
                extract(args[1], args[2]);
            } else if (args[0].equals("--rebuild")) {
                if (args.length < 3) {
                    System.err.println("Decoder number missing. Aborting");
                    System.exit(1);
                }
                rebuild(args[1], args[2]);
            } else if (args[0].equals("--decrypt")) {
                new Decrypter().decrypt(args[1], args[1] + ".dec");
            } else if (args[0].equals("--encrypt")) {
                String filename = new File(args[1]).getName();
                new Encrypter().encrypt(args[1], filename + ".enc");
            } else if (args[0].equals("--dec-ext")) {
                if (args.length < 3) {
                    System.err.println("Decoder number missing. Aborting");
                    System.exit(1);
                }
                new Decrypter().decrypt(args[1], args[1] + ".dec");
                new File(args[1]).renameTo(new File(args[1] + ".tmp"));
                new File(args[1] + ".dec").renameTo(new File(args[1]));
                extract(args[1], args[2]);
                new File(args[1]).delete();
                new File(args[1] + ".tmp").renameTo(new File(args[1]));
            } else if (args[0].equals("--reb-enc")) {
                if (args.length < 3) {
                    System.err.println("Decoder number missing. Aborting");
                    System.exit(1);
                }
                rebuild(args[1], args[2]);
                String filename = new File(args[1]).getName();
                new Encrypter().encrypt(filename + ".bin.out", filename
                        + ".bin.enc");
                System.out.println("Moving to " + filename + ".bin.enc");
                new File(filename + ".bin.out").delete();
            } else if (args[0].equals("--gen-index")) {
                new Decrypter().decrypt_index(args[1], null);
            } else if (args[0].equals("--dec-all")) {
                if (args.length < 3) {
                    System.err.println("Output folder missing. Aborting");
                    System.exit(1);
                }
                new Decrypter().decrypt_whole(args[1], args[2]);
            } else if(args[0].equals("--create-patch")) {
                new PatchBuilder().create(args);
            } else if(args[0].equals("--encrypt-quest")) {
                new QuestCypher().encrypt(args[1]);
            } else if(args[0].equals("--decrypt-quest")) {
                new QuestCypher().decrypt(args[1]);
            } else if(args[0].equals("--extract-quests")) {
                new QuestCypher().extract(args[1]);
            } else if(args[0].equals("--update-sha1")) {
                new QuestCypher().update_sha1(args[1]);
            } else if(args[0].equals("--encrypt-save")) {
                new SavedataCypher().encrypt(args[1]);
            } else if(args[0].equals("--decrypt-save")) {
                new SavedataCypher().decrypt(args[1]);
            } else if(args[0].equals("--encrypt-kirk")) {
                new KirkCypher().encrypt(args[1]);
            } else if(args[0].equals("--decrypt-kirk")) {
                new KirkCypher().decrypt(args[1]);
            } else {
                System.err.println("Unknown parameter: " + args[0]);
                System.exit(1);
            }
        }
    }

    public static String checkFile(String filename) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(filename));
            String name = file.readLine().split(" ")[0];
            file.close();
            return name;
        } catch (FileNotFoundException e) {
            System.err.println(e.toString());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
