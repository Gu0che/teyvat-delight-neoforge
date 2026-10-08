package com.guoche.teyvatdelight.entity.katheryne;

/** Tracks one uninterrupted descent, not a sum of vanilla fall statistics. */
final class CommissionFallTracker {
  private double peak, previous;
  private boolean started, counted;

  boolean sample(double y, boolean freeFall, double maximumStep) {
    if (!Double.isFinite(y) || !freeFall || started && Math.abs(y - previous) > maximumStep) {
      reset();
      return false;
    }
    if (!started || y > previous) {
      peak = y;
      counted = false;
      started = true;
    }
    previous = y;
    if (!counted && peak - y >= 100.0) {
      counted = true;
      return true;
    }
    return false;
  }

  void reset() { started = false; counted = false; }
}
