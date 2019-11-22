package com.example.damproject.util;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileUpdater extends AsyncTask<User, Void, Boolean> {

    private String fileName;
    private Context context;

    public FileUpdater(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }


    @Override
    protected Boolean doInBackground(User... users) {
        boolean ok;
        boolean remove = false;

        if (users.length == 2) {
            remove = true;
        }

        try  {
            File file = new File(context.getFilesDir(), fileName);
            ObjectOutputStream out = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            ObjectInputStream in = new ObjectInputStream(context.openFileInput(fileName));
            if (!file.exists()) {
                out.writeObject(users[0]);
                out.close();
            } else {
                ArrayList<User> fileUsers = new ArrayList<>();
                while (in.available() != 0) {
                    if (remove) {
                        User user = (User)in.readObject();
                        if (user.getUsername().equals(users[1].getUsername()))
                            continue;
                    }
                    fileUsers.add((User)in.readObject());
                }
                fileUsers.add(users[0]);

                for (User u : fileUsers) {
                    out.writeObject(u);
                }

                out.close();
                in.close();
            }

            ok = true;
        } catch (Exception e) {
            ok = false;
            e.printStackTrace();
        }

        return ok;
    }
}
