package ru.punkoff.stocksapp.utils

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FragmentArgumentDelegate<T : Any> : ReadWriteProperty<Fragment, T> {
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        Log.e(javaClass.simpleName, property.name)
        return thisRef.arguments?.get(property.name) as T
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        val args = thisRef.arguments ?: Bundle().also(thisRef::setArguments)
        args.put(property.name, value)
    }
}