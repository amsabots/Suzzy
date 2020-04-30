package com.example.suzzy.InterfaceMethods;

import com.google.firebase.database.DataSnapshot;

public interface IfireBase {
    void onLoadSucess(DataSnapshot snapshot);
    void onLoadFailure(String Message);

}
