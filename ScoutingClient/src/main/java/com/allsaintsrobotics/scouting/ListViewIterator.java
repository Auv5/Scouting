package com.allsaintsrobotics.scouting;

import android.widget.ListView;

import java.util.Iterator;

/**
 * Created by Jack on 7/31/13.
 */
public class ListViewIterator<T> implements Iterator<T> {
    private int index = 0;

    private ListView lv;

    public ListViewIterator(ListView lv)
    {
        this.lv = lv;
    }

    @Override
    public boolean hasNext() {
        return index != (lv.getCount() - 1);
    }

    @Override
    public T next() {
        return (T)lv.getItemAtPosition(index++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
