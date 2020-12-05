package com.app.zluetooth;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.app.zluetooth.Utils.DataPacket;
import com.app.zluetooth.Utils.RigidData;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        String src = "";
        ArrayList<DataPacket> dataPackets = new ArrayList<>();
        int start_index = 0;
        while(src.length() - start_index > RigidData.number_of_letter_each_packet){
            DataPacket dataPacket = new DataPacket();
            dataPacket.setData(src.substring(start_index, RigidData.number_of_letter_each_packet));
            start_index += RigidData.number_of_letter_each_packet;
        }

        start_index -= RigidData.number_of_letter_each_packet;
        if(start_index != src.length()) {
            DataPacket dataPacket = new DataPacket();
            dataPacket.setData(src.substring(start_index, src.length() - start_index));
        }

        for(int i = 0; i < dataPackets.size(); i++) {
            System.out.println(dataPackets.get(i).getData());
        }
    }
}
