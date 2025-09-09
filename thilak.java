public class User {
protected int id;
protected String name; protected String email; protected String role;

public User(int id, String name, String email, String role) {
this.id = id;
this.name = name; this.email = email; this.role = role;
}


public int getId() { return id; }
public void setId(int id) { this.id = id; }


public String getName() { return name; }
public void setName(String name) { this.name = name; }


public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }


public String getRole() { return role; }
public void setRole(String role) { this.role = role; }


public void display() {
System.out.println("User ID: " + id + " | Name: " + name + " | Role: " + role);
 
}
}


package model;


public class Agent extends User { private int workloadLimit; private int ticketsAssigned;

public Agent(int id, String name, String email, int workloadLimit) {
super(id, name, email, "Agent"); this.workloadLimit = workloadLimit; this.ticketsAssigned = 0;
}


public int getWorkloadLimit() { return workloadLimit; }
public void setWorkloadLimit(int workloadLimit) { this.workloadLimit = workloadLimit; }


public int getTicketsAssigned() { return ticketsAssigned; }
public void assignTicket() { this.ticketsAssigned++; }


@Override
public void display() {
System.out.println("Agent ID: " + id + " | Name: " + name + " | Assigned Tickets: " + ticketsAssigned +
"/" + workloadLimit);
}
}
 
package model;


public class Category { private int id; private String name; private int slaHours;
private String escalationRules;


public Category(int id, String name, int slaHours, String escalationRules) {
this.id = id;
this.name = name;
this.slaHours = slaHours;
this.escalationRules = escalationRules;
}


public int getId() { return id; }
public String getName() { return name; }
public int getSlaHours() { return slaHours; }
public String getEscalationRules() { return escalationRules; }


public void display() {
System.out.println("Category: " + name + " | SLA: " + slaHours + " hrs | Rules: " + escalationRules);
}
}




package model;
 
public class Ticket { private int ticketId; private String subject;
private String description; private String priority; private String status; private Agent assignee; private Category category;

public Ticket(int ticketId, String subject, String description, String priority) {
this.ticketId = ticketId; this.subject = subject; this.description = description; this.priority = priority; this.status = "NEW";
}


public Ticket(int ticketId, String subject, String description, String priority, Category category) {
this(ticketId, subject, description, priority);
this.category = category;
}


public int getTicketId() { return ticketId; }
public String getSubject() { return subject; }
public String getDescription() { return description; }
public String getPriority() { return priority; }
 
public String getStatus() { return status; }
public Agent getAssignee() { return assignee; }


public void assignAgent(Agent agent) {
if(agent.getTicketsAssigned() < agent.getWorkloadLimit()) {
this.assignee = agent; agent.assignTicket();
} else {
System.out.println("Agent " + agent.getName() + " has reached workload limit!");
}
}


public void changeStatus(String newStatus) {
if ((status.equals("NEW") && newStatus.equals("OPEN")) || (status.equals("OPEN") && (newStatus.equals("PENDING") ||
newStatus.equals("RESOLVED"))) ||
(status.equals("RESOLVED") && newStatus.equals("CLOSED"))) {
this.status = newStatus;
} else {
System.out.println("Invalid status transition!");
}
}


@Override
public String toString() {
return "Ticket #" + ticketId + " | Subject: " + subject + " | Priority: " + priority + " | Status: " + status +
(assignee != null ? " | Agent: " + assignee.getName() : " | Unassigned");
 
}
}




package service;


import model.*;
import java.util.*;


public class HelpdeskService {
private List<User> users = new ArrayList<>(); private List<Agent> agents = new ArrayList<>(); private List<Ticket> tickets = new ArrayList<>();
private List<Category> categories = new ArrayList<>();


public void registerUser(User user) { users.add(user);
}


public void registerAgent(Agent agent) { agents.add(agent);
}


public void addCategory(Category category) { categories.add(category);
}


public Ticket createTicket(Ticket ticket) {
 
tickets.add(ticket);
return ticket;
}


public void assignTicket(int ticketId, int agentId) { Ticket ticket = findTicket(ticketId);
Agent agent = findAgent(agentId);
if(ticket != null && agent != null) { ticket.assignAgent(agent);
}
}


public void changeTicketStatus(int ticketId, String status) { Ticket ticket = findTicket(ticketId);
if(ticket != null) ticket.changeStatus(status);
}


public void listTicketsByPriority(String priority) { tickets.stream()
.filter(t -> t.getPriority().equalsIgnoreCase(priority))
.forEach(System.out::println);
}


public void dashboard() { System.out.println("\n=== Dashboard ==="); for(Agent agent : agents) {
long count = tickets.stream()
.filter(t -> t.getAssignee() == agent)
 
.count();
System.out.println(agent.getName() + " → " + count + " tickets");
}
}


private Ticket findTicket(int id) {
return tickets.stream().filter(t -> t.getTicketId() == id).findFirst().orElse(null);
}


private Agent findAgent(int id) {
return agents.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
}
}


package main;


import model.*;
import service.HelpdeskService;


public class HelpdeskAppMain {
public static void main(String[] args) {
HelpdeskService service = new HelpdeskService();


Agent a1 = new Agent(1, "Arun", "arun@helpdesk.com", 3); Agent a2 = new Agent(2, "Priya", "priya@helpdesk.com", 2);

service.registerAgent(a1); service.registerAgent(a2);
 
Category c1 = new Category(1, "Network", 24, "Escalate after 24 hrs"); Category c2 = new Category(2, "Software", 48, "Escalate after 48 hrs");

service.addCategory(c1); service.addCategory(c2);

Ticket t1 = service.createTicket(new Ticket(101, "Internet Down", "No connectivity", "High", c1));
Ticket t2 = service.createTicket(new Ticket(102, "App Crash", "App closes suddenly", "Medium", c2));
Ticket t3 = service.createTicket(new Ticket(103, "Email Issue", "Can't send emails", "Low", c1));


service.assignTicket(101, 1);
service.assignTicket(102, 2);


service.changeTicketStatus(101, "OPEN");
service.changeTicketStatus(101, "RESOLVED"); service.changeTicketStatus(101, "CLOSED");

System.out.println("\nTickets by Priority: High"); service.listTicketsByPriority("High");

service.dashboard();
}
}
