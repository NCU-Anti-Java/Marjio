package io.github.antijava.marjio.network;

import java.io.IOException;
import com.bluelinelabs.logansquare.LoganSquare;
import io.github.antijava.marjio.common.input.Event;
import io.github.antijava.marjio.common.input.Status;

/**
 * Created by Date on 2015/12/28.
 */
public class Packer {

    // 因為這部分還沒完成，若直接設置成 static 方法會導致測試無法通過，所以先去掉 static 修飾詞
    public byte[] pack(Status status) throws IOException {
        return LoganSquare.serialize(status).getBytes();
    }

    // 因為這部分還沒完成，若直接設置成 static 方法會導致測試無法通過，所以先去掉 static 修飾詞
    public Status unpack(byte[] bytes) throws IOException {

        return LoganSquare.parse(new String(bytes), Status.class);
    }
}
