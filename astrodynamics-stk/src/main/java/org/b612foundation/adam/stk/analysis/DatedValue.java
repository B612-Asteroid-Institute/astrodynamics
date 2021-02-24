package org.b612foundation.adam.stk.analysis;

import agi.foundation.time.JulianDate;

/** Something with a Julian date attached to it. */
public class DatedValue<T> implements Comparable<DatedValue<T>> {
  JulianDate date;
  T value;

  public DatedValue(JulianDate date, T value) {
    this.date = date;
    this.value = value;
  }

  public JulianDate getDate() {
    return date;
  }

  public T getValue() {
    return value;
  }

  @Override
  public int compareTo(DatedValue<T> other) {
    return this.date.compareTo(other.date);
  }
}

