/* 
 * Xuan Phuong Nguyen, 14 Feb, 2025
 * This program will discuss the 3-water-jug problem that follows the puzzle-solving approach using BFS (Breadth-First Search) to explore all possible states
 * and tracks parent states, and prints the state transition graph, while noting states produced from multiple parents with a special notation like p2, p3, etc.
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.*;

public class lastName1_lastName2 {

   // Class to represent the state of the jugs
   static class State {
       int a, b, c; // Amounts of water in jugs A, B, and C
       List<State> parents = new ArrayList<>(); // List of parent states
       
       public State(int a, int b, int c) {
           this.a = a;
           this.b = b;
           this.c = c;
       }
       
       @Override
       public boolean equals(Object obj) {
           if (this == obj) return true;
           if (obj == null || getClass() != obj.getClass()) return false;
           State state = (State) obj;
           return a == state.a && b == state.b && c == state.c;
       }

       @Override
       public int hashCode() {
           return Objects.hash(a, b, c);
       }
       
       @Override
       public String toString() {
           return "[" + a + ", " + b + ", " + c + "]";
       }
   }
   
   public static void main(String[] args) {
       // Read inputs from command line arguments
       int capA = Integer.parseInt(args[0]);  // Capacity of Jug A
       int capB = Integer.parseInt(args[1]);  // Capacity of Jug B
       int capC = Integer.parseInt(args[2]);  // Capacity of Jug C
       int startA = Integer.parseInt(args[3]);  // Initial amount in Jug A
       int startB = Integer.parseInt(args[4]);  // Initial amount in Jug B
       int startC = Integer.parseInt(args[5]);  // Initial amount in Jug C
       
       // Initialize the initial state
       State initialState = new State(startA, startB, startC);
       Queue<State> queue = new LinkedList<>();  // Queue for BFS
       Set<State> visited = new HashSet<>();  // Set to track visited states
       Map<State, List<State>> stateGraph = new HashMap<>();  // State transition graph
       
       // Start BFS with the initial state
       queue.add(initialState);
       visited.add(initialState);
       stateGraph.put(initialState, new ArrayList<>());
       
       // Breadth-First Search (BFS) to explore all possible states
       while (!queue.isEmpty()) {
           State current = queue.poll();  // Dequeue the front state
           
           // Generate all possible next states from the current state
           List<State> nextStates = generateNextStates(current, capA, capB, capC);
           
           for (State next : nextStates) {
               // If the next state is already visited, add current as an additional parent
               if (visited.contains(next)) {
                   stateGraph.get(next).add(current);
               } else {
                   // If new state, add to queue and mark visited
                   next.parents.add(current);
                   queue.add(next);
                   visited.add(next);
                   stateGraph.put(next, new ArrayList<>(Collections.singletonList(current)));
               }
           }
       }
       
       // Print the state transition graph
       printStateTransitionGraph(stateGraph);
   }

   // Generate all possible next states by pouring water between jugs
   static List<State> generateNextStates(State current, int capA, int capB, int capC) {
       List<State> states = new ArrayList<>();
       int a = current.a, b = current.b, c = current.c;
       
       // Transfer water between jugs (A <-> B, B <-> C, A <-> C)
       states.add(pour(a, b, capB, 'A', 'B'));  // A to B
       states.add(pour(a, c, capC, 'A', 'C'));  // A to C
       states.add(pour(b, a, capA, 'B', 'A'));  // B to A
       states.add(pour(b, c, capC, 'B', 'C'));  // B to C
       states.add(pour(c, a, capA, 'C', 'A'));  // C to A
       states.add(pour(c, b, capB, 'C', 'B'));  // C to B
       
       return states;
   }
   
   // Pour water from one jug to another
   static State pour(int from, int to, int toCapacity, char fromJug, char toJug) {
       int pourAmount = Math.min(from, toCapacity - to);  // Calculate the maximum amount of water that can be poured
       return new State(
           from - pourAmount,  // Update the amount in the source jug
           to + pourAmount,    // Update the amount in the destination jug
           0  // Assume the third jug is constant for simplicity here
       );
   }

   // Print the state transition graph
   static void printStateTransitionGraph(Map<State, List<State>> stateGraph) {
       int step = 0;
       for (Map.Entry<State, List<State>> entry : stateGraph.entrySet()) {
           State state = entry.getKey();
           List<State> parents = entry.getValue();
           System.out.print("Step " + step + ": " + state);
           
           if (parents.size() > 1) {
               System.out.println(" p" + parents.size());  // Mark if a state has multiple parent states
           } else {
               System.out.println();
           }
           step++;
       }
   }
} 
