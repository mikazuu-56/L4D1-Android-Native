package com.l4d1.android;

import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;

public class L4D2Detector {

    public static class Result {
        public boolean executableFound;
        public boolean gameFolderFound;
        public boolean binFolderFound;

        public boolean isValid() {
            return executableFound && gameFolderFound;
        }

        public String getStatus() {
            return "L4D2 FILE CHECK\n"
                + "left4dead2.exe: " + executableFound + "\n"
                + "left4dead2/: " + gameFolderFound + "\n"
                + "bin/: " + binFolderFound + "\n"
                + "Ready: " + isValid();
        }
    }

    public static Result check(Context context, Uri treeUri) {
        Result result = new Result();

        if (treeUri == null) {
            return result;
        }

        String treeId =
            DocumentsContract.getTreeDocumentId(treeUri);

        Uri documentUri =
            DocumentsContract.buildDocumentUriUsingTree(
                treeUri,
                treeId
            );

        Uri childrenUri =
            DocumentsContract.buildChildDocumentsUriUsingTree(
                treeUri,
                treeId
            );

        android.database.Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(
                childrenUri,
                new String[] {
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                    DocumentsContract.Document.COLUMN_MIME_TYPE
                },
                null,
                null,
                null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    String mime = cursor.getString(1);

                    boolean directory =
                        DocumentsContract.Document.MIME_TYPE_DIR.equals(mime);

                    if ("left4dead2.exe".equalsIgnoreCase(name)
                            && !directory) {
                        result.executableFound = true;
                    }

                    if ("left4dead2".equalsIgnoreCase(name)
                            && directory) {
                        result.gameFolderFound = true;
                    }

                    if ("bin".equalsIgnoreCase(name)
                            && directory) {
                        result.binFolderFound = true;
                    }
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }
}
