package net.m3mobile.app.scannerservicehoney;

import android.os.Parcel;
import android.os.Parcelable;

public class SymbolConfig implements Parcelable {

    public int symID;
    public int Mask;
    public int Flags;
    public int MinLength;
    public int MaxLength;

    protected SymbolConfig(Parcel in) {
        symID = in.readInt();
        Mask = in.readInt();
        Flags = in.readInt();
        MinLength = in.readInt();
        MaxLength = in.readInt();
    }

    public SymbolConfig(int symbolID) {
        symID = symbolID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(symID);
        parcel.writeInt(Mask);
        parcel.writeInt(Flags);
        parcel.writeInt(MinLength);
        parcel.writeInt(MaxLength);
    }


    public static final Creator<SymbolConfig> CREATOR = new Creator<SymbolConfig>() {
        @Override
        public SymbolConfig createFromParcel(Parcel in) {
            return new SymbolConfig(in);
        }

        @Override
        public SymbolConfig[] newArray(int size) {
            return new SymbolConfig[size];
        }
    };
}
