package com.example.damproject.util;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileUpdater extends AsyncTask<User, Void, Boolean> {

    private String fileName;

    public FileUpdater(String fileName) {
        this.fileName = fileName;
    }


    @Override
    protected Boolean doInBackground(User... users) {
        boolean ok;

        try  {
            File file = new File(fileName);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            if (!file.exists()) {
                out.writeObject(users[0]);
                out.close();
            } else {
                ArrayList<User> fileUsers = new ArrayList<>();
                while (in.available() != 0) {
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
