import java.util.Scanner;
import java.util.ArrayList;

// ============================================================
//  KLH TRANSPORT TRACKER — Java Console Application
//  Covers all 5 syllabus topics:
//   1. Sorting        — Bubble Sort, Insertion Sort
//   2. Linked Lists   — Doubly Linked List
//   3. Stack & Queue  — Stack (LIFO), Queue (FIFO)
//   4. Hashing        — Division Method
//   5. Collision      — Linear Probing, Separate Chaining
// ============================================================

public class KLHTransportTracker {

    // ========================================================
    // TOPIC 2 — DOUBLY LINKED LIST
    // Each stop on a bus route is a node with next & prev.
    // ========================================================
    static class StopNode {
        String name;
        StopNode next;
        StopNode prev;

        StopNode(String name) {
            this.name = name;
            this.next = null;
            this.prev = null;
        }
    }

    static class DoublyLinkedList {
        StopNode head;
        StopNode tail;
        int size;

        DoublyLinkedList() {
            head = null;
            tail = null;
            size = 0;
        }

        void append(String name) {
            StopNode node = new StopNode(name);
            if (head == null) {
                head = node;
                tail = node;
            } else {
                node.prev = tail;
                tail.next = node;
                tail = node;
            }
            size++;
        }

        void insertAfter(int index, String name) {
            StopNode curr = head;
            int i = 0;
            while (curr != null && i < index) {
                curr = curr.next;
                i++;
            }
            if (curr == null) { append(name); return; }
            StopNode node = new StopNode(name);
            node.next = curr.next;
            node.prev = curr;
            if (curr.next != null) curr.next.prev = node;
            else tail = node;
            curr.next = node;
            size++;
        }

        void removeAt(int index) {
            StopNode curr = head;
            int i = 0;
            while (curr != null && i < index) {
                curr = curr.next;
                i++;
            }
            if (curr == null) return;
            if (curr.prev != null) curr.prev.next = curr.next;
            else head = curr.next;
            if (curr.next != null) curr.next.prev = curr.prev;
            else tail = curr.prev;
            size--;
        }

        void printForward() {
            StopNode curr = head;
            int idx = 1;
            while (curr != null) {
                String label = (curr == head) ? " [ORIGIN]" : (curr == tail) ? " [DESTINATION]" : " [Stop " + idx + "]";
                System.out.println("   " + idx + ". " + curr.name + label);
                curr = curr.next;
                idx++;
            }
        }

        void printBackward() {
            StopNode curr = tail;
            int idx = size;
            System.out.println("   (Reverse traversal — tail to head)");
            while (curr != null) {
                System.out.println("   " + idx + ". " + curr.name);
                curr = curr.prev;
                idx--;
            }
        }

        String[] toArray() {
            String[] arr = new String[size];
            StopNode curr = head;
            int i = 0;
            while (curr != null) { arr[i++] = curr.name; curr = curr.next; }
            return arr;
        }
    }

    // ========================================================
    // TOPIC 3 — STACK (LIFO)
    // Used for undo history of stop edits.
    // ========================================================
    static class UndoStack {
        private ArrayList<String[]> items = new ArrayList<>();

        void push(String[] state) {
            items.add(state.clone());
        }

        String[] pop() {
            if (isEmpty()) return null;
            return items.remove(items.size() - 1);
        }

        String[] peek() {
            if (isEmpty()) return null;
            return items.get(items.size() - 1);
        }

        boolean isEmpty() { return items.isEmpty(); }
        int size()        { return items.size(); }
    }

    // ========================================================
    // TOPIC 3 — QUEUE (FIFO)
    // Remembers the last 5 buses the user selected.
    // ========================================================
    static class BusQueue {
        private int[] items;
        private int front, rear, count, maxSize;

        BusQueue(int maxSize) {
            this.maxSize = maxSize;
            items = new int[maxSize];
            front = 0; rear = 0; count = 0;
        }

        void enqueue(int bus) {
            if (count == maxSize) dequeue();
            items[rear % maxSize] = bus;
            rear++;
            count++;
        }

        int dequeue() {
            if (isEmpty()) return -1;
            int val = items[front % maxSize];
            front++;
            count--;
            return val;
        }

        boolean isEmpty() { return count == 0; }

        void print() {
            if (isEmpty()) { System.out.println("   (No recent selections)"); return; }
            System.out.print("   Recently selected buses (oldest → newest): ");
            for (int i = 0; i < count; i++) {
                System.out.print("Bus " + items[(front + i) % maxSize]);
                if (i < count - 1) System.out.print(" → ");
            }
            System.out.println();
        }
    }

    // ========================================================
    // TOPIC 4 & 5 — HASHING: Division Method + Linear Probing
    // ========================================================
    static class RouteHashTable {
        static final int TABLE_SIZE = 31;
        int[]      keys      = new int[TABLE_SIZE];
        String[]   paths     = new String[TABLE_SIZE];
        String[][] stops     = new String[TABLE_SIZE][];
        String[]   locations = new String[TABLE_SIZE]; // live location URLs

        RouteHashTable() {
            for (int i = 0; i < TABLE_SIZE; i++) {
                keys[i]      = -1;
                locations[i] = null;
            }
        }

        int hash(int busNumber) {
            return busNumber % TABLE_SIZE;
        }

        void insert(int busNumber, String path, String[] stopList) {
            int slot = hash(busNumber);
            int i = 0;
            while (i < TABLE_SIZE) {
                int probe = (slot + i) % TABLE_SIZE;
                if (keys[probe] == -1) {
                    keys[probe]      = busNumber;
                    paths[probe]     = path;
                    stops[probe]     = stopList;
                    locations[probe] = null;
                    return;
                }
                i++;
            }
        }

        int search(int busNumber) {
            int slot = hash(busNumber);
            int i = 0;
            while (i < TABLE_SIZE) {
                int probe = (slot + i) % TABLE_SIZE;
                if (keys[probe] == -1)        return -1;
                if (keys[probe] == busNumber) return probe;
                i++;
            }
            return -1;
        }

        void printTable() {
            System.out.println("\n   Slot | Bus# | Route");
            System.out.println("   -----|------|-------------------------------");
            for (int i = 0; i < TABLE_SIZE; i++) {
                if (keys[i] != -1) {
                    System.out.printf("   %4d | %4d | %s%n", i, keys[i], paths[i]);
                } else {
                    System.out.printf("   %4d |  --- | (empty)%n", i);
                }
            }
        }
    }

    // ========================================================
    // TOPIC 4 & 5 — HASHING: Division Method + Separate Chaining
    // ========================================================
    static class CredentialNode {
        String email, password, bus;
        CredentialNode next;
        CredentialNode(String e, String p, String b) {
            email = e; password = p; bus = b; next = null;
        }
    }

    static class CredentialHashTable {
        static final int TABLE_SIZE = 13;
        CredentialNode[] chains = new CredentialNode[TABLE_SIZE];

        int hash(String email) {
            int sum = 0;
            for (char c : email.toCharArray()) sum += c;
            return sum % TABLE_SIZE;
        }

        void insert(String email, String password, String bus) {
            int slot = hash(email);
            CredentialNode node = new CredentialNode(email, password, bus);
            node.next    = chains[slot];
            chains[slot] = node;
        }

        CredentialNode search(String email, String password) {
            int slot = hash(email);
            CredentialNode curr = chains[slot];
            while (curr != null) {
                if (curr.email.equals(email) && curr.password.equals(password))
                    return curr;
                curr = curr.next;
            }
            return null;
        }

        void printChains() {
            System.out.println("\n   Slot | Chain");
            System.out.println("   -----|-------------------------------");
            for (int i = 0; i < TABLE_SIZE; i++) {
                System.out.printf("   %4d | ", i);
                CredentialNode curr = chains[i];
                if (curr == null) { System.out.println("(empty)"); continue; }
                while (curr != null) {
                    System.out.print("[" + curr.email + "]");
                    if (curr.next != null) System.out.print(" → ");
                    curr = curr.next;
                }
                System.out.println();
            }
        }
    }

    // ========================================================
    // TOPIC 1 — BUBBLE SORT
    // ========================================================
    static void bubbleSort(String[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    String temp = arr[j];
                    arr[j]      = arr[j + 1];
                    arr[j + 1]  = temp;
                }
            }
        }
    }

    // ========================================================
    // TOPIC 1 — INSERTION SORT
    // ========================================================
    static void insertionSort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j   = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    // ========================================================
    // ROUTE DATA
    // ========================================================
    static final String[] ROUTE_PATHS = {
        "LB Nagar → KLH Campus",       "Kukatpally → KLH Campus",
        "Secunderabad → KLH Campus",    "Dilsukhnagar → KLH Campus",
        "Madhapur → KLH Campus",        "Bowenpally → KLH Campus",
        "Tolichowki → KLH Campus",      "Ghatkesar → KLH Campus",
        "Patancheru → KLH Campus",      "Kompally → KLH Campus",
        "Vanasthalipuram → KLH Campus", "Chandanagar → KLH Campus",
        "Alwal → KLH Campus",           "Suchitra → KLH Campus",
        "Hayathnagar → KLH Campus",     "Mehdipatnam → KLH Campus",
        "Moula Ali → KLH Campus",       "Attapur → KLH Campus",
        "Bahadurpura → KLH Campus",     "Charminar → KLH Campus",
        "Ramanthapur → KLH Campus",     "Nacharam → KLH Campus",
        "Tarnaka → KLH Campus",         "Himayatnagar → KLH Campus",
        "Malakpet → KLH Campus",        "Santoshnagar → KLH Campus",
        "Musheerabad → KLH Campus"
    };

    static final String[][] ROUTE_STOPS = {
        {"LB Nagar","Vanasthalipuram","Nagole","Uppal","KLH Campus"},
        {"Kukatpally","KPHB","Miyapur","Kondapur","KLH Campus"},
        {"Secunderabad","Paradise","Begumpet","Ameerpet","KLH Campus"},
        {"Dilsukhnagar","Kothapet","Malakpet","Mehdipatnam","KLH Campus"},
        {"Madhapur","Hitech City","Kondapur","Gachibowli","KLH Campus"},
        {"Bowenpally","Malkajgiri","Trimulgherry","SR Nagar","KLH Campus"},
        {"Tolichowki","Masab Tank","Rethibowli","Banjara Hills","KLH Campus"},
        {"Ghatkesar","Peerzadiguda","Uppal","Ramanthapur","KLH Campus"},
        {"Patancheru","Balanagar","Moosapet","Bhel","KLH Campus"},
        {"Kompally","Medchal","Quthbullapur","Nizampet","KLH Campus"},
        {"Vanasthalipuram","Hayathnagar","Saroornagar","KLH Campus"},
        {"Chandanagar","Lingampally","Miyapur","Nizampet","KLH Campus"},
        {"Alwal","Bolarum","Malkajgiri","Tarnaka","KLH Campus"},
        {"Suchitra","Kompally","Jeedimetla","Balanagar","KLH Campus"},
        {"Hayathnagar","Saroornagar","LB Nagar","Uppal","KLH Campus"},
        {"Mehdipatnam","Tolichowki","Nanalnagar","Masab Tank","KLH Campus"},
        {"Moula Ali","Nacharam","Uppal","Ramanthapur","KLH Campus"},
        {"Attapur","Rajendranagar","Mehdipatnam","Tolichowki","KLH Campus"},
        {"Bahadurpura","Chandrayangutta","Falaknuma","Saidabad","KLH Campus"},
        {"Charminar","Abids","Nampally","Ameerpet","KLH Campus"},
        {"Ramanthapur","Nacharam","Uppal","Malkajgiri","KLH Campus"},
        {"Nacharam","Habsiguda","Tarnaka","Necklace Road","KLH Campus"},
        {"Tarnaka","Mettuguda","Secunderabad","Begumpet","KLH Campus"},
        {"Himayatnagar","Narayanguda","Chaderghat","Abids","KLH Campus"},
        {"Malakpet","Kothapet","Dilsukhnagar","LB Nagar","KLH Campus"},
        {"Santoshnagar","Saroornagar","Champapet","LB Nagar","KLH Campus"},
        {"Musheerabad","RTC X Roads","SR Nagar","Ameerpet","KLH Campus"}
    };

    // ========================================================
    // HELPERS
    // ========================================================
    static void divider() {
        System.out.println("  ─────────────────────────────────────────────────");
    }

    static void header(String title) {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.printf( "║  %-48s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    static void sectionHeader(String title) {
        System.out.println("\n  ┌─────────────────────────────────────────────────");
        System.out.println("  │  " + title);
        System.out.println("  └─────────────────────────────────────────────────");
    }

    // ========================================================
    // MAIN
    // ========================================================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        RouteHashTable routeTable = new RouteHashTable();
        for (int i = 0; i < 27; i++) {
            routeTable.insert(i + 1, ROUTE_PATHS[i], ROUTE_STOPS[i]);
        }

        CredentialHashTable credTable = new CredentialHashTable();
        int[] busNums = new int[27];
        for (int i = 0; i < 27; i++) busNums[i] = i + 1;
        insertionSort(busNums);
        for (int n : busNums) {
            credTable.insert("bus" + n + "@klh.com", "bus" + n + "pass", "bus" + n);
        }

        BusQueue recentQueue = new BusQueue(5);
        UndoStack undoStack  = new UndoStack();
        String loggedInBus   = null;

        boolean running = true;
        while (running) {
            header("KLH TRANSPORT TRACKER  —  Main Menu");
            System.out.println("  1. View All Routes (sorted A\u2192Z by Bubble Sort)");
            System.out.println("  2. Search Bus Route (Hash Lookup)");
            System.out.println("  3. View Bus Stops (Doubly Linked List)");
            System.out.println("  4. View Recent Selections (Queue - FIFO)");
            System.out.println("  5. Staff Login (Separate Chaining Auth)");
            System.out.println("  6. Edit Bus Stops + Undo (Stack - LIFO)");
            System.out.println("  7. Show Hash Tables (Internal Structure)");
            System.out.println("  8. Sort Demo (Bubble Sort + Insertion Sort)");
            System.out.println("  0. Exit");
            divider();
            System.out.print("  Enter choice: ");

            String input = sc.nextLine().trim();

            switch (input) {

                // ────────────────────────────────────────────
                // OPTION 1 — BUBBLE SORT
                // ────────────────────────────────────────────
                case "1": {
                    sectionHeader("All Routes (A \u2192 Z)");
                    String[] names = ROUTE_PATHS.clone();
                    bubbleSort(names);
                    System.out.println("  Routes:\n");
                    for (int i = 0; i < names.length; i++) {
                        System.out.printf("   %2d. %s%n", i + 1, names[i]);
                    }
                    break;
                }

                // ────────────────────────────────────────────
                // OPTION 2 — HASH LOOKUP
                // ────────────────────────────────────────────
                case "2": {
                    sectionHeader("Search Bus Route");
                    System.out.print("  Enter bus number (1-27): ");
                    try {
                        int busNum = Integer.parseInt(sc.nextLine().trim());
                        int slot   = routeTable.search(busNum);
                        if (slot == -1) {
                            System.out.println("  \u2717 Bus " + busNum + " not found.");
                        } else {
                            System.out.println("\n  Route  : " + routeTable.paths[slot]);
                            System.out.println("  Stops  : " + String.join(" \u2192 ", routeTable.stops[slot]));
                            String loc = routeTable.locations[slot];
                            if (loc != null) {
                                System.out.println("  Live   : " + loc);
                            } else {
                                System.out.println("  Live   : (not updated yet by incharge)");
                            }
                            recentQueue.enqueue(busNum);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("  \u2717 Invalid input.");
                    }
                    break;
                }

                // ────────────────────────────────────────────
                // OPTION 3 — DOUBLY LINKED LIST + OPEN LOCATION
                // ────────────────────────────────────────────
                case "3": {
                    sectionHeader("Bus Stop Details");
                    System.out.print("  Enter bus number (1-27): ");
                    try {
                        int busNum = Integer.parseInt(sc.nextLine().trim());
                        int slot   = routeTable.search(busNum);
                        if (slot == -1) {
                            System.out.println("  \u2717 Bus " + busNum + " not found.");
                        } else {
                            DoublyLinkedList dll = new DoublyLinkedList();
                            for (String s : routeTable.stops[slot]) dll.append(s);

                            System.out.println("\n  Route : " + routeTable.paths[slot]);
                            System.out.println("  Total stops: " + dll.size + "\n");
                            System.out.println("  \u25ba Forward (origin \u2192 destination):");
                            dll.printForward();
                            System.out.println("\n  \u25c4 Reverse (destination \u2192 origin):");
                            dll.printBackward();

                            // Live location option
                            String loc = routeTable.locations[slot];
                            System.out.println("\n  \u25ba Live Location:");
                            if (loc != null) {
                                System.out.println("  URL: " + loc);
                                System.out.print("  Open live location? (Y/N): ");
                                String choice = sc.nextLine().trim().toUpperCase();
                                if (choice.equals("Y")) {
                                    System.out.println("  Opening: " + loc);
                                    try {
                                        java.awt.Desktop.getDesktop().browse(new java.net.URI(loc));
                                        System.out.println("  \u2713 Launched in browser.");
                                    } catch (Exception ex) {
                                        System.out.println("  (Cannot open browser in this environment)");
                                        System.out.println("  Copy the URL above to view the live location.");
                                    }
                                }
                            } else {
                                System.out.println("  Location not yet updated by the bus incharge.");
                                System.out.print("  Open live location? (Y/N): ");
                                sc.nextLine(); // consume input
                                System.out.println("  \u26a0 No location available to open.");
                            }

                            recentQueue.enqueue(busNum);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("  \u2717 Invalid input.");
                    }
                    break;
                }

                // ────────────────────────────────────────────
                // OPTION 4 — QUEUE (FIFO)
                // ────────────────────────────────────────────
                case "4": {
                    sectionHeader("Recent Bus Selections");
                    System.out.println("  Last 5 buses you searched (oldest \u2192 newest):\n");
                    recentQueue.print();
                    break;
                }

                // ────────────────────────────────────────────
                // OPTION 5 — STAFF LOGIN + UPDATE LOCATION
                // ────────────────────────────────────────────
                case "5": {
                    sectionHeader("Staff Login");
                    System.out.println("  Format:  bus[N]@klh.com  /  bus[N]pass");
                    System.out.println("  Example: bus1@klh.com    /  bus1pass\n");
                    System.out.print("  Email    : ");
                    String email    = sc.nextLine().trim();
                    System.out.print("  Password : ");
                    String password = sc.nextLine().trim();

                    CredentialNode match = credTable.search(email, password);
                    if (match != null) {
                        loggedInBus = match.bus;
                        System.out.println("  \u2713 Login successful! Welcome, " + match.bus.toUpperCase() + " Incharge.");

                        // After login — show incharge dashboard
                        int busNum = Integer.parseInt(loggedInBus.replace("bus", ""));
                        int slot   = routeTable.search(busNum);

                        boolean dashboard = true;
                        while (dashboard) {
                            System.out.println("\n  ─── Incharge Dashboard (" + loggedInBus.toUpperCase() + ") ───");
                            System.out.println("  Route : " + routeTable.paths[slot]);
                            String curLoc = routeTable.locations[slot];
                            System.out.println("  Live  : " + (curLoc != null ? curLoc : "(not set)"));
                            System.out.println("\n  [U] Update live location");
                            System.out.println("  [V] View current live location");
                            System.out.println("  [B] Back to main menu");
                            System.out.print("  Choice: ");
                            String ch = sc.nextLine().trim().toUpperCase();

                            if (ch.equals("U")) {
                                System.out.println("\n  Paste your Google Maps live location link below.");
                                System.out.println("  (Get it from Google Maps \u2192 Share \u2192 Copy link)");
                                System.out.print("  Link: ");
                                String link = sc.nextLine().trim();
                                if (link.startsWith("http")) {
                                    routeTable.locations[slot] = link;
                                    System.out.println("  \u2713 Location updated! Students can now track your bus.");
                                    System.out.println("  Saved: " + link);
                                } else {
                                    System.out.println("  \u2717 Invalid link. Please paste a valid URL starting with http.");
                                }

                            } else if (ch.equals("V")) {
                                String loc = routeTable.locations[slot];
                                if (loc != null) {
                                    System.out.println("\n  Current live location: " + loc);
                                    System.out.print("  Open in browser? (Y/N): ");
                                    String open = sc.nextLine().trim().toUpperCase();
                                    if (open.equals("Y")) {
                                        System.out.println("  Opening: " + loc);
                                        try {
                                            java.awt.Desktop.getDesktop().browse(new java.net.URI(loc));
                                            System.out.println("  \u2713 Launched in browser.");
                                        } catch (Exception ex) {
                                            System.out.println("  (Cannot open browser in this environment)");
                                            System.out.println("  Copy the URL above to open manually.");
                                        }
                                    }
                                } else {
                                    System.out.println("  \u26a0 No location set yet. Use [U] to update.");
                                }

                            } else if (ch.equals("B")) {
                                dashboard = false;
                            } else {
                                System.out.println("  Invalid choice.");
                            }
                        }

                    } else {
                        System.out.println("  \u2717 Invalid credentials. Try bus1@klh.com / bus1pass");
                    }
                    break;
                }

                // ────────────────────────────────────────────
                // OPTION 6 — EDIT STOPS + UNDO (DLL + STACK)
                // ────────────────────────────────────────────
                case "6": {
                    sectionHeader("Edit Bus Stops");
                    if (loggedInBus == null) {
                        System.out.println("  \u2717 Please log in first (Option 5).");
                        break;
                    }
                    int busNum = Integer.parseInt(loggedInBus.replace("bus", ""));
                    int slot   = routeTable.search(busNum);

                    DoublyLinkedList dll = new DoublyLinkedList();
                    for (String s : routeTable.stops[slot]) dll.append(s);

                    boolean editing = true;
                    while (editing) {
                        System.out.println("\n  Current stops for " + loggedInBus.toUpperCase() + ":");
                        dll.printForward();
                        System.out.println("\n  [A] Add stop   [R] Remove stop   [U] Undo   [S] Save & Exit");
                        System.out.print("  Choice: ");
                        String choice = sc.nextLine().trim().toUpperCase();

                        switch (choice) {
                            case "A": {
                                System.out.print("  After which stop number? (0 = before first): ");
                                int idx = Integer.parseInt(sc.nextLine().trim());
                                System.out.print("  New stop name: ");
                                String stopName = sc.nextLine().trim();
                                undoStack.push(dll.toArray());
                                dll.insertAfter(idx - 1, stopName);
                                System.out.println("  \u2713 Stop added.");
                                break;
                            }
                            case "R": {
                                System.out.print("  Remove stop number: ");
                                int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
                                undoStack.push(dll.toArray());
                                dll.removeAt(idx);
                                System.out.println("  \u2713 Stop removed.");
                                break;
                            }
                            case "U": {
                                String[] prev = undoStack.pop();
                                if (prev == null) {
                                    System.out.println("  \u2717 Nothing to undo.");
                                } else {
                                    dll = new DoublyLinkedList();
                                    for (String s : prev) dll.append(s);
                                    System.out.println("  \u2713 Undo successful.");
                                }
                                break;
                            }
                            case "S": {
                                routeTable.stops[slot] = dll.toArray();
                                System.out.println("  \u2713 Stop list saved!");
                                editing = false;
                                break;
                            }
                            default:
                                System.out.println("  Invalid choice.");
                        }
                    }
                    break;
                }

                // ────────────────────────────────────────────
                // OPTION 7 — HASH TABLE STRUCTURE
                // ────────────────────────────────────────────
                case "7": {
                    sectionHeader("Hash Table Structure");
                    System.out.println("\n  [A] Route Hash Table (Linear Probing, size=31)");
                    System.out.println("  [B] Credential Hash Table (Separate Chaining, size=13)");
                    System.out.print("  Choice: ");
                    String choice = sc.nextLine().trim().toUpperCase();
                    if (choice.equals("A")) {
                        routeTable.printTable();
                    } else if (choice.equals("B")) {
                        credTable.printChains();
                    }
                    break;
                }

                // ────────────────────────────────────────────
                // OPTION 8 — SORT DEMO
                // ────────────────────────────────────────────
                case "8": {
                    sectionHeader("Sort Demo");

                    System.out.println("\n  \u25ba Bubble Sort \u2014 Route names A\u2192Z:");
                    String[] names = {"Tolichowki", "Alwal", "Secunderabad", "Madhapur", "Kompally"};
                    System.out.println("  Before: " + java.util.Arrays.toString(names));
                    bubbleSort(names);
                    System.out.println("  After : " + java.util.Arrays.toString(names));

                    System.out.println("\n  \u25ba Insertion Sort \u2014 Bus numbers ascending:");
                    int[] sample = {15, 3, 22, 7, 19, 1, 11};
                    System.out.println("  Before: " + java.util.Arrays.toString(sample));
                    insertionSort(sample);
                    System.out.println("  After : " + java.util.Arrays.toString(sample));
                    break;
                }

                case "0":
                    System.out.println("\n  Goodbye! \u2014 KLH Transport Tracker");
                    running = false;
                    break;

                default:
                    System.out.println("  \u2717 Invalid option. Please try again.");
            }

            if (running) {
                System.out.print("\n  Press Enter to continue...");
                sc.nextLine();
            }
        }

        sc.close();
    }
}