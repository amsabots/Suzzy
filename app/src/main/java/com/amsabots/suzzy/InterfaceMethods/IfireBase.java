package com.amsabots.suzzy.InterfaceMethods;

import com.google.firebase.database.DataSnapshot;

public interface IfireBase {
    void onLoadSucess(DataSnapshot snapshot);
    void onLoadFailure(String Message);

}
