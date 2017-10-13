package com.github.hindol;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;

public class Machine<S, I> {

    private final S mInitialState;
    private final Set<S> mTerminalStates;
    private final Map<S, Map<I, S>> mTransitions;
    private S mCurrentState;

    private Machine(S initialState, Set<S> terminalStates, Map<S, Map<I, S>> transitions) {
        mInitialState = mCurrentState = initialState;
        mTerminalStates = terminalStates;
        mTransitions = transitions;
    }

    public void reset() {
        mCurrentState = mInitialState;
    }

    public Machine<S, I> process(I... inputs) {
        Preconditions.checkNotNull(inputs);
        return process(Arrays.asList(inputs));
    }

    public Machine<S, I> process(List<I> inputs) {
        Preconditions.checkNotNull(inputs);

        for (I input : inputs) {
            Preconditions.checkNotNull(input);
            Preconditions.checkState(mTransitions.containsKey(mCurrentState));
            Preconditions.checkNotNull(mTransitions.get(mCurrentState).get(input));

            mCurrentState = mTransitions.get(mCurrentState).get(input);
        }

        return this;
    }

    public S peek() {
        return mCurrentState;
    }

    public boolean isTerminated() {
        return mTerminalStates.contains(mCurrentState);
    }

    public static class Builder<S, I> {

        private final Map<S, Map<I, S>> mTransitions = Maps.newHashMap();
        private final Set<S> mTerminalStates = Sets.newHashSet();

        public Builder<S, I> addState(S state) {
            // Adding a state that is not part of any transition is symbolic. Do nothing.
            return this;
        }

        public Builder<S, I> addTerminalState(S state) {
            Preconditions.checkState(!mTerminalStates.contains(state));

            mTerminalStates.add(state);
            return this;
        }

        public Builder<S, I> addTransition(S current, I input, S next) {
            Preconditions.checkNotNull(current);
            Preconditions.checkNotNull(input);
            Preconditions.checkNotNull(next);

            mTransitions.computeIfAbsent(current, s -> new HashMap<>());

            Preconditions.checkState(!mTransitions.get(current).containsKey(input));
            mTransitions.get(current).put(input, next);

            return this;
        }

        public Machine<S, I> build(S initialState) {
            Preconditions.checkNotNull(initialState);
            return new Machine<>(initialState, mTerminalStates, mTransitions);
        }
    }

    public static void main(String[] args) {
        Machine<String, String> machine = new Machine.Builder<String, String>()
                .addTransition("0S", "1R", "1S")
                .addTransition("0S", "2R", "2S")
                .addTransition("1S", "1R", "2S")
                .addTransition("1S", "2R", "3S")
                .addTransition("2S", "1R", "3S")
                .addTransition("2S", "2R", "4S")
                .addTransition("3S", "1R", "4S")
                .addTransition("4S", "BUY", "COMPLETED")
                .addTransition("0S", "CANCEL", "CANCELLED")
                .addTransition("1S", "CANCEL", "CANCELLED")
                .addTransition("2S", "CANCEL", "CANCELLED")
                .addTransition("3S", "CANCEL", "CANCELLED")
                .addTransition("4S", "CANCEL", "CANCELLED")
                .addTerminalState("CANCELLED")
                .addTerminalState("COMPLETED")
                .build("0S"); // Initial state

        machine.reset();
        machine.process("1R", "2R", "1R", "BUY");
        System.out.println(machine.isTerminated());
    }
}
