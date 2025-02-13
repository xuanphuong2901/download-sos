/*
 * Xuan P Nguyen, Earl Dozier, Rania Bouiri, Ruotong Jiao, Thanh Son Nguyen, Archita Rajesh, 24 May. 2024
 * This program asks users Write a program named Checkout.java that uses queues to compare three different models for self-checkout stations at a grocery store:
 * One line for customers, with n checkout stations. Customers go to the next available station.
 * n lines for customers, with one checkout station per line. Customers go to the line with the fewest number of customers.
 * n lines for customers, with one checkout station per line. Customers go to a randomly chosen line.
 */
import java.util.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Customer {
    private int checkoutTime; // Checkout time (in seconds)

    private int waitTime;

    public Customer(int checkoutTime) {
        this.checkoutTime = checkoutTime;
        this.waitTime = 0;
    }

    public int getCheckoutTime() {
        return checkoutTime;
    }

    public int getWaitTime(){
        return this.waitTime;
    }

    public void addWaitTime(){
        this.waitTime += 1;
    }

    public void setWaitTime(){ this.waitTime = 0; }

    public void decrementCheckoutTime() {
        if (checkoutTime > 0) {
            checkoutTime--;
        }
    }
}

class CustomerGenerator {
    private List<Customer> customers;

    public CustomerGenerator(int numberOfCustomers) {
        customers = new ArrayList<>();
        Random rand = new Random(42); // Set fixed random seeds
        for (int i = 0; i < numberOfCustomers; i++) {
            int checkoutTime = rand.nextInt(61)+20; // Generate a checkout time of 60 to 80 seconds
            System.out.println("Customer " + (i+1) + " generated with checkout time of " + checkoutTime + " seconds.");
            customers.add(new Customer(checkoutTime));
        }
    }

    public List<Customer> getCustomers() {
        return customers;
    }
}

class Cashier {
    private int id;
    private Queue<Customer> queue;
    private int currentServiceTime;
    private int servedCustomers;
    private long totalWaitingTime;
    private int maxQueueLength;

    public Cashier(int id) {
        this.id = id;
        this.queue = new LinkedList<>();
        this.currentServiceTime = 0;
        this.servedCustomers = 0;
        this.totalWaitingTime = 0;
        this.maxQueueLength = 0;
    }

    public int getServedCustomers() {
        return servedCustomers;
    }

    public long getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public Queue<Customer> getQueue() {
        return queue;
    }

    public int getId(){
        return this.id;
    }

    public void addCustomer(Customer customer) {
        customer.setWaitTime();
        queue.add(customer);
        int currentQueueSize = queue.size();
        if (currentQueueSize > maxQueueLength) {
            maxQueueLength = currentQueueSize;
        }
        if (currentServiceTime == 0 && !queue.isEmpty()) {
            Customer nextCustomer = queue.poll();
            currentServiceTime = nextCustomer.getCheckoutTime();
        }
    }

    public void update() {
        if (currentServiceTime > 0) {
            currentServiceTime--;
            if (currentServiceTime == 0) {
                servedCustomers++;
                if (!queue.isEmpty()) {
                    Customer nextCustomer = queue.poll();
                    System.out.println(""+ nextCustomer.getWaitTime());
                    totalWaitingTime += nextCustomer.getWaitTime();
                    currentServiceTime = nextCustomer.getCheckoutTime();
                }
            }
        }
        queue.stream().forEach(p->p.addWaitTime());
    }
}

public class Checkout {
    public static void main(String[] args) {
        // Scene 1: One Cashier
        Cashier cashier1 = new Cashier(1);
        CustomerGenerator generator1 = new CustomerGenerator(720); // Generate 720 customers
        List<Customer> customers1 = generator1.getCustomers();
        Queue<Customer> customerQueue1 = new LinkedList<>(customers1);

        // Simulation for 2 hours (7200 times, once per second)
        for (int time = 0; time < 7200; time++) {
            if (time % 10 == 0 && !customerQueue1.isEmpty()) {
                cashier1.addCustomer(customerQueue1.poll());
            }
            cashier1.update();
        }
        while (!cashier1.getQueue().isEmpty()) {
            cashier1.update();
        }

        System.out.println("Scene 1: There is only one cashier.");
        System.out.println("Checkout counter 1 served " + cashier1.getServedCustomers() + " customers.");
        System.out.println("Average waiting time: " + (cashier1.getTotalWaitingTime() / (double) cashier1.getServedCustomers()) + " seconds");
        System.out.println("Maximum queue length: " + cashier1.getMaxQueueLength());

        // Scene 2: Multiple Cashiers
        List<Cashier> cashiers = new ArrayList<>();
        List<Customer> customers2 = generator1.getCustomers();
        Queue<Customer> customerQueue2 = new LinkedList<>(customers2);
        Random random = new Random(42); // Set fixed random seed

        for (int i = 0; i < 5; i++) {
            cashiers.add(new Cashier(i + 1));
        }

        // Simulation for 2 hours (7200 times, once per second)
        for (int time = 0; time < 7200; time++) {
            if (time % 10 == 0 && !customerQueue2.isEmpty()) {
                int randomIndex = random.nextInt(5);
                cashiers.get(randomIndex).addCustomer(customerQueue2.poll());
            }
            for (Cashier cashier : cashiers) {
                cashier.update();
            }
        }
        for (Cashier cashier : cashiers) {
            while (!cashier.getQueue().isEmpty()) {
                cashier.update();
            }
        }

        System.out.println("\nScene 2: There are 5 checkout counters, and customers randomly select one checkout counter to queue.");
        for (Cashier cashier : cashiers) {
            System.out.println("Cashier counter " + cashier.getId() + " served " + cashier.getServedCustomers() + " customers.");
            System.out.println("Average waiting time: " + (cashier.getTotalWaitingTime() / (double) cashier.getServedCustomers()) + " seconds");
            System.out.println("Maximum queue length: " + cashier.getMaxQueueLength());
        }

		// Scenario 3: Multiple Cashiers - Fewest People
        List<Cashier> cashiersFewest = new ArrayList<>();
        List<Customer> customers3 = generator1.getCustomers();
        Queue<Customer> customerQueue3 = new LinkedList<>(customers3);
        for (int i = 0; i < 5; i++) {
            cashiersFewest.add(new Cashier(i + 1));
        }

        // Simulation for 2 hours (7200 times, once per second)
        for (int time = 0; time < 7200; time++) {
            if (time % 10 == 0 && !customerQueue3.isEmpty()) {
                Cashier leastBusyCashier = cashiersFewest.get(0);
                int minQueueSize = Integer.MAX_VALUE;

                // Find the cashier with the shortest queue
                for (Cashier cashier : cashiersFewest) {
                    if (cashier.getQueue().size() < minQueueSize) {
                        minQueueSize = cashier.getQueue().size();
                        leastBusyCashier = cashier;
                    }
                }

                leastBusyCashier.addCustomer(customerQueue3.poll());
            }

            // Update the status of each cashier
            for (Cashier cashier : cashiersFewest) {
                cashier.update();
            }
        }
        // Until all the customers check out.
        for (Cashier cashier : cashiersFewest) {
            while (!cashier.getQueue().isEmpty()) {
                cashier.update();
            }
        }

        System.out.println("\nScenario 3: There are 5 cashiers, and customers choose the queue with the fewest people.");
        for (Cashier cashier : cashiersFewest) {
            System.out.println("Cashier counter " + cashier.getId() + " served " + cashier.getServedCustomers() + " customers.");
            System.out.println("Average waiting time: " + (cashier.getTotalWaitingTime() / (double) cashier.getServedCustomers()) + " seconds");
            System.out.println("Maximum queue length: " + cashier.getMaxQueueLength());
        }
    }
}
