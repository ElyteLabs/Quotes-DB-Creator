package com.elytelabs.quotescreator.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

class DatabaseHandler {

    public static final String backup = "-backup"; //value to be appended to file name when renaming (pseudo delete)
    public static final int OUCH = -666666666;
    static final String[] tempFiles = new String[]{"-journal", "-wal", "-shm"}; // temporary files to rename

    /**
     * Check if the database already exists. NOTE will create the databases folder is it doesn't exist
     *
     * @return true if it exists, false if it doesn't
     */
    public static boolean checkDataBase(Context context, String dbName) {

        File db = new File(context.getDatabasePath(dbName).getPath()); //Get the file name of the database
        if (db.exists()) return true; // If it exists then return doing nothing

        // Get the parent (directory in which the database file would be)
        File dbdir = db.getParentFile();
        // If the directory does not exits then make the directory (and higher level directories)
        if (!dbdir.exists()) {
            db.getParentFile().mkdirs();
            dbdir.mkdirs();
        }
        return false;
    }

    /**
     * Copy database file from the assets folder
     * (long version caters for asset file name being different to the database name)
     *
     * @param context          Context is needed to get the applicable package
     * @param dbName           name of the database file
     * @param assetFileName    name of the asset file
     * @param deleteExistingDB true if an existing database file should be deleted
     *                         note will delete journal and wal files
     *                         note doesn't actually delete the files rater it renames
     *                         the files by appended -backup to the file name
     *                         SEE/USE clearForceBackups below to delete the renamed files
     */
    public static void copyDataBase(Context context, String dbName, String assetFileName, boolean deleteExistingDB, int version) {

        checkpointIfWALEnabled(context, dbName);
        int stage = 0, buffer_size = 4096, blocks_copied = 0, bytes_copied = 0;
        File f = new File(context.getDatabasePath(dbName).toString());
        InputStream is;
        OutputStream os;

        /**
         * If forcing then effectively delete (rename) current database files
         */
        if (deleteExistingDB) {
            f.renameTo(context.getDatabasePath(dbName + backup));
            for (String s : tempFiles) {
                File tmpf = new File(context.getDatabasePath(dbName + s).toString());
                if (tmpf.exists()) {
                    tmpf.renameTo(context.getDatabasePath(dbName + s + backup));
                }
            }
        }

        //Open your local db as the input stream
        try {
            is = context.getAssets().open(assetFileName); // Open the Asset file
            stage++;

            os = new FileOutputStream(f);
            stage++;
            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[buffer_size];
            int length;
            while ((length = is.read(buffer)) > 0) {
                blocks_copied++;
                os.write(buffer, 0, length);
                bytes_copied += length;
            }
            stage++;
            //Close the streams
            os.flush();
            stage++;
            os.close();
            stage++;
            is.close();
            if (version > 0) {
                setVersion(context, dbName, version);
            }
        } catch (IOException e) {
            String exception_message = "";
            e.printStackTrace();
            switch (stage) {
                case 0:
                    exception_message = "Error trying to open the asset " + dbName;
                    break;
                case 1:
                    exception_message = "Error opening Database file for output, path is " + f.getPath();
                    break;
                case 2:
                    exception_message = "Error flushing written database file " + f.getPath();
                    break;
                case 3:
                    exception_message = "Error closing written database file " + f.getPath();
                    break;
                case 4:
                    exception_message = "Error closing asset file " + f.getPath();

            }
            throw new RuntimeException("Unable to copy the database from the asset folder." + exception_message + " see starck-trace above.");
        }
    }

    /**
     * Copy the databsse from the assets folder where asset name and dbName are the same
     *
     * @param context
     * @param dbName
     * @param deleteExistingDB
     */
    public static void copyDataBase(Context context, String dbName, boolean deleteExistingDB, int version) {
        copyDataBase(context, dbName, dbName, deleteExistingDB, version);
    }

    /**
     * Get the SQLite_user_version from the DB in the asset folder
     *
     * @param context       needed to get the appropriate package assets
     * @param assetfilename the name of the asset file (assumes/requires name matches database)
     * @return the version number as stored in the asset DB
     */
    public static int getVersionFromDBInAssetFolder(Context context, String assetfilename) {
        InputStream is;
        try {
            is = context.getAssets().open(assetfilename);
        } catch (IOException e) {
            return OUCH;
        }
        return getDBVersionFromInputStream(is);
    }

    /**
     * Get the version from the database itself without opening the database as an SQliteDatabase
     *
     * @param context Needed to ascertain package
     * @param dbName  the name of the dataabase
     * @return the version number extracted
     */

    public static int getVersionFromDBFile(Context context, String dbName) {
        InputStream is;
        try {
            is = new FileInputStream(new File(context.getDatabasePath(dbName).toString()));
        } catch (IOException e) {
            return OUCH;
        }
        return getDBVersionFromInputStream(is);
    }

    /**
     * Get the Database Version (user_version) from an inputstream
     * Note the inputstream is closed
     *
     * @param is The Inputstream
     * @return The extracted version number
     */
    private static int getDBVersionFromInputStream(InputStream is) {
        int rv = -1, dbversion_offset = 60, dbversion_length = 4;
        byte[] dbfileheader = new byte[64];
        byte[] dbversion = new byte[4];
        try {
            is.read(dbfileheader);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return rv;
        }

        for (int i = 0; i < dbversion_length; i++) {
            dbversion[i] = dbfileheader[dbversion_offset + i];
        }
        return ByteBuffer.wrap(dbversion).getInt();
    }

    /**
     * Check to see if the asset file exists
     *
     * @param context       needed to get the appropriate package
     * @param assetFileName the name of the asset file to check
     * @return true if the asset file exists, else false
     */

    public static boolean ifAssetFileExists(Context context, String assetFileName) {
        try {
            context.getAssets().open(assetFileName);
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    /**
     * Delete the backup
     *
     * @param context
     * @param dbName
     */

    public static void clearForceBackups(Context context, String dbName) {
        String[] fulllist = new String[tempFiles.length + 1];

        for (int i = 0; i < tempFiles.length; i++) {
            fulllist[i] = tempFiles[i];
        }
        fulllist[tempFiles.length] = ""; // Add "" so database file backup is also deleted
        for (String s : fulllist) {
            File tmpf = new File(context.getDatabasePath(dbName + s + backup).toString());
            if (tmpf.exists()) {
                tmpf.delete();
            }
        }
    }

    /**
     * @param context The context so that the respective package is used
     * @param dbName  The name of the database (the old will have -backup appended)
     */
//
//    public static void restoreTable(Context context, String dbName, String table) {
//        ContentValues cv = new ContentValues();
//        SQLiteDatabase dbNew = SQLiteDatabase.openDatabase(context.getDatabasePath(dbName).toString(), null,SQLiteDatabase.OPEN_READWRITE);
//        SQLiteDatabase dbOld = SQLiteDatabase.openDatabase(context.getDatabasePath(dbName + backup).toString(),null,SQLiteDatabase.OPEN_READONLY);
//        Cursor csr = dbOld.query(table,null,null,null,null,null,null);
//        dbNew.beginTransaction();
//        while (csr.moveToNext()) {
//            cv.clear();
//            int offset = 0;
//            for (String column: csr.getColumnNames()) {
//                switch (csr.getType(offset++)){
//                    case Cursor.FIELD_TYPE_NULL:
//                        break;
//                    case Cursor.FIELD_TYPE_INTEGER:
//                        cv.put(column,csr.getLong(csr.getColumnIndex(column)));
//                        break;
//                    case Cursor.FIELD_TYPE_FLOAT:
//                        cv.put(column,csr.getFloat(csr.getColumnIndex(column)));
//                        break;
//                    case Cursor.FIELD_TYPE_STRING:
//                        cv.put(column,csr.getString(csr.getColumnIndex(column)));
//                        break;
//                    case Cursor.FIELD_TYPE_BLOB:
//                        cv.put(column,csr.getBlob(csr.getColumnIndex(column)));
//                }
//            }
//            dbNew.insert(Constants.TABLE_BOOKMARK,null,cv);
//        }
//        dbNew.setTransactionSuccessful();
//        dbNew.endTransaction();
//        csr.close();
//        dbNew.close();
//        dbOld.close();
//    }
    private static void checkpointIfWALEnabled(Context context, String dbName) {
        Cursor csr;
        int wal_busy = -99, wal_log = -99, wal_checkpointed = -99;
        if (!new File(context.getDatabasePath(dbName).getPath()).exists()) {
            return;
        }
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(dbName).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        csr = db.rawQuery("PRAGMA journal_mode", null);
        if (csr.moveToFirst()) {
            String mode = csr.getString(0);
            if (mode.toLowerCase().equals("wal")) {
                csr = db.rawQuery("PRAGMA wal_checkpoint", null);
                if (csr.moveToFirst()) {
                    wal_busy = csr.getInt(0);
                    wal_log = csr.getInt(1);
                    wal_checkpointed = csr.getInt(2);
                }
                csr = db.rawQuery("PRAGMA wal_checkpoint(TRUNCATE)", null);
                csr.getCount();
                csr = db.rawQuery("PRAGMA wal_checkpoint", null);
                if (csr.moveToFirst()) {
                    wal_busy = csr.getInt(0);
                    wal_log = csr.getInt(1);
                    wal_checkpointed = csr.getInt(2);
                }
            }
        }
        csr.close();
        db.close();
    }

    private static void setVersion(Context context, String dbName, int version) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(dbName).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        db.setVersion(version);
        db.close();

    }
}