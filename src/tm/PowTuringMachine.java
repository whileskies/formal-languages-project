package tm;

import java.util.*;

public class PowTuringMachine {
    private final List<Character> tape;

    private final List<Map<Character, StateTransition>> stateTransitionMap;

    public static final int START_STATE = 0;
    public static final int END_STATE = 17;

    public PowTuringMachine() {
        tape = new ArrayList<>();
        stateTransitionMap = new ArrayList<>(END_STATE + 1);
        for (int i = 0; i < END_STATE + 1; i++) {
            stateTransitionMap.add(new HashMap<>());
        }

        StateTransition s1 = new StateTransition(0, '0', 17, '0', true);
        StateTransition s2 = new StateTransition(0, '1', 1, 'a', true);
        addStateTransitionMap(0, Arrays.asList(s1, s2));

        StateTransition s3 = new StateTransition(1, '0', 2, '0', true);
        StateTransition s4 = new StateTransition(1, '1', 1, '1', true);
        addStateTransitionMap(1, Arrays.asList(s3, s4));

        StateTransition s5 = new StateTransition(2, '0', 10, '0', false);
        StateTransition s6 = new StateTransition(2, '1', 3, 'b', true);
        addStateTransitionMap(2, Arrays.asList(s5, s6));

        StateTransition s7 = new StateTransition(3, '0', 4, '0', true);
        StateTransition s8 = new StateTransition(3, '1', 3, '1', true);
        addStateTransitionMap(3, Arrays.asList(s7, s8));

        StateTransition s9 = new StateTransition(4, '0', 9, '0', false);
        StateTransition s10 = new StateTransition(4, '1', 5, 'c', true);
        addStateTransitionMap(4, Arrays.asList(s9, s10));

        StateTransition s11 = new StateTransition(5, '0', 6, '0', true);
        StateTransition s12 = new StateTransition(5, '1', 5, '1', true);
        addStateTransitionMap(5, Arrays.asList(s11, s12));

        StateTransition s13 = new StateTransition(6, '0', 7, '1', true);
        StateTransition s14 = new StateTransition(6, '1', 6, '1', true);
        StateTransition s15 = new StateTransition(6, ' ', 7, '1', true);
        addStateTransitionMap(6, Arrays.asList(s13, s14, s15));

        StateTransition s16 = new StateTransition(7, ' ', 8, '0', false);
        addStateTransitionMap(7, Collections.singletonList(s16));

        StateTransition s17 = new StateTransition(8, '0', 8, '0', false);
        StateTransition s18 = new StateTransition(8, '1', 8, '1', false);
        StateTransition s19 = new StateTransition(8, 'c', 4, 'c', true);
        addStateTransitionMap(8, Arrays.asList(s17, s18, s19));

        StateTransition s20 = new StateTransition(9, '0', 9, '0', false);
        StateTransition s21 = new StateTransition(9, '1', 9, '1', false);
        StateTransition s22 = new StateTransition(9, 'b', 2, 'b', true);
        StateTransition s23 = new StateTransition(9, 'c', 9, '1', false);
        addStateTransitionMap(9, Arrays.asList(s20, s21, s22, s23));

        StateTransition s24 = new StateTransition(10, '0', 11, '0', true);
        StateTransition s25 = new StateTransition(10, 'b', 10, '1', false);
        addStateTransitionMap(10, Arrays.asList(s24, s25));

        StateTransition s26 = new StateTransition(11, '0', 12, '0', true);
        StateTransition s27 = new StateTransition(11, '1', 11, '1', true);
        addStateTransitionMap(11, Arrays.asList(s26, s27));

        StateTransition s28 = new StateTransition(12, '0', 13, '1', true);
        StateTransition s29 = new StateTransition(12, '1', 12, 'd', true);
        addStateTransitionMap(12, Arrays.asList(s28, s29));

        StateTransition s30 = new StateTransition(13, '0', 14, ' ', false);
        StateTransition s31 = new StateTransition(13, '1', 13, '1', true);
        addStateTransitionMap(13, Arrays.asList(s30, s31));

        StateTransition s32 = new StateTransition(14, '1', 15, '0', false);
        addStateTransitionMap(14, Collections.singletonList(s32));

        StateTransition s33 = new StateTransition(15, '0', 16, '0', false);
        StateTransition s34 = new StateTransition(15, '1', 15, '1', false);
        StateTransition s35 = new StateTransition(15, 'd', 13, '1', true);
        addStateTransitionMap(15, Arrays.asList(s33, s34, s35));

        StateTransition s36 = new StateTransition(16, '0', 16, '0', false);
        StateTransition s37 = new StateTransition(16, '1', 16, '1', false);
        StateTransition s38 = new StateTransition(16, 'a', 0, 'a', true);
        addStateTransitionMap(16, Arrays.asList(s36, s37, s38));

    }

    private void addStateTransitionMap(int curState, List<StateTransition> addStateTransitions) {
        Map<Character, StateTransition> map = stateTransitionMap.get(curState);

        for (StateTransition st : addStateTransitions) {
            map.put(st.getRead(), st);
        }

        stateTransitionMap.set(curState, map);
    }


    public int pow(int x, int y) {
        initTape(x, y);
        int cur = START_STATE;  //读头指针
        int state = START_STATE;  //当前状态

        while (state != END_STATE) {
            System.out.println(getID(cur, state));

            char read = tape.get(cur);

            StateTransition st = stateTransitionMap.get(state).get(read);
            if (st == null) {
                state = END_STATE;
            } else {
                state = st.getNextState();
                if (read == ' ') {
                    tape.add(' ');
                }
                tape.set(cur, st.getWrite());
                if (st.isToRight()) {
                    cur = cur + 1;
                } else {
                    cur = cur - 1;
                }
            }
        }

        System.out.println(tape);

        int last = tape.size() - 1;
        while (tape.get(last) == ' ') {
            last--;
        }

        int ret = 0;
        for (int i = last - 1; i >= 0; i--) {
            if (tape.get(i) == '1')
                ret++;
            else if (tape.get(i) == '0')
                break;
        }

        return ret;
    }

    //y0x010的初始带
    private void initTape(int x, int y) {
        if (y == 0) {
            tape.add('0');
        } else {
            for (int i = 0; i < y; i++) {
                tape.add('1');
            }
        }

        tape.add('0');

        for (int i = 0; i < x; i++) {
            tape.add('1');
        }

        tape.add('0');
        tape.add('1');
        tape.add('0');
        tape.add(' ');
    }

    private String getID(int cur, int state) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tape.size(); i++) {
            if (cur == i) {
                sb.append("q");
                sb.append(state);
                sb.append(" ");
            }
            sb.append(tape.get(i));
            sb.append(" ");
        }
        if (cur == tape.size()) {
            sb.append("q");
            sb.append(state);
            sb.append(" ");
        }

        return sb.toString();
    }



    public static void main(String[] args) {
        PowTuringMachine tm = new PowTuringMachine();
        System.out.println("ret: " + tm.pow(2, 3));
    }

}
