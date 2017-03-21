package com.github.thomas_p.popularmoviesapp.utils;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

abstract public class MovieListMode {
    public enum LIST_MODES {POPULAR, TOP_RATED, FAVORITES}

    ;
    private Context context;
    private final static String STORAGE_KEY = "MovieListKeyMode";

    private LIST_MODES listMode;

    private MenuItem mActionPopular;
    private MenuItem mActionTopRated;
    private MenuItem mActionFavorites;


    public MovieListMode(Context context) {
        this.context = context;

    }

    /**
     * store the list mode in a bundle
     *
     * @param outState
     */
    public void storeListMode(Bundle outState) {
        final byte listModeValue;
        switch (listMode) {
            case TOP_RATED:
                listModeValue = 1;
                break;
            case FAVORITES:
                listModeValue = 2;
                break;
            case POPULAR:
            default:
                listModeValue = 0;
                break;
        }
        outState.putByte(STORAGE_KEY, listModeValue);
    }


    /**
     * load the list mode from an instance state
     *
     * @param savedInstanceState
     */
    public void loadListMode(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(STORAGE_KEY)) {
            byte mode = savedInstanceState.getByte(STORAGE_KEY);
            switch (mode) {
                case 1:
                    changeListMode(LIST_MODES.TOP_RATED, false);
                    break;
                case 2:
                    changeListMode(LIST_MODES.FAVORITES, false);
                    break;
                default:
                    changeListMode(LIST_MODES.POPULAR, false);
            }
        } else {
            changeListMode(LIST_MODES.POPULAR, false);
        }
    }

    /**
     * set the menu buttons
     *
     * @param mActionPopular
     * @param mActionTopRated
     * @param mActionFavorites
     */
    public void setActionMenus(MenuItem mActionPopular, MenuItem mActionTopRated, MenuItem mActionFavorites) {
        this.mActionPopular = mActionPopular;
        this.mActionTopRated = mActionTopRated;
        this.mActionFavorites = mActionFavorites;
        setListModeState();
    }

    /**
     * set the actual list mode state in the menu item
     */
    private void setListModeState() {
        if (mActionFavorites != null
                && mActionTopRated != null
                && mActionPopular != null) {
            switch (listMode) {
                case FAVORITES:
                    mActionFavorites.setChecked(true);
                    break;
                case POPULAR:
                    mActionPopular.setChecked(true);
                    break;
                default:
                    mActionTopRated.setChecked(true);
                    break;
            }
        }
    }

    /**
     * change the actual list mode
     *
     * @param listMode
     */
    public void changeListMode(LIST_MODES listMode) {
        changeListMode(listMode, true);
    }

    private void changeListMode(LIST_MODES listMode, boolean callListener) {
        if (listMode == this.listMode) {
            return;
        }
        this.listMode = listMode;
        setListModeState();
        if (callListener) {
            onListModeChanged(listMode);
        }
    }

    public LIST_MODES getListMode() {
        return listMode;
    }

    /**
     * will be called when the list mode changed
     *
     * @param listMode
     */
    public abstract void onListModeChanged(LIST_MODES listMode);
}
