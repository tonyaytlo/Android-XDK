package com.layer.xdk.ui.message.model;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Allows ordering of strings into certain priority slots for metadata display.
 *
 * @param <T> Type of metadata used when computing the slots
 */
public abstract class MetadataSlots<T> {

    private ArrayList<String> mOrderedMetadata = new ArrayList<>(0);
    private String mSlotB;
    private String mSlotC;
    private String mSlotD;
    private Context mAppContext;
    private boolean mUsingDefaultSlotB;

    public MetadataSlots(Context appContext) {
        mAppContext = appContext;
    }

    /**
     * @return the full ordered list of metadata strings
     */
    public ArrayList<String> getOrderedMetadata() {
        return mOrderedMetadata;
    }

    /**
     * @return the metadata string that should go in slot B
     */
    public String getSlotB() {
        return mSlotB;
    }

    /**
     * @return the metadata string that should go in slot C
     */
    public String getSlotC() {
        return mSlotC;
    }

    /**
     * @return the metadata string that should go in slot D
     */
    public String getSlotD() {
        return mSlotD;
    }

    /**
     * Determine what data should go in the slots based on available metadata. Top elements from
     * slot C and D may be promoted and other elements may be demoted depending on the available
     * data.
     *
     * @param metadata metadata used when computing the slots
     */
    public void compute(@NonNull T metadata) {
        mOrderedMetadata.clear();
        Queue<String> slotB = createSlotBQueue(metadata, mOrderedMetadata);
        Queue<String> slotC = createSlotCQueue(metadata, mOrderedMetadata);
        Queue<String> slotD = createSlotDQueue(metadata, mOrderedMetadata);

        mSlotB = slotB.isEmpty() ? null : slotB.remove();

        mSlotC = slotC.isEmpty() ? null : slotC.remove();
        if (mSlotC == null) {
            // Promotion attempt
            mSlotC = slotD.isEmpty() ? null : slotD.remove();
        }
        if (mSlotC == null) {
            // Demotion attempt
            mSlotC = slotB.isEmpty() ? null : slotB.remove();
        }

        mSlotD = slotD.isEmpty() ? null : slotD.remove();
        if (mSlotD == null) {
            // Demotion attempt
            mSlotD = slotC.isEmpty() ? null : slotC.remove();
        }
        if (mSlotD == null) {
            // Demotion attempt
            mSlotD = slotB.isEmpty() ? null : slotB.remove();
        }
    }

    /**
     * @return application context
     */
    protected Context getAppContext() {
        return mAppContext;
    }

    /**
     * This is called by {@link #compute(Object)} to determine the priority order for slot B.
     *
     * @param metadata raw metadata used to process this slot
     * @param overallOrder the overall order of strings. Add values here if
     * {@link #getOrderedMetadata()} is to be used
     * @return a queue of values representing the priority for this slot
     */
    @NonNull
    protected abstract Queue<String> createSlotBQueue(@NonNull T metadata, @NonNull List<String> overallOrder);

    /**
     * This is called by {@link #compute(Object)} to determine the priority order for slot C.
     *
     * @param metadata raw metadata used to process this slot
     * @param overallOrder the overall order of strings. Add values here if
     * {@link #getOrderedMetadata()} is to be used
     * @return a queue of values representing the priority for this slot
     */
    @NonNull
    protected abstract Queue<String> createSlotCQueue(@NonNull T metadata, @NonNull List<String> overallOrder);

    /**
     * This is called by {@link #compute(Object)} to determine the priority order for slot D.
     *
     * @param metadata raw metadata used to process this slot
     * @param overallOrder the overall order of strings. Add values here if
     * {@link #getOrderedMetadata()} is to be used
     * @return a queue of values representing the priority for this slot
     */
    @NonNull
    protected abstract Queue<String> createSlotDQueue(@NonNull T metadata, @NonNull List<String> overallOrder);

    /**
     * Call when creating slot B's queue if a default value is used. This is essential to ensure
     * correctness when calling {@link #hasOnlyDefaultData()}.
     */
    protected void setUsingDefaultSlotB() {
        mUsingDefaultSlotB = true;
    }

    /**
     * @return true if the only value that exists is data provided by default by the subclasses,
     * false otherwise
     */
    public boolean hasOnlyDefaultData() {
        return mOrderedMetadata.size() == 1 && mUsingDefaultSlotB;
    }
}
