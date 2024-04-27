package org.ryml.ryml;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface ILibRyml extends Library {
    int graal_create_isolate(Pointer params, PointerByReference isolate, PointerByReference thread);
    int graal_tear_down_isolate(Pointer thread);
    String load_ys_to_json(Pointer thread, String ryml);
}

