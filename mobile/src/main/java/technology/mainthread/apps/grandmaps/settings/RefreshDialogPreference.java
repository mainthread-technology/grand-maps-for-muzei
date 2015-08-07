package technology.mainthread.apps.grandmaps.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import technology.mainthread.apps.grandmaps.R;

public class RefreshDialogPreference extends DialogPreference {

    private NumberPicker picker;
    private int value;
    private boolean valueSet;

    public RefreshDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.refresh_dialog);
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();
        picker = (NumberPicker) view.findViewById(R.id.picker_hours);
        picker.setMinValue(1);
        picker.setMaxValue(24);
        picker.setValue(value);
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            int value = picker.getValue();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        final boolean changed = this.value != value;
        if (changed || !valueSet) {
            this.value = value;
            valueSet = true;
            persistInt(value);
            if (changed) {
                notifyChanged();
            }
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(value) : (Integer) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 8);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.value = getValue();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setValue(myState.value);
    }

    private static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        int value;

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }
    }
}
