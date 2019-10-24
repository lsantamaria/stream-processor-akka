package com.lsantamaria;

import akka.NotUsed;
import akka.stream.javadsl.Flow;

/**
 * A stream processor is a program which takes as input one or more streams of events/messages, runs
 * some computation for each event, and generates output in the form of other events, side effects,
 * or a combination of the two.
 */
public interface StreamProcessor<I, O> {

  Flow<I, O, NotUsed> process();
}
