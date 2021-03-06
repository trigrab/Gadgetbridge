/*  Copyright (C) 2017 Andreas Shimokawa

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitcor;

import java.util.HashMap;
import java.util.Map;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareType;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;

public class AmazfitCorFirmwareInfo extends HuamiFirmwareInfo {
    // guessed - at least it is the same accross current versions and different from other devices
    private static final byte[] FW_HEADER = new byte[]{
            (byte) 0x06, (byte) 0x48, (byte) 0x00, (byte) 0x47, (byte) 0xfe, (byte) 0xe7, (byte) 0xfe, (byte) 0xe7,
            (byte) 0xfe, (byte) 0xe7, (byte) 0xfe, (byte) 0xe7, (byte) 0xfe, (byte) 0xe7, (byte) 0xfe, (byte) 0xe7
    };

    //FIXME: this is a moving target :/
    private static final int FW_HEADER_OFFSET = 0x9330;
    private static final int FW_HEADER_OFFSET_2 = 0x9340;
    private static final int FW_HEADER_OFFSET_3 = 0x9288;

    private static final int NEW_RES_HEADER_OFFSET = 0x9;

    private static Map<Integer, String> crcToVersion = new HashMap<>();

    static {
        // firmware
        crcToVersion.put(39948, "1.0.5.60");
        crcToVersion.put(62147, "1.0.5.78");

        // resources
        crcToVersion.put(46341, "RES 1.0.5.60");
        crcToVersion.put(21770, "RES 1.0.5.78");

    }

    public AmazfitCorFirmwareInfo(byte[] bytes) {
        super(bytes);
    }

    @Override
    protected HuamiFirmwareType determineFirmwareType(byte[] bytes) {
        if (ArrayUtils.startsWith(bytes, RES_HEADER)) {
            if (bytes.length < 700000) { // dont know how to distinguish from Bip .res
                return HuamiFirmwareType.INVALID;
            }
            return HuamiFirmwareType.RES;
        } else if (ArrayUtils.equals(bytes, RES_HEADER, NEW_RES_HEADER_OFFSET)) {
            return HuamiFirmwareType.RES_NEW;
        } else if (ArrayUtils.equals(bytes, FW_HEADER, FW_HEADER_OFFSET) || ArrayUtils.equals(bytes, FW_HEADER, FW_HEADER_OFFSET_2) || ArrayUtils.equals(bytes, FW_HEADER, FW_HEADER_OFFSET_3)) {
            // TODO: this is certainly not a correct validation, but it works for now
            return HuamiFirmwareType.FIRMWARE;
        }
        return HuamiFirmwareType.INVALID;
    }

    @Override
    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return isHeaderValid() && device.getType() == DeviceType.AMAZFITCOR;
    }

    @Override
    protected Map<Integer, String> getCrcMap() {
        return crcToVersion;
    }
}
