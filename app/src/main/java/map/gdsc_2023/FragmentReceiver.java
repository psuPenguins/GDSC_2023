package map.gdsc_2023;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.GoogleMap;

// For transmitting map buttons to fragments
public interface FragmentReceiver {
    public Buttons getButtons();
    public GoogleMap getMap();
    public FragmentManager getFM();
}
